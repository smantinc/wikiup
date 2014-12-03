package org.wikiup.core.impl.df;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.DocumentReader;
import org.xml.sax.InputSource;

import java.io.StringReader;

public class StringDocumentReader extends AbstractDocumentReader implements DocumentReader<String> {
    public Document filter(String xml) {
        return parse(new InputSource(new StringReader(xml)));
    }
}
