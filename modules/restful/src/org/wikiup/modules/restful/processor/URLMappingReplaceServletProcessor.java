package org.wikiup.modules.restful.processor;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;

import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLMappingReplaceServletProcessor implements ServletProcessor, DocumentAware {
    private Document configure;

    public void process(ServletProcessorContext context) {
        StringWriter writer = context.getResponseWriter();
        String html = context.getResponseText();
        for(Document node : configure.getChildren())
            html = doReplace(html, node);
        writer.write(html);
    }

    public void aware(Document desc) {
        configure = desc;
    }

    private String doReplace(String html, Document node) {
        Pattern pattern = Pattern.compile(Documents.getDocumentValue(node, "regexp"));
        String replacement = Documents.getDocumentValue(node, "replacement");
        Matcher matcher = pattern.matcher(html);
        StringBuilder buffer = new StringBuilder();
        Getter<String> getter = new RegexpMatcherGroupGetter(matcher);
        while(matcher.find()) {
            String rep = StringUtil.evaluateEL(replacement, getter);
            matcher.appendReplacement(buffer, rep);
        }
        matcher.appendTail(buffer);
        return buffer.toString();

    }

    private static class RegexpMatcherGroupGetter implements Getter<String> {
        private Matcher matcher;

        public RegexpMatcherGroupGetter(Matcher matcher) {
            this.matcher = matcher;
        }

        public String get(String name) {
            int i = ValueUtil.toInteger(name, -1);
            return i != -1 ? matcher.group(i) : null;
        }
    }
}
