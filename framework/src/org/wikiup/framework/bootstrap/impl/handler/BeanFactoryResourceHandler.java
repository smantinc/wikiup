package org.wikiup.framework.bootstrap.impl.handler;

import java.util.HashMap;

import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupBeanFactory;
import org.wikiup.core.impl.document.StyleDocument;
import org.wikiup.core.impl.factory.FactoryImpl;
import org.wikiup.core.impl.factory.FactoryWithBackup;
import org.wikiup.core.impl.factory.FactoryWithTranslator;
import org.wikiup.core.inf.Decorator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;

public class BeanFactoryResourceHandler extends DirectoryDocumentResourceHandler {
    private WikiupBeanFactory beanFactory = Wikiup.getModel(WikiupBeanFactory.class);
    private Builder builder = new Builder();

    @Override
    protected void loadDirectoryResource(Resource resource, Document doc, String[] path) {
        beanFactory.loadBeans(doc, builder);
    }

    public static class Builder implements Factory<Factory<?>> {
        private HashMap<String, Node> factories = new HashMap<String, Node>();

        @Override
        public Factory<?> build(Document desc) {
            String name = Documents.ensureAttributeValue(desc, Constants.Attributes.NAME);
            Node node = factories.get(name);
            if(node == null)
                factories.put(name, node = new Node(desc));
            node.wire(desc);
            return node.factory;
        }
        
        private static class Node implements Wirable.ByDocument<Factory<?>> {
            private FactoryImpl.WIRABLE<Object> wirable;
            private Factory<Object> factory;

            public Node(Document desc) {
                wirable = new FactoryImpl.WIRABLE<Object>();
                factory = new FactoryWithBackup<Object>(wirable.wire(desc), Constants.Factories.BY_CLASS_NAME);
            }

            @Override
            public Factory<?> wire(Document desc) {
                String style = Documents.getAttributeValue(desc, Constants.Attributes.STYLE, null);
                if(style != null) {
                    try {
                        Document styles = StyleDocument.parse(style);
                        Decorator<Factory<Object>> decorator = new FactoryWithTranslator.DECORATOR<Object>();
                        factory = decorator.decorate(factory, styles);
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