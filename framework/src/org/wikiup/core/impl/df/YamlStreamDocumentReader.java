package org.wikiup.core.impl.df;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.DocumentReader;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

public class YamlStreamDocumentReader implements DocumentReader<InputStream> {
    private int indentBlockSize = 0;
    private char indentChar = 0;

    private Stack<ParsingContextNode> yamlStack = new Stack<ParsingContextNode>();

    private DocumentImpl root = new DocumentImpl("root");

    private ParsingContext mapping = new MapParsingContext();
    private ParsingContext sequence = new SequenceParsingContext();

    private ParsingContext defaultContext = new DefaultParsingContext();

    public Document translate(InputStream stream) {
        BufferedReader reader = null;
        push(root);
        try {
            reader = new BufferedReader(new InputStreamReader(stream, WikiupConfigure.CHAR_SET));
            while(!yamlStack.isEmpty()) {
                String line = parseLine(reader.readLine());
                if(line == null)
                    break;
                parse(line);
            }
        } catch(IOException e) {
        } finally {
            StreamUtil.close(reader);
        }
        return root;
    }

    public void parse(String line) {
        int depth = indentCount(line);
        ParsingContextNode node = setStack(depth);
        line = StringUtil.trim(line);
        if(StringUtil.isNotEmpty(line))
            node.parse(line);
    }

    private int getIndent() {
        return yamlStack.size() - 1;
    }

    private ParsingContextNode setStack(int depth) {
        int indent = getIndent();
        if(depth > indent) {
            while(depth > indent) {
                indent++;
                push(null);
            }
        } else if(depth < indent) {
            while(depth < indent) {
                indent--;
                yamlStack.pop();
            }
        }
        return yamlStack.get(depth);
    }

    private String parseLine(String line) {
        return line != null ? StringUtil.first(line, '#', 0) : null;
    }

    private int indentCount(String line) {
        int i, size = line.length();
        int count = 0;
        char[] chars = line.toCharArray();
        for(i = 0; i < size; i++)
            switch(chars[i]) {
                case '\t':
                case ' ':
                    if(indentChar == 0)
                        indentChar = chars[i];
                    count += 1;
                    break;
                default:
                    if(indentBlockSize == 0)
                        indentBlockSize = count;
                    return indentBlockSize != 0 ? count / indentBlockSize : 0;
            }
        return indentBlockSize != 0 ? count / indentBlockSize : 0;
    }

    private ParsingContextNode push(DocumentImpl p) {
        DocumentImpl parent = p != null ? p : yamlStack.peek().parent.addChild("item");
        ParsingContextNode node = new ParsingContextNode(defaultContext, parent);
        yamlStack.push(node);
        return node;
    }

    static private class ParsingContextNode implements ParsingContext {
        public ParsingContext context;
        public DocumentImpl parent;

        public ParsingContextNode(ParsingContext context, DocumentImpl parent) {
            this.context = context;
            this.parent = parent;
        }

        public void parse(String line) {
            context.parse(parent, line);
        }

        public void parse(DocumentImpl doc, String line) {
            context.parse(doc, line);
        }
    }

    private class DefaultParsingContext implements ParsingContext {
        public void parse(DocumentImpl doc, String line) {
            String value = StringUtil.trim(line);
            if(StringUtil.isNotEmpty(value)) {
                if(value.charAt(0) == '-')
                    sequence.parse(doc, line);
                else
                    mapping.parse(doc, line);
            }
        }
    }

    private class SequenceParsingContext implements ParsingContext {
        public void parse(DocumentImpl doc, String line) {
            String value = StringUtil.trim(line, " -\t");
            push(doc.addChild("sequence")).parse(value);
        }
    }

    private class MapParsingContext implements ParsingContext {
        public void parse(DocumentImpl doc, String line) {
            StringUtil.StringPair pair = StringUtil.pair(line, ':', 0);
            if(pair != null) {
                String key = StringUtil.trim(pair.first);
                String value = StringUtil.trim(pair.second);
                if(StringUtil.isNotEmpty(value)) {
                    Document attr = Documents.setChildValue(doc, key, value);
                    doc.addAttribute(key, attr);
                } else
                    push(doc.addChild(key));
            } else {
                String value = StringUtil.trim(line);
                if(StringUtil.isNotEmpty(value))
                    doc.setObject(value);
            }
        }
    }

    private interface ParsingContext {
        public void parse(DocumentImpl doc, String line);
    }
}
