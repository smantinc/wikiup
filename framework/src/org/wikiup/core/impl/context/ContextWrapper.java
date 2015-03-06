package org.wikiup.core.impl.context;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ext.Context;

public class ContextWrapper<G, S> implements Context<G, S> {
    private Dictionary<G> dictionary;
    private Mutable<S> mutable;

    public ContextWrapper(Mutable<S> mutable) {
        this(null, mutable);
    }

    public ContextWrapper(Dictionary<G> dictionary) {
        this(dictionary, null);
    }

    public ContextWrapper(Dictionary<G> dictionary, Mutable<S> mutable) {
        this.dictionary = dictionary != null ? dictionary : Null.getInstance();
        this.mutable = mutable != null ? mutable : Null.getInstance();
    }

    public G get(String name) {
        return dictionary.get(name);
    }

    public void set(String name, S value) {
        mutable.set(name, value);
    }

    public Dictionary<G> getGetter() {
        return dictionary;
    }

    public Mutable<S> getSetter() {
        return mutable;
    }

    public void setSetter(Mutable<S> mutable) {
        this.mutable = mutable;
    }

    public void setGetter(Dictionary<G> dictionary) {
        this.dictionary = dictionary;
    }
}
