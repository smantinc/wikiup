package org.wikiup.core.inf;

public interface ExpressionLanguage<E, R> {
    public R evaluate(E context, String script);
}
