package org.wikiup.core.inf;

public interface Action<S, P> {
    public void doAction(S sender, P param);
}
