package org.wikiup.core.impl.df;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.DocumentReader;
import org.wikiup.core.util.StreamUtil;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileDocumentReader extends AbstractDocumentReader implements DocumentReader<File> {
    public Document filter(File file) {
        InputStream stream = null;
        Document doc = null;
        try {
            stream = new FileInputStream(file);
            doc = parse(new InputSource(stream));
        } catch(FileNotFoundException e) {
        } finally {
            StreamUtil.close(stream);
        }
        return doc;
    }
}
