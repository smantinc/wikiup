package org.wikiup.modules.webdav.method;

import org.wikiup.core.inf.Document;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

import javax.servlet.http.HttpServletResponse;

public class OptionsRequestMethod implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        HttpServletResponse response = context.getServletResponse();
        response.setHeader("DAV", "1, 2, slide, access-control, binding");
        response.addHeader("DAV", "version-control, version-history, checkout-in-place");
        response.addHeader("DAV", "workspace, working-resource, update, label");
        response.setHeader("MS-Author-Via", "DAV");
        response.setHeader("DASL", "<DAV:basicsearch>");
        response.setHeader("Allow", "PROPPATCH, COPY, DELETE, POST, GET, REPORT, PROPFIND, PUT, UNBIND, MOVE,UNLOCK, TRACE, OPTIONS, HEAD, ACL, LOCK, BIND, CONNECT, REBIND, SEARCH");
    }
}
