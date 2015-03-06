package org.wikiup.modules.crypt.context;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.modules.crypt.util.Crypt;

public class MD5 implements Dictionary<String> {
    @Override
    public String get(String name) {
        return Crypt.MD5Encrypt(name);
    }
}
