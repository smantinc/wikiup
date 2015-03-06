package org.wikiup.servlet.inf;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.servlet.ServletProcessorContext;

import java.io.StringWriter;

public interface TagProcessor {
    public void process(ServletProcessorContext context, TagProcessor parent, String body, Dictionary<?> parameters, StringWriter writer);
}
