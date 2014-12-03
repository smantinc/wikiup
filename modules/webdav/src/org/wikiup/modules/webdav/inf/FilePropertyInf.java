package org.wikiup.modules.webdav.inf;

import org.wikiup.core.inf.Document;

import java.util.Date;
import java.util.Iterator;


public interface FilePropertyInf {
    public Date getCreationDate();

    public Date getLastModifiedDate();

    public String getDisplayName();

    public String getContentType();

    public String getContentLanguage();

    public long getContentLength();

    public boolean isCollection();

    public String getHref(String parent);

    public Document getExtraAttribute(String name);

    public Iterator getExtraAttributes();
}
