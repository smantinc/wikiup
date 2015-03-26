package org.wikiup.servlet.impl.context.expr;

import org.wikiup.core.bean.WikiupExpressionLanguage;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.ValueUtil;

public class EvalExpressionDictionary implements Dictionary<String> {
    public String get(String name) {
        return ValueUtil.toString(WikiupExpressionLanguage.getInstance().get("eval").evaluate(null, name));
    }
}
