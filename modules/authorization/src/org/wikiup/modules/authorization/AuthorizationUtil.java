package org.wikiup.modules.authorization;

import org.wikiup.Wikiup;
import org.wikiup.modules.authorization.imp.principal.Anonymous;
import org.wikiup.modules.authorization.inf.AuthorizationContextInf;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.servlet.ServletProcessorContext;

public class AuthorizationUtil {
    static private final String[] WND_AUTHORIZATION_CONTEXT = {"wk", "modules", "authorization", "authorization-context", "http-session"};

    static public Principal getCurrentAccount(ServletProcessorContext context) {
//    AccountInf account = getAuthorizationContext(context).authenticate();
        return new Anonymous();
    }

    static public Principal getAuthorizatedAccount(ServletProcessorContext context) {
        return null;
    }

    static public AuthorizationContextInf getAuthorizationContext(ServletProcessorContext context) {
        AuthorizationContextInf ac = Wikiup.getInstance().get(AuthorizationContextInf.class, WND_AUTHORIZATION_CONTEXT);
        context.awaredBy(ac);
        return ac;
    }
}
