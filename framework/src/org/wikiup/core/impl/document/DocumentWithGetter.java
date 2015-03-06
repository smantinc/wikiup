package org.wikiup.core.impl.document;

import org.wikiup.core.impl.attribute.AttributeWrapper;
import org.wikiup.core.impl.element.ElementWrapper;
import org.wikiup.core.impl.iterator.IteratorWrapper;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Element;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.util.Iterator;

public class DocumentWithGetter extends DocumentWrapper {
    private Dictionary<?> dictionary;
    private AttributeWithGetter attributeWithGetter;
    private ElementWithGetter element;

    public DocumentWithGetter(Document doc, Dictionary<?> dictionary) {
        super(doc);
        this.dictionary = dictionary;
    }

    @Override
    public void setDocument(Document doc) {
        super.setDocument(doc);
        attributeWithGetter = new AttributeWithGetter(doc);
        element = new ElementWithGetter(doc);
    }

    @Override
    public org.wikiup.core.inf.Document getChild(String name) {
        Document child = super.getChild(name);
        return child != null ? new DocumentWithGetter(child, dictionary) : null;
    }

    @Override
    public Iterable<Document> getChildren(String name) {
        final Iterable<Document> children = super.getChildren(name);
        return new Iterable<Document>() {
            public Iterator<Document> iterator() {
                return new ChildrenIterator(children.iterator());
            }
        };
    }

    @Override
    public Iterable<Document> getChildren() {
        final Iterable<Document> children = super.getChildren();
        return new Iterable<Document>() {
            public Iterator<Document> iterator() {
                return new ChildrenIterator(children.iterator());
            }
        };
    }

    @Override
    public Document getParentNode() {
        return new DocumentWithGetter(super.getParentNode(), dictionary);
    }

    @Override
    public Attribute getAttribute(String name) {
        return element.getAttribute(name);
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return element.getAttributes();
    }

    @Override
    public String getName() {
        return attributeWithGetter.getName();
    }

    @Override
    public Object getObject() {
        return attributeWithGetter.getObject();
    }

    private class ChildrenIterator extends IteratorWrapper<Document> {
        public ChildrenIterator(Iterator<Document> iterator) {
            super(iterator);
        }

        @Override
        public Document next() {
            return new DocumentWithGetter(super.next(), dictionary);
        }
    }

    private class ElementWithGetter extends ElementWrapper {
        public ElementWithGetter(Element element) {
            super(element);
        }

        @Override
        public Attribute getAttribute(String name) {
            Attribute attr = super.getAttribute(name);
            return attr != null ? new AttributeWithGetter(attr) : null;
        }

        @Override
        public Iterable<Attribute> getAttributes() {
            final Iterable<Attribute> attributes = super.getAttributes();
            return new Iterable<Attribute>() {
                public Iterator<Attribute> iterator() {
                    return new AttributeIterator(attributes.iterator());
                }
            };
        }

        private class AttributeIterator extends IteratorWrapper<Attribute> {
            public AttributeIterator(Iterator<Attribute> iterator) {
                super(iterator);
            }

            @Override
            public Attribute next() {
                return new AttributeWithGetter(super.next());
            }
        }
    }

    private class AttributeWithGetter extends AttributeWrapper {
        public AttributeWithGetter(Attribute attr) {
            super(attr);
        }

        public String getName() {
            return StringUtil.evaluateEL(super.getName(), dictionary);
        }

        public Object getObject() {
            return StringUtil.evaluateEL(ValueUtil.toString(super.getObject()), dictionary);
        }
    }
}
