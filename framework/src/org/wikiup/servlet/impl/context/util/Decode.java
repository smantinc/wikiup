package org.wikiup.servlet.impl.context.util;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.impl.getter.NamespaceGetter;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Base64Coder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Decode extends NamespaceGetter {
    public Decode() {
        addNamespace("url", new URLDecoderWrapper());
        addNamespace("base64", new Base64DecoderWrapper());
    }

    static class Base64DecoderWrapper implements Getter<String> {
        public String get(String name) {
            return Base64Coder.decodeString(name);
        }
    }

    static class URLDecoderWrapper implements Getter<Getter<?>> {
        public Getter<?> get(String url) {
            return new URLDecoderInstance(url);
        }

        static class URLDecoderInstance implements Getter<String> {
            private String url;

            public URLDecoderInstance(String url) {
                this.url = url;
            }

            @Override
            public String toString() {
                return get(WikiupConfigure.CHAR_SET);
            }

            public String get(String charset) {
                try {
                    return URLDecoder.decode(url, charset);
                } catch(UnsupportedEncodingException e) {
                    Assert.fail(e);
                }
                return null;
            }
        }
    }
}
