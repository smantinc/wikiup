package org.wikiup.modules.authorization.action;

import org.wikiup.Wikiup;
import org.wikiup.core.inf.Document;
import org.wikiup.modules.authorization.AuthorizationManager;
import org.wikiup.modules.authorization.inf.AuthorizationContextInf;
import org.wikiup.modules.authorization.inf.AuthorizationInf;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class AuthorizationServletAction implements ServletAction {

    public void doAction(ServletProcessorContext context, Document desc) {
        AuthorizationManager am = Wikiup.getModel(AuthorizationManager.class);
        AuthorizationContextInf ac = am.getAuthorizationContext(context);
        Principal pricipal = ac.getPrincipal();
        AuthorizationInf auth = am.getAuthorization(context);
        if(pricipal == null || !auth.authenticate(pricipal)) {
            auth.unauthorized();
            context.getServletResponse().setStatus(401);
            context.abort();
        } else
            ac.login(pricipal);
    }
}
