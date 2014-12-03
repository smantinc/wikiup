package org.wikiup.servlet.impl.tp;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.TagProcessor;

import java.io.StringWriter;


public class IfTagProcessor implements TagProcessor {
    public void process(ServletProcessorContext context, TagProcessor parent, String body, Getter<?> parameters, StringWriter writer) {
        if(StringUtil.test(parameters.get("test")))
            parent.process(context, parent, body, parameters, writer);
    }


}
