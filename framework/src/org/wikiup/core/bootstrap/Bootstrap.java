package org.wikiup.core.bootstrap;

import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.bootstrap.inf.ext.BootstrapAction;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.exception.BeanException;
import org.wikiup.core.impl.mf.NamespaceFactory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.LogicalTranslator;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class Bootstrap implements DocumentAware, ResourceHandler {
    static private Bootstrap instance = null;
    static private boolean started = false;

    private NamespaceFactory factory = new NamespaceFactory();
    private BootstrapResource bootstrapResource = new BootstrapResource();

    static synchronized public Bootstrap getInstance() {
        return instance == null ? (instance = new Bootstrap()) : instance;
    }

    static public void release() {
        instance = null;
    }

    public void bootstrap(boolean force) {
        if(!started || force) {
            started = false;
            new Thread(new BootstrapLoader()).start();
            ready();
        }
    }

    public BootstrapResource getBootstrapResource() {
        return bootstrapResource;
    }

    public void start() {
        String entry = WikiupConfigure.getInstance().getProperty("wikiup.bootstrap.entry", "wikiup/wikiup-bootstrap.xml");
        Document bootstrap = Documents.loadFromURL(Thread.currentThread().getContextClassLoader().getResource(entry));
        aware(bootstrap);
    }

    private void handleBootstrapResources(Document container) {
        for(Document node : container.getChildren(Constants.Elements.HANDLER)) {
            ResourceHandler resourceHandler = build(ResourceHandler.class, node);
            LogicalTranslator<String> filter = build(LogicalTranslator.class, node.getChild("filter"));
            bootstrapResource.loadResources(resourceHandler, filter);
        }
    }

    public <E> E build(Class<E> clazz, Document desc) {
        return build(clazz, desc, null);
    }

    public <E> E build(Class<E> clazz, Document desc, String def) {
        if(desc != null) {
            String attr = Wikiup.getCsidAttribute(desc, def);
            if(attr != null) {
                BeanContainer mc = Assert.notNull(factory.get(attr), AttributeException.class, factory, attr);
                E obj = mc.query(clazz);
                Assert.notNull(obj, BeanException.class, attr, desc);
                Interfaces.initialize(obj, desc);
                return obj;
            }
        }
        return null;
    }

    public void aware(Document desc) {
        Document node;
        Interfaces.initialize(factory, desc.getChild("model-factory"));

        if((node = desc.getChild("beans")) != null) {
            for(Document doc : node.getChildren()) {
                String name = Documents.getId(doc, null);
                Object bean = build(Object.class, doc);
                if(name != null)
                    WikiupNamingDirectory.getInstance().set(name, bean);
            }
        }

        if((node = desc.getChild("resources")) != null)
            handleBootstrapResources(node);

        doBootstrapAction(desc);
    }

    private void doBootstrapAction(Document node) {
        BootstrapAction action = build(BootstrapAction.class, node);
        if(action != null)
            action.doAction(this, node);
        else
            for(Document n : node.getChildren("action"))
                doBootstrapAction(n);
    }

    public void handle(Resource resource) {
        aware(Documents.loadFromResource(resource));
    }

    public void finish() {
    }

    public static void onReady(Runnable callback) {
        new Thread(new BootstrapCallbacker(callback)).start();
    }

    synchronized private void ready() {
        while(!started) {
            try {
                wait();
            } catch(InterruptedException ex) {
            }
        }
    }

    static private class BootstrapLoader implements Runnable {
        public void run() {
            Bootstrap bootstrap = Bootstrap.getInstance();
            try {
                bootstrap.start();
            } finally {
                started = true;
                synchronized(bootstrap) {
                    bootstrap.notifyAll();
                }
            }
        }
    }

    static private class BootstrapCallbacker implements Runnable {
        private Runnable callback;

        public BootstrapCallbacker(Runnable callback) {
            this.callback = callback;
        }

        public void run() {
            Bootstrap.getInstance().ready();
            callback.run();
        }
    }
}
