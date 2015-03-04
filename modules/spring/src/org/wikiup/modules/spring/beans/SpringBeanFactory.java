package org.wikiup.modules.spring.beans;

import java.util.Iterator;

import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.Documents;
import org.wikiup.modules.spring.WikiupSpringDynamicSingleton;

public class SpringBeanFactory implements ModelFactory, Iterable<String>, Factory<BeanContainer> {
    private WikiupSpringDynamicSingleton spring = Wikiup.getModel(WikiupSpringDynamicSingleton.class);

    public BeanContainer get(String name) {
        return spring.get(name);
    }

    public Iterator<String> iterator() {
        return spring.iterator();
    }

    @Override
    public BeanContainer build(Document doc) {
        return spring.get(Documents.ensureAttributeValue(doc, Constants.Attributes.CLASS));
    }
}
