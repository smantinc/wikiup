package org.wikiup.modules.authorization.inf;

public interface AuthorizationInf {
    public boolean authenticate(Principal principal);

    public void unauthorized();
}