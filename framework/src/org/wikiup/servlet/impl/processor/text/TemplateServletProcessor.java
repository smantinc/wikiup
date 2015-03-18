package org.wikiup.servlet.impl.processor.text;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.exception.CloseMarkupNotFoundException;
import org.wikiup.servlet.impl.rl.ResponseBufferResourceHandler;
import org.wikiup.servlet.inf.TagProcessor;
import org.wikiup.servlet.inf.ext.ResourceHandler;
import org.wikiup.servlet.util.ProcessorContexts;

public class TemplateServletProcessor extends ResponseBufferResourceHandler implements ResourceHandler, TagProcessor {
    final private static Pattern WIKIUP_PATTERN = Pattern.compile("<([^\\s:>]+:[^/\\s>]+)\\s*");
    final private static String[] WNDI_TAG_PROCESSORS = {"wk", "servlet", "tag"};

    private Dictionary<TagProcessor> tags;

    public void process(ServletProcessorContext context) {
        StringWriter writer = context.getResponseWriter();
        this.tags = Wikiup.getInstance().get(Dictionary.class, WNDI_TAG_PROCESSORS);
        process(context, this, context.getResponseText(), null, writer);
    }

    public void process(ServletProcessorContext context, TagProcessor parent, String text, Dictionary<?> p, StringWriter writer) {
        int offset = 0;
        Matcher matcher = WIKIUP_PATTERN.matcher(text);
        while(matcher.find(offset)) {
            String markupName = matcher.group(1);
            String name = StringUtil.shrinkLeft(markupName, "wk:");
            writer.write(StringUtil.evaluateEL(text.substring(offset, matcher.start()), context));
            offset = searchToken(text, matcher.start(), '>');
            boolean closed = text.charAt(offset - 1) == '/';
            Dictionary<?> params = getContextParameters(context, text.substring(matcher.end(), closed ? offset - 1 : offset));
            try {
                if(closed) {
                    offset++;
                    if(!doTagProcess(context, name, null, params, writer))
                        writer.write(getStringFromModelContainer(context, name, params));
                } else {
                    int end = searchMarkupClosePosition(text, matcher.end(), markupName);
                    Assert.isTrue(end != -1, CloseMarkupNotFoundException.class, name);
                    String body = text.substring(offset + 1, end);
                    offset = text.indexOf('>', end) + 1;
                    if(!doTagProcess(context, name, body, params, writer)) {
                        ServletProcessorContext.BeanStack beanStack = context.getBeanStack();
                        Object ctx = ProcessorContexts.get(context, StringUtil.evaluateEL(name, context), params);
                        if(ctx != null) {
                            beanStack.push(ctx);
                            if(ValueUtil.toBoolean(beanStack.peek(Boolean.class), true)) {
                                String html = beanStack.peek(String.class);
                                process(context, parent, html != null ? html : body, params, writer);
                            }
                            beanStack.pop();
                        }
                    }
                }
            } catch(Exception ex) {
                if(!context.handle(ex))
                    Assert.fail(ex);
            }
        }
        writer.write(StringUtil.evaluateEL(text.substring(offset), context));
    }

    private boolean doTagProcess(ServletProcessorContext context, String name, String body, Dictionary<?> params, StringWriter writer) {
        TagProcessor tag = tags.get(name);
        if(tag != null)
            tag.process(context, this, body, params, writer);
        return tag != null;
    }

    private String getStringFromModelContainer(ServletProcessorContext context, String name, Dictionary<?> params) {
        BeanContainer modelProvider = ProcessorContexts.getBeanContainer(context, name, params);
        Object value = modelProvider != null ? modelProvider.query(String.class) : null;
        return ValueUtil.toString(value, "");
    }

    private Dictionary<?> getContextParameters(final Context<?, ?> context, String str) {
        Dictionary<?> ret = Null.getInstance();
        if(!StringUtil.isEmpty(str)) {
            final HashMap<String, String> param = new HashMap<String, String>();
            String params[] = StringUtil.separate(str, "[^=\\s]+=(['\"]{1})[^'\"]*\\1");
            for(String line : params)
                Dictionaries.parseNameValuePair(new Dictionary.Mutable<String>() {
                    public void set(String name, String value) {
                        param.put(StringUtil.trim(name, " \t"), dequote(value));
                    }

                    private String dequote(String str) {
                        char q = str.charAt(0);
                        if(q == '\'' || q == '"')
                            if(str.charAt(str.length() - 1) == q)
                                return str.substring(1, str.length() - 1);
                        return str;
                    }
                }, line, '=');
            ret = new Dictionary<String>() {
                public String get(String name) {
                    return StringUtil.evaluateEL(param.get(name), context);
                }
            };
        }
        return ret;
    }

    private int searchToken(String text, int offset, char token) {
        int pos = offset, len = text.length();
        final char bracketChar = '"';
        boolean out = true;
        while(pos < len) {
            if(text.charAt(pos) == bracketChar)
                out = !out;
            else if(text.charAt(pos) == token && out)
                break;
            pos++;
        }
        return pos < len ? pos : -1;
    }

    private int searchMarkupClosePosition(String text, int offset, String markup) {
        int pos = 0, depth = 1;
        final int len = markup.length();
        while(depth > 0) {
            pos = text.indexOf(markup, pos == 0 ? offset : pos + len);
            if(pos == -1)
                break;
            if("> ".indexOf(text.charAt(pos + len)) != -1)
                if(text.charAt(pos - 1) == '<')
                    depth++;
                else if(text.substring(pos - 2, pos).equals("</"))
                    depth--;
        }
        return pos != -1 ? pos - 2 : -1;
    }
}
