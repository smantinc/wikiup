package org.wikiup.modules.authorization.imp.principal;

import org.wikiup.modules.authorization.inf.Principal;

public class Anonymous implements Principal {
    public boolean equals(Object another) {
        return another instanceof Anonymous;
    }

    public Object get(String name) {
        return null;
    }

    public String getId() {
        return null;
    }

    public String getName() {
        return null;
    }

    public boolean isAnonymous() {
        return true;
    }

    public boolean isSupervisor() {
        return false;
    }

    public boolean validate() {
        return true;
    }
}
