package org.wikiup.core.impl.df;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.DocumentReader;
import org.xml.sax.InputSource;

import java.io.InputStream;

public class XmlStreamDocumentReader extends AbstractDocumentReader implements DocumentReader<InputStream> {
    public Document filter(InputStream input) {
        return parse(new InputSource(input));
    }
}
