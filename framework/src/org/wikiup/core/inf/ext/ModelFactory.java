package org.wikiup.core.inf.ext;

import org.wikiup.core.inf.BeanContainer;

@Deprecated
public interface ModelFactory {
    public BeanContainer get(String name);
}
