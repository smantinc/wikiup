package org.wikiup.core.bootstrap.impl.handler;

import java.util.HashMap;

import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupBeanFactory;
import org.wikiup.core.impl.factory.FactoryByClass;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.Wirable;
import org.wikiup.core.util.Documents;

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
                factories.put(name, node = createNode(desc));
            node.wirable.wire(desc);
            return node.factory;
        }
        
        private Node createNode(Document desc) {
            Node node = new Node();
            node.wirable = new FactoryByClass.WIRABLE<Object>();
            node.factory = node.wirable.wire(desc);
            return node;
        }
        
        private static class Node implements Wirable.ByDocument<Factory<?, ?>> {
            private FactoryByClass.WIRABLE<?> wirable;
            private Factory<?, ?> factory;

            @Override
            public Factory<?, ?> wire(Document desc) {
                return wirable.wire(desc);
            }
        }
    }
}