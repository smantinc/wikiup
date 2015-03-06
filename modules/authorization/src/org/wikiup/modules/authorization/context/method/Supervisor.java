package org.wikiup.modules.authorization.context.method;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.modules.authorization.AuthorizationManager;
import org.wikiup.modules.authorization.inf.AuthorizationInf;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ext.ContextMethodSupport;

public class Supervisor extends ContextMethodSupport {
    @Override
    protected Object invoke(ServletProcessorContext context, Dictionary<?> params) {
        AuthorizationManager am = Wikiup.getModel(AuthorizationManager.class);
        Principal principal = am.getAuthorizationContext(context).getPrincipal();
        AuthorizationInf authz = am.getAuthorization(context);
        return authz.authenticate(principal) ? principal.isSupervisor() : false;
    }
}
