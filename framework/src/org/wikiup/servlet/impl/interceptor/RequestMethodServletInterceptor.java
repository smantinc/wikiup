package org.wikiup.servlet.impl.interceptor;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletInterceptor;

public class RequestMethodServletInterceptor implements ServletInterceptor, DocumentAware {
    private String interceptMethod;

    public boolean intercept(ServletProcessorContext context) {
        String method = context.getServletRequest().getMethod();
        return StringUtil.compareIgnoreCase(method, interceptMethod) || method.matches(interceptMethod);
    }

    public String getInterceptMethod() {
        return interceptMethod;
    }

    public void setInterceptMethod(String interceptMethod) {
        this.interceptMethod = interceptMethod;
    }

    public void aware(Document desc) {
        interceptMethod = Documents.getDocumentValue(desc, "intercept-method", null);
    }
}
