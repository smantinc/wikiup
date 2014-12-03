package org.wikiup.modules.webdav.jaas.spi;

import java.security.Principal;

public class AcaciaPrincipal implements Principal {
    private String name;

    public AcaciaPrincipal(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int hashCode() {
        return name.hashCode();
    }
}
