package org.wikiup.servlet.impl.context.expr;

import org.wikiup.core.bean.WikiupExpressionLanguage;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.ValueUtil;

public class EvalExpressionGetter implements Getter<String> {
    public String get(String name) {
        return ValueUtil.toString(WikiupExpressionLanguage.getInstance().get("eval").evaluate(null, name));
    }
}
