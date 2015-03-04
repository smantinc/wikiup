package org.wikiup.core.impl;

import java.util.Iterator;

import org.wikiup.core.impl.iterable.GenericCastIterable;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Bindable;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Factory;
import org.wikiup.core.inf.ext.Context;


public class Null implements Context, Iterator, Document, BeanContainer, Iterable, Bindable, Factory {
    private static final Null instance = new Null();

    public static Null getInstance() {
        return instance;
    }

    public Object get(String name) {
        return null;
    }

    public void set(String name, Object obj) {
    }

    public boolean hasNext() {
        return false;
    }

    public Object next() {
        return null;
    }

    public void remove() {
    }

    public Attribute getAttribute(String name) {
        return null;
    }

    public Attribute addAttribute(String name) {
        return this;
    }

    public void removeAttribute(Attribute attr) {
    }

    public Iterable<Attribute> getAttributes() {
        return iter();
    }

    public Document getChild(String name) {
        return null;
    }

    public Document addChild(String name) {
        return this;
    }

    public Iterable<Document> getChildren(String name) {
        return iter();
    }

    public Iterable<Document> getChildren() {
        return iter();
    }

    public void removeNode(Document child) {
    }

    public Document getParentNode() {
        return null;
    }

    public String getName() {
        return "";
    }

    public void setName(String name) {
    }

    public void setObject(Object obj) {
    }

    public Object getObject() {
        return null;
    }

    public <E> Iterable<E> iter() {
        return new GenericCastIterable<E>(this);
    }

    @Override
    public String toString() {
        return "";
    }

    public <E> E query(Class<E> clazz) {
        return null;
    }

    public Iterator iterator() {
        return this;
    }

    public void bind(Object object) {
    }

    public void put(Object instance) {
    }

    public Object get() {
        return null;
    }

    @Override
    public Object build(Document doc) {
        return null;
    }
}
