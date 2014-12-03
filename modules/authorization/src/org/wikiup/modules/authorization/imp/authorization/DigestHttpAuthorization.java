package org.wikiup.modules.authorization.imp.authorization;

import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.authorization.inf.AuthorizationInf;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class DigestHttpAuthorization implements AuthorizationInf, DocumentAware, ServletProcessorContextAware {
    private ServletProcessorContext context;
    private Document configure;
    private String domain;

    public boolean authenticate(Principal principal) {
        String authorization = context.getHeader("Authorization");
        if(authorization != null) {
//			Getter<String> params = parse(StringUtil.shrinkLeft(authorization, "Digest "));
//
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("username", params.get("username"));
//			map.put("realm", params.get("realm"));
//			ContextUtil.setProperties(configure, principal, new MapContext<String>(map));
//
//			StackGetter stackGetter = new StackGetter();
//			stackGetter.append(principal, params);
//
//			String ha1 = StringUtil.md5(StringUtil.evaluateEL("${username}:${realm}:${password}", stackGetter));
//			String ha2 = StringUtil.md5(context.getServletRequest().getMethod() + ':' + params.get("uri"));
//			String response = StringUtil.md5(ha1 + StringUtil.evaluateEL(":${nonce}:${nc}:${cnonce}:${qop}:", params) + ha2);
//			return StringUtil.compareIgnoreCase(response, params.get("response"));
            return true;
        }
        return false;
    }

    public void unauthorized() {
        StringBuffer buf = new StringBuffer("Digest realm=\"");
        buf.append(context.getServletRequest().getServerName());
        buf.append("\", domain=\"");
        buf.append(domain);
        buf.append("\", nonce=\"");
        buf.append(StringUtil.generateRandomString(16));
        buf.append("\", opaque=\"");
        buf.append(StringUtil.generateRandomString(16));
        buf.append("\", qop=\"auth,auth-int\"");
        context.setHeader("WWW-Authenticate", buf.toString());
    }

    public void aware(Document desc) {
        configure = desc;
        domain = Documents.getDocumentValue(desc, "domain", "/");
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    private Getter<String> parse(String s) {
        MapContext<String> mc = new MapContext<String>();
        for(String line : s.split(","))
            ContextUtil.parseNameValuePair(mc, line, '=', true);
        return mc;
    }
}
