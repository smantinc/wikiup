package org.wikiup.core.impl.wndi;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.getter.dl.ByAttributeNameSelector;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Interfaces;

import java.util.Iterator;


public class TransientWikiupNamingDirectory implements Context<Object, Object>, DocumentAware, Iterable<String> {
    private ByAttributeNameSelector descriptions;

    public Object get(String name) {
        Document desc = descriptions.get(name);
        Object object = Wikiup.getInstance().getBean(Object.class, desc);
        Interfaces.initialize(object, desc);
        return object;
    }

    public void set(String name, Object obj) {
    }

    public void aware(Document desc) {
        descriptions = new ByAttributeNameSelector(desc, "name");
    }

    public Iterator<String> iterator() {
        return descriptions.iterator();
    }
}
