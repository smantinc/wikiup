package org.wikiup.modules.crypt.context;

import org.wikiup.core.inf.Getter;

public class RSA implements Getter<String> {
    @Override
    public String get(String name) {
        return name;
    }
}
