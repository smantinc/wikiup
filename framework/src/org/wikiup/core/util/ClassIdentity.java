package org.wikiup.core.util;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupBeanFactory;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.inf.ext.ModelFactory;

public class ClassIdentity {
    private String namespace;
    private String name;

    private String id;

    public ClassIdentity(String identity) {
        setId(identity);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        int idx = id != null ? id.lastIndexOf(':') : -1;
        this.id = id;
        namespace = idx != -1 ? StringUtil.trimLeft(id.substring(0, idx), ":") : null;
        namespace = StringUtil.isEmpty(namespace) ? null : namespace;
        name = idx != -1 ? id.substring(idx + 1) : id;
    }

    public <E> E getBean(Class<E> clazz) {
        ModelProvider mc = getModelContainer(clazz);
        return mc == null ? Interfaces.newInstance(clazz, getName()) : mc.getModel(clazz);
    }

    public ModelProvider getModelContainer(Class<?> clazz) {
        ModelFactory aliasFactory = getModelFactory(clazz);
        ModelFactory factory = aliasFactory != null ? aliasFactory : Wikiup.getInstance().get(ModelFactory.class, namespace);
        return factory != null ? factory.get(name) : null;
    }

    private ModelFactory getModelFactory(Class<?> clazz) {
        return Wikiup.getModel(WikiupBeanFactory.class).getModelFactory(clazz, this);
    }
}
