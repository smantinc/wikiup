/*
 *  Copyright 2005 - 2010 by Jason T. smantinc@gmail.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wikiup.core;

import org.wikiup.core.bean.WikiupBeanContainer;
import org.wikiup.core.bean.WikiupBeanFactory;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.impl.mp.InstanceModelProvider;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Provider;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class Wikiup implements Context<Object, Object>, Releasable {
    private static Provider<Wikiup> instanceProvider = new DefaultWikiupInstanceProvider();

    private BeanContainer beanContainer = new WikiupBeanContainer();
    private WikiupBeanFactory modelDirectory = new WikiupBeanFactory();
    private Context<Object, Object> wndi = WikiupNamingDirectory.getInstance();

    static public Wikiup getInstance() {
        return instanceProvider.get();
    }

    public void set(String name, Object container) {
        wndi.set(name, container);
    }

    public Object get(String name) {
        return wndi.get(name);
    }

    public <E> E get(Class<E> clazz, String[] path) {
        return clazz.cast(WikiupNamingDirectory.getInstance().get(path, path.length));
    }

    public <E> E get(Class<E> clazz, String name) {
        return clazz.cast(wndi.get(name));
    }

    public <E> E get(Class<E> clazz, Document doc) {
        ClassIdentity csid = getClassIdentity(doc);
        return get(clazz, csid.getId());
    }

    public <E> E getBean(Class<E> clazz, Document doc) {
        ClassIdentity csid = getClassIdentity(doc);
        BeanContainer mc = csid.getModelContainer(clazz);
        E bean = mc != null ? mc.query(clazz) : null;
        return bean != null ? bean : build(clazz, csid.getNamespace(), csid.getName(), doc);
    }

    static public BeanContainer getModelProvider(Class<?> inf, Document doc) {
        ClassIdentity csid = getClassIdentity(doc);
        BeanContainer mc = csid.getModelContainer(inf);
        return mc == null ? new InstanceModelProvider(build(Object.class, doc, null)) : mc;
    }

    public <E> void loadBeans(Class<E> clazz, Setter<E> directory, Document doc) {
        for(Document node : doc.getChildren("bean")) {
            E bean = getBean(clazz, node);
            bean = bean != null ? bean : Wikiup.build(clazz, node, null);
            String name = Documents.getId(node, Assert.notNull(bean).toString());
            Interfaces.initialize(bean, node);
            Assert.notNull(directory, IllegalStateException.class, name).set(name, bean);
        }
    }

    static public <E> E build(Class<E> clazz, Document doc, Document init) {
        ClassIdentity csid = getClassIdentity(doc);
        E instance = Interfaces.newInstance(clazz, csid.getName());
        Interfaces.initialize(instance, init);
        return instance;
    }

    private <E> E build(Class<E> clazz, String namespace, String name, Document desc) {
        ModelFactory factory = modelDirectory.get(namespace);
        Assert.notNull(factory, namespace);
        return build(clazz, factory, name, desc);
    }

    static private <E> E build(Class<E> clazz, ModelFactory factory, String name, Document desc) {
        BeanContainer mc = name != null ? factory.get(name) : null;
        Interfaces.initialize(mc, desc);
        return mc != null ? mc.query(clazz) : null;
    }

    static public <E> E getModel(Class<E> clazz) {
        return getInstance().beanContainer.query(clazz);
    }

    @Deprecated
    static public ClassIdentity getClassIdentity(String id) {
        return new ClassIdentity(id);
    }

    @Deprecated
    static public ClassIdentity getClassIdentity(Document doc) {
        return new ClassIdentity(getCsidAttribute(doc, null));
    }

    @Deprecated
    static public String getCsidAttribute(Document doc, String def) {
        return Documents.getAttributeValue(doc, WikiupConfigure.DEFAULT_FACTORY_ATTRIBUTE, def);
    }

    public void release() {
        Interfaces.release(beanContainer);
        Interfaces.release(modelDirectory);
        Interfaces.release(wndi);
        instanceProvider = new DefaultWikiupInstanceProvider();
    }

    static private class DefaultWikiupInstanceProvider implements Provider<Wikiup> {
        private Wikiup instance;

        synchronized public Wikiup get() {
            return instance == null ? (instance = new Wikiup()) : instance;
        }
    }
}
