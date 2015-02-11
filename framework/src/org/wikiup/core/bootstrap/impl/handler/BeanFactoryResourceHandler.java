package org.wikiup.core.bootstrap.impl.handler;

import java.util.HashMap;

import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupBeanFactory;
import org.wikiup.core.impl.factory.FactoryByName;
import org.wikiup.core.impl.factory.FactoryWithBackup;
import org.wikiup.core.impl.factory.FactoryWithTranslator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

public class BeanFactoryResourceHandler extends DirectoryDocumentResourceHandler {
    private WikiupBeanFactory beanFactory = Wikiup.getModel(WikiupBeanFactory.class);
    private Builder builder = new Builder();

    @Override
    protected void loadDirectoryResource(Resource resource, Document doc, String[] path) {
        beanFactory.loadBeans(doc, builder);
    }

    public static class Builder implements Factory.ByDocument<Factory<?, ?>> {
        private HashMap<String, Node> factories = new HashMap<String, Node>();

        @Override
        public Factory<?, ?> build(Document desc) {
            String name = Documents.ensureAttributeValue(desc, Constants.Attributes.NAME);
            Node node = factories.get(name);
            if(node == null)
                factories.put(name, node = new Node(desc));
            node.wire(desc);
            return node.factory;
        }
        
        private static class Node implements Wirable.ByDocument<Factory<?, ?>> {
            private FactoryByName.WIRABLE<Object> wirable;
            private Factory<Object, String> factory;

            public Node(Document desc) {
                wirable = new FactoryByName.WIRABLE<Object>();
                factory = new FactoryWithBackup<Object, String>(wirable.wire(desc), Constants.Factories.BY_CLASS_NAME);
            }

            @Override
            public Factory<?, ?> wire(Document desc) {
                String style = Documents.getAttributeValue(desc, "style", null);
                if(style != null) {
                    try {
                        String[] styles = style.split(":");
                        String className = styles[1];
                        Translator<Object, Object> translator = Interfaces.cast(Translator.class, Interfaces.newInstance(Interfaces.getClass(className)));
                        factory = new FactoryWithTranslator<Object, String>(factory, translator);
                    } catch(Exception e) {
                        Assert.fail(e);
                    }
                }
                wirable.wire(desc);
                return factory;
            }
        }
    }
}