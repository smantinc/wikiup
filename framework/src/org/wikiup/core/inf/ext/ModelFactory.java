package org.wikiup.core.inf.ext;

import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;

@Deprecated
public interface ModelFactory extends Dictionary<BeanContainer> {
    @Override
    public BeanContainer get(String name);
}
