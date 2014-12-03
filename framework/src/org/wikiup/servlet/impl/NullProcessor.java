package org.wikiup.servlet.impl;

import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;

public class NullProcessor implements ServletProcessor {
    static private NullProcessor instance = null;

    public static ServletProcessor getInstance() {
        return (instance == null) ? (instance = new NullProcessor()) : instance;
    }

    public void process(ServletProcessorContext context) {
    }
}
