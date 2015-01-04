package org.wikiup.core.impl.df;

import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.DocumentReader;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;

import java.io.File;

public class DirectoryDocumentReader implements DocumentReader<File> {
    public Document translate(File directory) {
        DocumentImpl data = new DocumentImpl(directory.getName());
        loadDirectoryFiles(data, directory);
        return data;
    }

    private void loadDirectoryFiles(Document doc, File dir) {
        File files[] = dir.listFiles();
        FileDocumentReader fs = new FileDocumentReader();
        if(files != null)
            for(File file : files)
                if(file.isDirectory())
                    loadDirectoryFiles(doc.addChild(file.getName()), file);
                else if(FileUtil.getFileExt(file.getName()).equalsIgnoreCase("xml"))
                    Documents.merge(doc, fs.translate(file));
    }
}
