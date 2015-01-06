package org.wikiup.core.inf.ext;

import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Getter;

@Deprecated
public interface ModelFactory extends Getter<BeanContainer> {
    @Override
    public BeanContainer get(String name);
}
