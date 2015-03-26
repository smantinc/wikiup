package org.wikiup.modules.webdav.imp.processor;

import org.wikiup.Wikiup;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Assert;
import org.wikiup.modules.authorization.AuthorizationManager;
import org.wikiup.modules.authorization.inf.AuthorizationContextInf;
import org.wikiup.modules.authorization.inf.AuthorizationInf;
import org.wikiup.modules.authorization.inf.Principal;
import org.wikiup.modules.webdav.imp.WebdavFileSystem;
import org.wikiup.modules.webdav.inf.FileSystemInf;
import org.wikiup.modules.webdav.util.StatusUtil;
import org.wikiup.modules.webdav.util.WebdavUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.exception.ServiceNotImplementException;
import org.wikiup.servlet.inf.ServletProcessor;
import org.wikiup.servlet.util.ActionUtil;

public class WebdavServletProcessor implements ServletProcessor, DocumentAware {
    private Document configure;

    public void process(ServletProcessorContext context) {
        String path = WebdavUtil.getWebdavFilePath(context);
        String methodName = context.getServletRequest().getMethod();
        String destination = WebdavUtil.getWebdavFilePath(context.getHeader("Destination"));
        FileSystemInf fs = WebdavFileSystem.getInstance().getFileSystem(context, path);
        AuthorizationContextInf ac = WebdavFileSystem.getInstance().getAuthorizationContext(context, fs, path);
        Principal principal = ac.getPrincipal();
        AuthorizationInf auth = Wikiup.getModel(AuthorizationManager.class).getAuthorization(context);
        if((principal != null && auth.authenticate(principal) && principal.isSupervisor()) || methodName.equals("OPTIONS") || fs.hasPrivilege(path, destination != null ? destination : null, principal, methodName))
            doMethod(context, methodName);
        else {
            if(principal == null || !principal.validate() || principal.isAnonymous())
                auth.unauthorized();
            WebdavUtil.sendError(context, StatusUtil.SC_UNAUTHORIZED);
        }
    }

    private void doMethod(ServletProcessorContext context, String methodName) {
        Document entry = configure.getChild("method-handlers").getChild(methodName.toLowerCase());
        Assert.notNull(entry, ServiceNotImplementException.class, methodName);
        ActionUtil.doAction(context, entry);
    }

    public void aware(Document desc) {
        configure = desc;
    }
}
