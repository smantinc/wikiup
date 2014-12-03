package org.wikiup.servlet.inf;

import org.wikiup.core.inf.Getter;
import org.wikiup.servlet.ServletProcessorContext;

import java.io.StringWriter;

public interface TagProcessor {
    public void process(ServletProcessorContext context, TagProcessor parent, String body, Getter<?> parameters, StringWriter writer);
}
