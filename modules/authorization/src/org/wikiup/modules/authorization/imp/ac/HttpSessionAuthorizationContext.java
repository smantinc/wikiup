package org.wikiup.modules.authorization.imp.ac;

import org.wikiup.framework.bean.WikiupConfigure;
import org.wikiup.core.util.Interfaces;
import org.wikiup.modules.authorization.inf.AuthorizationContextInf;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import javax.servlet.http.HttpSession;

public class HttpSessionAuthorizationContext implements AuthorizationContextInf, ServletProcessorContextAware {

    final static private String DEFAULT_SESSION_NAME = "wikiup.module.authorization.session-name";
    final static private String SESSION_NAME = WikiupConfigure.getInstance().getProperty(DEFAULT_SESSION_NAME, "wk:authorization:context-name");

    private HttpSession session;

    public void logout() {
        session.removeAttribute(SESSION_NAME);
    }

    public void login(Principal account) {
        session.setAttribute(SESSION_NAME, account);
    }

    public Principal getPrincipal() {
        return Interfaces.cast(Principal.class, session.getAttribute(SESSION_NAME));
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        session = context.getSession();
    }
}
