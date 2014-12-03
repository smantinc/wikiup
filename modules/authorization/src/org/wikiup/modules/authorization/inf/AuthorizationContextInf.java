package org.wikiup.modules.authorization.inf;

public interface AuthorizationContextInf {
    public void logout();

    public void login(Principal account);

    public Principal getPrincipal();
}
