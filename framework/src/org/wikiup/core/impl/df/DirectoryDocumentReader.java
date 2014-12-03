package org.wikiup.core.impl.df;

import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.DocumentReader;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;

import java.io.File;

public class DirectoryDocumentReader implements DocumentReader<File> {
    public Document filter(File directory) {
        DocumentImpl data = new DocumentImpl(directory.getName());
        loadDirectoryFiles(data, directory);
        return data;
    }

    private void loadDirectoryFiles(Document doc, File dir) {
        File files[] = dir.listFiles();
        FileDocumentReader fs = new FileDocumentReader();
        int i;
        for(i = 0; i < files.length; i++)
            if(files[i].isDirectory())
                loadDirectoryFiles(doc.addChild(files[i].getName()), files[i]);
            else if(FileUtil.getFileExt(files[i].getName()).equalsIgnoreCase("xml"))
                Documents.merge(doc, fs.filter(files[i]));
    }
}
