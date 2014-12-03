package org.wikiup.core.impl.mf;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Documents;

import java.util.HashMap;
import java.util.Map;

public class NamespaceFactory implements ModelFactory, DocumentAware {
    private Map<String, ModelFactory> factorys = new HashMap<String, ModelFactory>();

    public ModelProvider get(String name) {
        ClassIdentity ci = Wikiup.getClassIdentity(name);
        ModelFactory factory = getFactory(ci.getNamespace());
        return factory != null ? factory.get(ci.getName()) : null;
    }

    public void addFactory(String name, ModelFactory factory) {
        factorys.put(name, factory);
    }

    public ModelFactory getFactory(String name) {
        return factorys.get(name);
    }

    public void aware(Document desc) {
        addFactoryList(desc);
    }

    public void addFactoryList(Document desc) {
        for(Document node : desc.getChildren())
            addFactory(node);
    }

    public void addFactory(Document desc) {
        addFactory(Documents.getId(desc), desc);
    }

    public void addFactory(String name, Document desc) {
        if(!factorys.containsKey(name) || Documents.getAttributeBooleanValue(desc, "override", false))
            addFactory(name, Wikiup.build(ModelFactory.class, desc, desc));
    }

    public Map<String, ModelFactory> getFactorys() {
        return factorys;
    }
}
