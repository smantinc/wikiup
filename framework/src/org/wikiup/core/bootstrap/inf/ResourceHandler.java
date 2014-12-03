package org.wikiup.core.bootstrap.inf;

import org.wikiup.core.inf.Resource;

public interface ResourceHandler {
    public void handle(Resource resource);

    public void finish();
}
