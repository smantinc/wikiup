package org.wikiup.core.util;

import org.wikiup.core.inf.ExceptionHandler;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class StreamUtil {
    private static final int BUFFER_SIZE = 16384;

    static public long copy(OutputStream dest, InputStream source) {
        return copy(dest, source, null);
    }

    static public long copy(OutputStream dest, InputStream source, ExceptionHandler eh) {
        int len;
        long length = 0;
        byte[] buf = new byte[BUFFER_SIZE];
        try {
            while((len = source.read(buf, 0, buf.length)) > 0) {
                dest.write(buf, 0, len);
                length += len;
            }
            dest.flush();
        } catch(IOException ex) {
            if(!Interfaces.handleException(eh, ex))
                Assert.fail(ex);
        }
        return length;
    }

    static public String loadText(Reader source) {
        return loadText(source, true);
    }

    static public String loadText(Reader source, boolean close) {
        StringWriter writer = new StringWriter();
        try {
            copy(writer, source, null);
        } finally {
            if(close)
                StreamUtil.close(source);
        }
        return writer.toString();
    }

    static public long copy(Writer dest, Reader source) {
        return copy(dest, source, null);
    }

    static public long copy(Writer dest, Reader source, ExceptionHandler eh) {
        long length = 0;
        try {
            int len;
            char[] buf = new char[BUFFER_SIZE];
            while((len = source.read(buf, 0, BUFFER_SIZE)) > 0) {
                dest.write(buf, 0, len);
                length += len;
            }
            dest.flush();
        } catch(IOException ex) {
            if(!Interfaces.handleException(eh, ex))
                Assert.fail(ex);
        }
        return length;
    }

    static public void close(Closeable closeable) {
        close(closeable, null);
    }

    static public void close(Closeable closeable, ExceptionHandler eh) {
        try {
            if(closeable != null)
                closeable.close();
        } catch(IOException ex) {
            if(!Interfaces.handleException(eh, ex))
                Assert.fail(ex);
        }
    }

    static public void flush(Flushable flushable) {
        flush(flushable, null);
    }

    static public void flush(Flushable flushable, ExceptionHandler eh) {
        try {
            if(flushable != null)
                flushable.flush();
        } catch(IOException ex) {
            if(!Interfaces.handleException(eh, ex))
                Assert.fail(ex);
        }
    }
}
