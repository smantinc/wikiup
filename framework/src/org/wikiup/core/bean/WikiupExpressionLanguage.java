package org.wikiup.core.bean;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.el.TemplateExpressionLanguage;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WikiupExpressionLanguage extends WikiupDynamicSingleton<WikiupExpressionLanguage> implements
        DocumentAware, ExpressionLanguage<Dictionary<?>, Object>, Dictionary<ExpressionLanguage<Dictionary<?>, Object>>, Iterable<ExpressionLanguage<Dictionary<?>, Object>> {
    private ExpressionLanguage<Dictionary<?>, Object> defaultExpressionLanguage;
    private Map<String, ExpressionLanguage<Dictionary<?>, Object>> expressionLanguages;

    static public WikiupExpressionLanguage getInstance() {
        return getInstance(WikiupExpressionLanguage.class);
    }

    public void aware(Document desc) {
        for(Document doc : desc.getChildren("bean")) {
            ExpressionLanguage<Dictionary<?>, Object> el = Wikiup.build(ExpressionLanguage.class, doc, doc);
            expressionLanguages.put(Documents.getId(doc, el.toString()), el);
        }
        defaultExpressionLanguage = get(Documents.getDocumentValue(desc, "default-el"));
    }

    public Object evaluate(Dictionary<?> context, String script) {
        return defaultExpressionLanguage.evaluate(context, script);
    }

    public ExpressionLanguage<Dictionary<?>, Object> get(String name) {
        return expressionLanguages.get(name);
    }

    public void firstBuilt() {
        defaultExpressionLanguage = new TemplateExpressionLanguage();
        expressionLanguages = new HashMap<String, ExpressionLanguage<Dictionary<?>, Object>>();
    }

    public Iterator<ExpressionLanguage<Dictionary<?>, Object>> iterator() {
        return expressionLanguages.values().iterator();
    }
}
