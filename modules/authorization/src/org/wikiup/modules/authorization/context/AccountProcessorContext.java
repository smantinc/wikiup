package org.wikiup.modules.authorization.context;

import org.wikiup.modules.authorization.AuthorizationUtil;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class AccountProcessorContext implements ProcessorContext, ServletProcessorContextAware {
    private Principal account;

    public void setServletProcessorContext(ServletProcessorContext context) {
        account = AuthorizationUtil.getCurrentAccount(context);
    }

    public Object get(String name) {
        return account.get(name);
    }
}
