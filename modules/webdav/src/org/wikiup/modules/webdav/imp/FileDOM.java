package org.wikiup.modules.webdav.imp;

import org.wikiup.core.impl.document.DocumentImpl;
import org.wikiup.core.impl.document.DocumentWrapper;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.webdav.inf.FilePropertyInf;

import java.util.Locale;

public class FileDOM extends DocumentWrapper {
    private FilePropertyInf fileAttribute;

    public FileDOM(FilePropertyInf attribute) {
        super(new DocumentImpl("prop"));
        fileAttribute = attribute;
    }

    public boolean prepareProperty(String name) {
        if(name.equals("creationdate"))
            return setFileAttribute(name, ValueUtil.getFormattedDate(fileAttribute.getCreationDate(), "yyyy-MM-dd'T'HH:mm:ss'Z'"));
        if(name.equals("getlastmodified"))
            return setFileAttribute(name, ValueUtil.getFormattedDate(fileAttribute.getLastModifiedDate(), "EEE, dd MMM yyyy HH:mm:ss z", Locale.US));
        else if(name.equals("getcontentlength"))
            return setFileAttribute(name, String.valueOf(fileAttribute.getContentLength()));
        if(name.equals("displayname"))
            return setFileAttribute(name, fileAttribute.getDisplayName());
        if(name.equals("resourcetype")) {
            if(fileAttribute.isCollection())
                getDocument().addChild("D:" + name).addChild("D:collection");
            return true;
        }
        return false;
    }

    public void prepareAllProperty() {
        prepareProperty("creationdate");
        prepareProperty("getlastmodified");
        prepareProperty("getcontentlength");
        prepareProperty("displayname");
        prepareProperty("resourcetype");
    }

    public boolean setFileAttribute(String name, String value) {
        Documents.setChildValue(getDocument(), "D:" + name, value);
        return true;
    }

    public FilePropertyInf getFileAttribute() {
        return fileAttribute;
    }

}
