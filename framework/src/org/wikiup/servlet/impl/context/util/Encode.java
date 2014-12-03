package org.wikiup.servlet.impl.context.util;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.impl.getter.NamespaceGetter;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Base64Coder;
import org.wikiup.core.util.Documents;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class Encode extends NamespaceGetter {
    public Encode() {
        addNamespace("url", new URLEncodeWrapper());
        addNamespace("xml", new XmlEncodeWrapper());
        addNamespace("base64", new Base64EncodeWrapper());
    }

    private static class XmlEncodeWrapper implements Getter<String> {
        public String get(String name) {
            return Documents.encode(name);
        }
    }

    private static class Base64EncodeWrapper implements Getter<String> {
        public String get(String name) {
            return Base64Coder.encodeString(name);
        }
    }

    private static class URLEncodeWrapper implements Getter<Getter<?>> {
        public Getter<?> get(String name) {
            return new URLEncoderInstance(name);
        }

        static class URLEncoderInstance implements Getter<String> {
            private String url;

            public URLEncoderInstance(String url) {
                this.url = url;
            }

            @Override
            public String toString() {
                return get(WikiupConfigure.CHAR_SET);
            }

            public String get(String charset) {
                try {
                    return URLEncoder.encode(url, charset);
                } catch(UnsupportedEncodingException e) {
                    Assert.fail(e);
                }
                return null;
            }
        }
    }
}
