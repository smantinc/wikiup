package org.wikiup.servlet.impl.tp;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.TagProcessor;

import java.io.StringWriter;

public class PreTagProcessor implements TagProcessor {
    public void process(ServletProcessorContext context, TagProcessor parent, String body, Dictionary<?> parameters, StringWriter writer) {
        writer.write(body);
    }
}
