package org.wikiup.servlet.impl.processor;

import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.exception.ServiceNotImplementException;
import org.wikiup.servlet.inf.ServletProcessor;

public class DefaultServletProcessor implements ServletProcessor {
    public void process(ServletProcessorContext context) {
        throw new ServiceNotImplementException();
    }
}
