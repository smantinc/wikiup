/*
 *  Copyright 2005 - 2015 by Jason T. smantinc@gmail.com
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
import org.wikiup.framework.bean.WikiupNamingDirectory;
import org.wikiup.core.impl.mp.InstanceModelProvider;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.Wrapper;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ClassIdentity;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class Wikiup implements Context<Object, Object>, Releasable {
    private static Wrapper<Wikiup> instanceProvider = new DefaultWikiupInstanceWrapper();

    private BeanContainer beanContainer = new WikiupBeanContainer();
    private WikiupBeanFactory beanFactory = new WikiupBeanFactory();
    private Context<Object, Object> wndi = WikiupNamingDirectory.getInstance();

    static public Wikiup getInstance() {
        return instanceProvider.wrapped();
    }

    public void set(String name, Object container) {
        wndi.set(name, container);
    }

    public Object get(String name) {
        return wndi.get(name);
    }

    public <E> E get(Class<E> clazz, String name) {
        return clazz.cast(wndi.get(name));
    }

    public <E> E get(Class<E> clazz, Document doc) {
        ClassIdentity csid = ClassIdentity.obtain(doc);
        return get(clazz, csid.getId());
    }

    public <E> E getBean(Class<E> clazz, Document doc) {
        return beanFactory.build(clazz, doc);
    }

    public <T, P> T getBean(Class<T> clazz, Document doc, P wire) {
        return beanFactory.build(clazz, doc, wire);
    }
    
    @Deprecated
    public BeanContainer getModelProvider(Class<?> inf, Document doc) {
        Object obj = getBean(inf, doc);
        if(obj == null)
            obj = beanFactory.build(Object.class, doc);
        return new InstanceModelProvider(obj);
    }

    public <E> void loadBeans(Class<E> clazz, Mutable<E> directory, Document doc) {
        for(Document node : doc.getChildren(Constants.Attributes.BEAN)) {
            E bean = getBean(clazz, node);
            bean = bean != null ? bean : Wikiup.build(clazz, node, null);
            String name = Documents.getId(node, Assert.notNull(bean).toString());
            Interfaces.initialize(bean, node);
            Assert.notNull(directory, IllegalStateException.class, name).set(name, bean);
        }
    }

    static public <E> E build(Class<E> clazz, Document doc, Document init) {
        ClassIdentity csid = ClassIdentity.obtain(doc);
        E instance = Interfaces.cast(clazz, Interfaces.newInstance(Interfaces.getClass(csid.getName())));
        Interfaces.initialize(instance, init);
        return instance;
    }

    static public <E> E getModel(Class<E> clazz) {
        return getInstance().beanContainer.query(clazz);
    }

    @Deprecated
    static public String getCsidAttribute(Document doc, String def) {
        return Documents.getAttributeValue(doc, Constants.Attributes.CLASS, def);
    }

    public void release() {
        Interfaces.release(beanContainer);
        Interfaces.release(wndi);
        instanceProvider = new DefaultWikiupInstanceWrapper();
    }

    static private class DefaultWikiupInstanceWrapper implements Wrapper<Wikiup> {
        private Wikiup instance;

        @Override
        public synchronized Wikiup wrapped() {
            return instance == null ? (instance = new Wikiup()) : instance;
        }
    }
}
