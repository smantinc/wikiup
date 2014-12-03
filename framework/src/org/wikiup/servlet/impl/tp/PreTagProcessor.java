package org.wikiup.servlet.impl.tp;

import org.wikiup.core.inf.Getter;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.TagProcessor;

import java.io.StringWriter;

public class PreTagProcessor implements TagProcessor {
    public void process(ServletProcessorContext context, TagProcessor parent, String body, Getter<?> parameters, StringWriter writer) {
        writer.write(body);
    }
}
