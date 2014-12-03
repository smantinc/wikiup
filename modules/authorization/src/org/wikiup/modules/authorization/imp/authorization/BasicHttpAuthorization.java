package org.wikiup.modules.authorization.imp.authorization;

import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Base64Coder;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.authorization.inf.AuthorizationInf;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.util.HashMap;
import java.util.Map;

public class BasicHttpAuthorization implements AuthorizationInf, DocumentAware, ServletProcessorContextAware {
    private ServletProcessorContext context;
    private Document configure;
    private String domain;

    public boolean authenticate(Principal principal) {
        String authorization = context.getHeader("Authorization");
        if(authorization != null) {
            authorization = Base64Coder.decodeString(StringUtil.shrinkLeft(authorization, "Basic "));
            int pos = authorization.indexOf(':');
            if(pos != -1) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("name", authorization.substring(0, pos));
                map.put("domain", domain);
                map.put("password", authorization.substring(pos + 1));
                ContextUtil.setProperties(configure, principal, new MapContext<String>(map));
            }
        }
        return principal.validate();
    }

    public void unauthorized() {
        StringBuffer buf = new StringBuffer("Basic realm=\"");
        buf.append(context.getServletRequest().getServerName());
        buf.append("\", domain=\"");
        buf.append(domain);
        buf.append("\", nonce=\"");
        buf.append(StringUtil.generateRandomString(16));
        buf.append("\", algorithm=MD5, qop=\"auth,auth-int\"");
        context.setHeader("WWW-Authenticate", buf.toString());
    }

    public void aware(Document desc) {
        configure = desc;
        domain = Documents.getDocumentValue(desc, "domain", "/");
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}