package org.wikiup.modules.authorization.inf;

import org.wikiup.core.inf.Getter;

public interface Principal extends java.security.Principal, Getter<Object> {
    public String getId();

    public boolean validate();

    public boolean isSupervisor();

    public boolean isAnonymous();
}
