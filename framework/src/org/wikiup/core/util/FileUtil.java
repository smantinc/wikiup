package org.wikiup.core.util;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.inf.ExceptionHandler;
import org.wikiup.core.inf.ext.LogicalFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtil {
    static public String getFilePath(String file) {
        return getFilePath(file, File.separatorChar);
    }

    static public String getFilePath(String file, char separator) {
        int pos = file.lastIndexOf(separator);
        return pos != -1 ? file.substring(0, pos) : "";
    }

    static public String getFileExt(String file) {
        int pos = file.lastIndexOf('.');
        return pos != -1 ? file.substring(pos + 1) : "";
    }

    static public String getFileName(String file, char separator) {
        int epos = file.lastIndexOf('.');
        int ppos = file.lastIndexOf(separator);
        return epos != -1 && epos > ppos ? file.substring(ppos != -1 ? ppos + 1 : 0, epos) :
                file.substring(ppos != -1 ? ppos + 1 : 0);
    }

    static public String getFileName(String file) {
        return getFileName(file, File.separatorChar);
    }

    static public String getFileNameExt(String file) {
        int ppos = getSeparatorPosition(file);
        return file.substring(ppos != -1 ? ppos + 1 : 0);
    }

    static public void copy(File source, File target) throws IOException {
        if(source.isFile() && (target.isFile() || !target.exists()))
            copyFile(source, target);
        else if(source.isFile() && target.isDirectory())
            copyFileToDirectory(source, target);
        else if(source.isDirectory())
            copyDirectoryToDirectory(source, target);
    }

    static public void copyFile(File source, File dest) throws FileNotFoundException {
        copyFile(source, dest, null);
    }

    static public void copyFile(File source, File dest, ExceptionHandler eh) throws FileNotFoundException {
        OutputStream os = null;
        InputStream is = null;
        try {
            os = new FileOutputStream(dest);
            is = new FileInputStream(source);
            StreamUtil.copy(os, is, eh);
        } finally {
            StreamUtil.close(os, eh);
            StreamUtil.close(is, eh);
        }
    }

    static public void copyFileToDirectory(File source, File dest) throws IOException {
        copyFile(source, new File(dest, source.getName()));
    }

    static public void copyDirectoryToDirectory(File source, File dest) throws IOException {
        File files[] = source.listFiles();
        int i;
        dest.mkdirs();
        for(i = 0; i < files.length; i++)
            copy(files[i], new File(dest, getFileNameExt(files[i].getName())));
    }

    static public String joinPath(String path, String file, char separator) {
        return StringUtil.connect(path, file, separator);
    }

    static public String joinPath(String path, String file) {
        return StringUtil.connect(path, file, File.separatorChar);
    }

    static public boolean isDirectory(String fileName) {
        File file = new File(fileName);
        return file.isDirectory();
    }

    static public boolean isExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    static public File getFile(String path) {
        return new File(path);
    }

    static public File getFile(String path, String name) {
        return new File(path, name);
    }

    static public File touch(String filename) throws IOException {
        return touch(getFile(filename));
    }

    static public File touch(File file) throws IOException {
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        return file;
    }

    static public boolean delete(File file) {
        if(file.isDirectory()) {
            File files[] = file.listFiles();
            int loop;
            for(loop = 0; loop < files.length; loop++)
                if(!delete(files[loop]))
                    return false;
        }
        return file.delete();
    }

    static public boolean isUpToDate(File file, long date) {
        return file.lastModified() >= date;
    }

    static public boolean isHidden(File file) {
        return file.getName().charAt(0) == '.' || file.isHidden();
    }

    static public void unzip(InputStream is, File directory, LogicalFilter<String> filter) throws IOException {
        byte buffer[] = new byte[16384];
        ZipInputStream zis = null;
        try {
            ZipEntry entry;
            zis = new ZipInputStream(is);
            while((entry = zis.getNextEntry()) != null) {
                String name = entry.getName();
                if(filter == null || filter.translate(name))
                    if(entry.isDirectory())
                        new File(directory, name).mkdirs();
                    else
                        unzip(buffer, directory, zis, entry);
            }
        } finally {
            if(zis != null)
                zis.close();
        }
    }

    static public void unzip(File file, File directory) throws IOException {
        unzip(file, directory, null);
    }

    static public void unzip(File file, File directory, LogicalFilter<String> filter) throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            unzip(is, directory, filter);
        } finally {
            is.close();
        }
    }

    static public String decodeFileName(String fileName) {
        try {
            return URLDecoder.decode(fileName, WikiupConfigure.CHAR_SET);
        } catch(UnsupportedEncodingException e) {
            return fileName;
        }
    }

    static public String getSystemFilePath(String path) {
        return StringUtil.trimRight(StringUtil.replaceAll(path, File.separatorChar == '/' ? '\\' : '/', File.separatorChar), "/\\");
    }

    private static void unzip(byte[] buffer, File directory, ZipInputStream zis, ZipEntry entry) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(directory, entry.getName()), false);
        try {
            int size;
            while((size = zis.read(buffer, 0, buffer.length)) != -1)
                fos.write(buffer, 0, size);
        } finally {
            fos.close();
        }
    }

    private static int getSeparatorPosition(String fileName) {
        int pos = fileName.lastIndexOf('\\');
        return pos != -1 ? pos : fileName.lastIndexOf('/');
    }
}
