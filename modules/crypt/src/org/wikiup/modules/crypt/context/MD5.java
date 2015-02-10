package org.wikiup.modules.crypt.context;

import org.wikiup.core.inf.Getter;
import org.wikiup.modules.crypt.util.Crypt;

public class MD5 implements Getter<String> {
    @Override
    public String get(String name) {
        return Crypt.MD5Encrypt(name);
    }
}
