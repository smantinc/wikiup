package org.wikiup.database.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class FileBlob implements Blob {
    private File file;

    public FileBlob(File file) {
        this.file = file;
    }

    public long length() throws SQLException {
        return file.length();
    }

    public byte[] getBytes(long pos, int length) throws SQLException {
        byte bytes[] = new byte[length];
        try {
            FileInputStream is = new FileInputStream(file);
            is.skip(pos - 1);
            is.read(bytes);
            is.close();
        } catch(IOException ex) {
        }
        return bytes;
    }

    public InputStream getBinaryStream() throws SQLException {
        try {
            return new FileInputStream(file);
        } catch(IOException ex) {
            return null;
        }
    }

    public InputStream getBinaryStream(long offset, long len) throws SQLException {
        try {
            FileInputStream is = new FileInputStream(file);
            is.skip(offset);
            return is;
        } catch(IOException ex) {
            return null;
        }
    }

    public void free() {
        file = null;
    }

    public long position(byte pattern[], long start) throws SQLException {
        return 0;
    }

    public long position(Blob pattern, long start) throws SQLException {
        return 0;
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException {
        return 0;
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
        return 0;
    }

    public OutputStream setBinaryStream(long pos) throws SQLException {
        try {
            return new FileOutputStream(file);
        } catch(FileNotFoundException ex) {
            return null;
        }
    }

    public void truncate(long len) throws SQLException {
    }
}
