package org.wikiup.modules.authorization;

import org.wikiup.Wikiup;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.document.DocumentWithGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Interfaces;
import org.wikiup.modules.authorization.inf.AuthorizationContextInf;
import org.wikiup.modules.authorization.inf.AuthorizationInf;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.servlet.ServletProcessorContext;

public class AuthorizationManager extends WikiupDynamicSingleton<AuthorizationManager> implements DocumentAware {
    private Document authorization;
    private Document authorizationContext;
    private Document principal;

    public AuthorizationInf getAuthorization(ServletProcessorContext context) {
        AuthorizationInf authz = Wikiup.getInstance().getBean(AuthorizationInf.class, authorization);
        Interfaces.initialize(authz, authorization);
        return context.awaredBy(authz);
    }

    public AuthorizationContextInf getAuthorizationContext(ServletProcessorContext context) {
        AuthorizationContextInf ctx = Wikiup.getInstance().getBean(AuthorizationContextInf.class, authorizationContext);
        Interfaces.initialize(ctx, authorization);
        return new AuthorizationContext(context, context.awaredBy(ctx));
    }

    public void firstBuilt() {
    }

    @Override
    public void aware(Document desc) {
        authorization = desc.getChild("authorization");
        authorizationContext = desc.getChild("authorization-context");
        principal = desc.getChild("principal");
    }

    private class AuthorizationContext implements AuthorizationContextInf {
        private AuthorizationContextInf as;
        private ServletProcessorContext context;

        public AuthorizationContext(ServletProcessorContext context, AuthorizationContextInf as) {
            this.context = context;
            this.as = as;
        }

        public void logout() {
            as.logout();
        }

        public void login(Principal account) {
            as.login(account);
        }

        public Principal getPrincipal() {
            Principal p = as.getPrincipal();
            if(p == null) {
                p = Wikiup.getInstance().getBean(Principal.class, principal);
                Interfaces.initialize(p, new DocumentWithGetter(principal, context));
            }
            return context.awaredBy(p);
        }
    }
}
