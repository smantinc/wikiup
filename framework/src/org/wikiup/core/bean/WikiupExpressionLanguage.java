package org.wikiup.core.bean;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.el.TemplateExpressionLanguage;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Documents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WikiupExpressionLanguage extends WikiupDynamicSingleton<WikiupExpressionLanguage> implements
        DocumentAware, ExpressionLanguage<Getter<?>, Object>, Getter<ExpressionLanguage<Getter<?>, Object>>, Iterable<ExpressionLanguage<Getter<?>, Object>> {
    private ExpressionLanguage<Getter<?>, Object> defaultExpressionLanguage;
    private Map<String, ExpressionLanguage<Getter<?>, Object>> expressionLanguages;

    static public WikiupExpressionLanguage getInstance() {
        return getInstance(WikiupExpressionLanguage.class);
    }

    public void aware(Document desc) {
        for(Document doc : desc.getChildren("bean")) {
            ExpressionLanguage<Getter<?>, Object> el = Wikiup.build(ExpressionLanguage.class, doc, doc);
            expressionLanguages.put(Documents.getId(doc, el.toString()), el);
        }
        defaultExpressionLanguage = get(Documents.getDocumentValue(desc, "default-el"));
    }

    public Object evaluate(Getter<?> context, String script) {
        return defaultExpressionLanguage.evaluate(context, script);
    }

    public ExpressionLanguage<Getter<?>, Object> get(String name) {
        return expressionLanguages.get(name);
    }

    public void firstBuilt() {
        defaultExpressionLanguage = new TemplateExpressionLanguage();
        expressionLanguages = new HashMap<String, ExpressionLanguage<Getter<?>, Object>>();
    }

    public Iterator<ExpressionLanguage<Getter<?>, Object>> iterator() {
        return expressionLanguages.values().iterator();
    }
}
