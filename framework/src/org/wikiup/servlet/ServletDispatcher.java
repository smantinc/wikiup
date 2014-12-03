package org.wikiup.servlet;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bootstrap.Bootstrap;
import org.wikiup.core.util.Interfaces;
import org.wikiup.servlet.beans.ServletContextContainer;
import org.wikiup.servlet.exception.ServiceNotImplementException;
import org.wikiup.servlet.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class ServletDispatcher extends HttpServlet {
    static final long serialVersionUID = 2415824383601438004L;

    @Override
    public void init() throws ServletException {
        try {
            ServletContextContainer.getInstance().setServletContext(this.getServletContext());
            Bootstrap.getInstance().bootstrap(false);
        } catch(Exception ex) {
            throw new ServletException(ex);
        }
    }

    protected boolean doService(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletProcessorContext context = null;
        try {
            context = new ServletProcessorContext(request, response);
            context.doInit();
            context.doProcess();
            context.flush();
        } catch(ServiceNotImplementException ex) {
            return false;
        } catch(Exception e) {
            Exception ex = Interfaces.stripException(e);
            if(!Interfaces.handleException(context, ex))
                if(ex instanceof IOException)
                    throw ((IOException) ex);
                else
                    throw new ServletException(ex);
        } finally {
            if(context != null)
                context.release();
        }
        return true;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if(!doService(req, resp))
            super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletUtil.doGet(request, response);
    }

    @Override
    protected long getLastModified(HttpServletRequest request) {
        File file = new File(getServletContext().getRealPath(ServletUtil.getResourcePath(request)));
        if(file.exists())
            return file.lastModified();
        return super.getLastModified(request);
    }

    @Override
    public void destroy() {
        super.destroy();
        Wikiup.getInstance().release();
    }
}
