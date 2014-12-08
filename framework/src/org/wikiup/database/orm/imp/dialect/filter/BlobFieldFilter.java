package org.wikiup.database.orm.imp.dialect.filter;

import org.wikiup.core.inf.Translator;
import org.wikiup.database.util.BytesBlob;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;

public class BlobFieldFilter implements Translator<Object, Blob> {
    public Blob translate(Object object) {
        byte[] bytes = null;
        if(object instanceof byte[])
            bytes = (byte[]) object;
        else if(object instanceof String) {
            try {
                bytes = ((String) object).getBytes("utf-8");
            } catch(UnsupportedEncodingException e) {
            }
        }
        return bytes != null ? new BytesBlob(bytes, "utf-8") : null;
    }
}
