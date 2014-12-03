package org.wikiup.core.impl.releasable;

import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.StreamUtil;

import java.io.Closeable;
import java.io.IOException;

public class CloseableObjectRelease implements Releasable, Closeable {
    private Closeable closeable;

    public CloseableObjectRelease(Closeable object) {
        closeable = object;
    }

    public void release() {
        StreamUtil.close(closeable);
    }

    public void close() throws IOException {
        closeable.close();
    }
}
