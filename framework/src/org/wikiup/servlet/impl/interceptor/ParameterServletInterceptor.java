package org.wikiup.servlet.impl.interceptor;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletInterceptor;

public class ParameterServletInterceptor implements ServletInterceptor, DocumentAware {
    private String contains;

    public boolean intercept(ServletProcessorContext context) {
        return contains != null && !StringUtil.isEmpty(context.getParameter(contains, null));
    }

    public void aware(Document desc) {
        contains = Documents.getAttributeValue(desc, "contains", null);
    }
}
