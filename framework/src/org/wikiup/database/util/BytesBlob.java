package org.wikiup.database.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BytesBlob implements Blob {
    private byte[] bytes;
    private ResultSet result = null;
    private String fieldName = null;
    private String content = null;
    private String charset;

    public BytesBlob(byte[] data, String charset) {
        this.bytes = data;
        this.charset = charset;
    }

    public BytesBlob(ResultSet result, String fieldName, byte[] data, String charset) {
        this.result = result;
        this.fieldName = fieldName;
        this.bytes = data;
        this.charset = charset;
    }

    public long length() throws SQLException {
        return bytes.length;
    }

    public byte[] getBytes(long pos, int length) throws SQLException {
        return bytes;
    }

    public InputStream getBinaryStream() throws SQLException {
        return new ByteArrayInputStream(bytes);
    }

    public InputStream getBinaryStream(long offset, long len) throws SQLException {
        return new ByteArrayInputStream(bytes, (int) offset, (int) len);
    }

    public void free() {
        bytes = null;
    }

    public long position(byte pattern[], long start) throws SQLException {
        return 0;
    }

    public long position(Blob pattern, long start) throws SQLException {
        return 0;
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException {
        if(pos == 0 && result != null && fieldName != null)
            result.updateObject(fieldName, bytes);
        return 0;
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        return 0;
    }

    public OutputStream setBinaryStream(long pos) throws SQLException {
        return new ByteArrayOutputStream();
    }

    public void truncate(long len) throws SQLException {
    }

    @Override
    public String toString() {
        try {
            return content == null ? (content = new String(bytes, charset)) : content;
        } catch(UnsupportedEncodingException ex) {
            return new String(bytes);
        }
    }
}
