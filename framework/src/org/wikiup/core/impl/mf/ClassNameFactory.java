package org.wikiup.core.impl.mf;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.impl.document.MergedDocument;
import org.wikiup.core.impl.element.ElementImpl;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Element;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClassNameFactory implements ModelFactory, DocumentAware, Iterable<String> {
    private Map<String, String> classNameMap = new HashMap<String, String>();
    private Map<String, Document> classDocumentMap = new HashMap<String, Document>();
    private ModelFactory factory = new ClassFactory();

    public ClassNameFactory() {
    }

    public ClassNameFactory(Document doc) {
        aware(doc);
    }

    public ClassNameFactory(Document desc, ModelFactory factory) {
        this.factory = factory;
        aware(desc);
    }

    public BeanFactory get(String name) {
        String className = classNameMap.get(name);
        return className != null ? new AliasNameFactoryModelProvider(factory.get(className), classDocumentMap.get(name)) : null;
    }

    public void setFactory(ModelFactory factory) {
        this.factory = factory;
    }

    public void aware(Document desc) {
        for(Document doc : desc.getChildren()) {
            String name = Documents.getId(doc);
            if(!classNameMap.containsKey(name) || Documents.getAttributeBooleanValue(desc, "override", false)) {
                classNameMap.put(name, Documents.getAttributeValue(doc, WikiupConfigure.DEFAULT_FACTORY_ATTRIBUTE, null));
                if(attributeCount(doc) > 2)
                    classDocumentMap.put(name, doc);
            }
        }
    }

    private int attributeCount(Element element) {
        ElementImpl el = Interfaces.cast(ElementImpl.class, element);
        return el != null ? el.attributeCount() : -1;
    }

    public Iterator<String> iterator() {
        return classNameMap.keySet().iterator();
    }

    static private class AliasNameFactoryModelProvider implements BeanFactory {
        private BeanFactory modelProvider;
        private Document document;

        public AliasNameFactoryModelProvider(BeanFactory modelProvider, Document document) {
            this.modelProvider = modelProvider;
            this.document = document;
        }

        public <E> E query(Class<E> clazz) {
            if(clazz.equals(DocumentAware.class)) {
                DocumentAware aware = modelProvider.query(DocumentAware.class);
                return aware != null ? clazz.cast(document != null ? new AliasNameFactoryModelContainerDocumentAware(aware, document) : aware) : null;
            }
            return modelProvider.query(clazz);
        }

        @Override
        public String toString() {
            return modelProvider.toString();
        }

        static private class AliasNameFactoryModelContainerDocumentAware implements DocumentAware {
            private DocumentAware aware;
            private Document document;

            public AliasNameFactoryModelContainerDocumentAware(DocumentAware aware, Document document) {
                this.aware = aware;
                this.document = document;
            }

            public void aware(Document desc) {
                aware.aware(new MergedDocument(desc, document));
            }
        }
    }
}