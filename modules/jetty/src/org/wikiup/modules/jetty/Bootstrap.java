package org.wikiup.modules.jetty;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Response;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.ErrorHandler;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.webapp.WebAppContext;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class Bootstrap {
    private Server server;

    public Bootstrap() {
        server = new Server();
    }

    public void start(Configure conf) throws Exception {
        Connector connector = new SelectChannelConnector();
        connector.setPort(conf.getPort());
        server.setConnectors(new Connector[]{connector});

        String webroot = conf.getWebroot();
        String webapps = conf.getWebapps();

        if(webapps != null) {
            ContextHandlerCollection chc = new ContextHandlerCollection();
            File dir = new File(webapps);
            for(File file : dir.listFiles()) {
                String fileName = file.getName();
                if(file.isDirectory() && !fileName.startsWith(".")) {
                    String contextPath = fileName.equals("ROOT") ? "/" : '/' + fileName;
                    Context context = addWebappContext(conf, file.getAbsolutePath(), contextPath);
                    ContextHandler contextHandler = new ContextHandler(contextPath);
                    contextHandler.setHandler(context);
                    chc.addHandler(contextHandler);
                }
            }
            server.setHandler(chc);
        } else {
            Context context = addWebappContext(conf, webroot, conf.getContextPath());
            server.setHandler(context);
        }
        server.start();
        if(conf.isJoin())
            join();

    }

    private Context addWebappContext(Configure conf, String webroot, String contextPath) throws IOException {
        File dir = new File(webroot);
        if(dir.isDirectory()) {
            String resourceBase = dir.getCanonicalFile().toURI().toString();
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setResourceBase(resourceBase);

            WebAppContext webapp = new WebAppContext(resourceBase, contextPath);

            webapp.setDefaultsDescriptor(webroot + "/WEB-INF/web.xml");
            webapp.setErrorHandler(new WikiupErrorHandler(resourceHandler));

            if(conf.isEnableJsp())
                webapp.addServlet("org.apache.jasper.servlet.JspServlet", "*.jsp");

            return webapp;
        }
        return null;
    }

    public void join() throws InterruptedException {
        server.join();
    }

    public void stop() throws Exception {
        server.stop();
    }

    static class WikiupErrorHandler extends ErrorHandler {
        private Handler defaultHandler;

        public WikiupErrorHandler(Handler defaultHandler) {
            this.defaultHandler = defaultHandler;
        }

        @Override
        public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch)
                throws IOException {
            try {
                Request baseRequest = (request instanceof Request) ? (Request) request : HttpConnection.getCurrentConnection().getRequest();
                Response baseResponse = (request instanceof Response) ? (Response) response : HttpConnection.getCurrentConnection().getResponse();
                if(baseResponse.getStatus() == Response.SC_NOT_FOUND)
                    defaultHandler.handle(target, request, response, dispatch);
                if(!baseRequest.isHandled())
                    super.handle(target, request, response, dispatch);
            } catch(ServletException e) {
            }
        }
    }
}
