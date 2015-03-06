package org.wikiup.modules.authorization.inf;

import org.wikiup.core.inf.Dictionary;

public interface Principal extends java.security.Principal, Dictionary<Object> {
    public String getId();

    public boolean validate();

    public boolean isSupervisor();

    public boolean isAnonymous();
}
