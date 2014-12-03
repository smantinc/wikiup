package org.wikiup.modules.xslt;

import org.wikiup.core.util.FileUtil;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TransformerCache {
    private static TransformerCache instance = null;

    private Map<String, FileCacheEntry> entries = new HashMap<String, FileCacheEntry>();

    public static TransformerCache getInstance() {
        return instance != null ? instance : (instance = new TransformerCache());
    }

    public Transformer getTransformer(String uri) throws TransformerConfigurationException, TransformerFactoryConfigurationError {
        FileCacheEntry cacheEntry = entries.get(uri);
        if(cacheEntry == null)
            entries.put(uri, (cacheEntry = new FileCacheEntry(FileUtil.getFile(uri))));
        return cacheEntry.getTransformer();
    }

    static private class FileCacheEntry {
        private File file;
        private long lastModified;
        private Transformer transformer;

        public FileCacheEntry(File file) {
            this.file = file;
        }

        public Transformer getTransformer() throws TransformerFactoryConfigurationError, TransformerConfigurationException {
            if(transformer == null || FileUtil.isUpToDate(file, lastModified)) {
                transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(file));
                lastModified = file.lastModified();
            }
            return transformer;
        }
    }
}
