package org.wikiup.core.impl.context;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.inf.ext.Context;

public class ContextWrapper<G, S> implements Context<G, S> {
    private Getter<G> getter;
    private Setter<S> setter;

    public ContextWrapper(Setter<S> setter) {
        this(null, setter);
    }

    public ContextWrapper(Getter<G> getter) {
        this(getter, null);
    }

    public ContextWrapper(Getter<G> getter, Setter<S> setter) {
        this.getter = getter != null ? getter : Null.getInstance();
        this.setter = setter != null ? setter : Null.getInstance();
    }

    public G get(String name) {
        return getter.get(name);
    }

    public void set(String name, S value) {
        setter.set(name, value);
    }

    public Getter<G> getGetter() {
        return getter;
    }

    public Setter<S> getSetter() {
        return setter;
    }

    public void setSetter(Setter<S> setter) {
        this.setter = setter;
    }

    public void setGetter(Getter<G> getter) {
        this.getter = getter;
    }
}
