package org.wikiup.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.wikiup.core.Constants;
import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.bean.WikiupTypeTranslator;
import org.wikiup.core.exception.AttributeException;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.beancontainer.BeanContainerByTranslator;
import org.wikiup.core.impl.beancontainer.Singleton;
import org.wikiup.core.impl.context.ContextWrapper;
import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.impl.dictionary.StackDictionary;
import org.wikiup.core.impl.mp.CollectionModelProvider;
import org.wikiup.core.impl.releasable.TrashTin;
import org.wikiup.core.impl.setter.StackSetter;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ExceptionHandler;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.Entity;
import org.wikiup.database.orm.WikiupEntityManager;
import org.wikiup.database.util.EntityPath;
import org.wikiup.servlet.beans.ServletContextContainer;
import org.wikiup.servlet.exception.MissingRequestParameterException;
import org.wikiup.servlet.exception.ServiceNotImplementException;
import org.wikiup.servlet.impl.NullProcessor;
import org.wikiup.servlet.impl.context.CompositeProcessorContext;
import org.wikiup.servlet.impl.context.NamespaceProcessorContext;
import org.wikiup.servlet.impl.context.WikiupNamingDirectoryProcessorContext;
import org.wikiup.servlet.impl.eh.ServletExceptionHandler;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletConfigureMapping;
import org.wikiup.servlet.inf.ServletProcessor;
import org.wikiup.servlet.inf.ServletProcessorContextAware;
import org.wikiup.servlet.util.ActionUtil;


public class ServletProcessorContext implements ProcessorContext, ProcessorContext.ByParameters, BeanContainer, ExceptionHandler, Context<Object, Object> {
    private ProcessorContextContainer servletScope;
    private ProcessorContextContainer requestScope;

    static final private String[] WNDI_GLOBAL_CONTEXT = {"wk", "servlet", "context"};

    final private WikiupNamingDirectoryProcessorContext globalContext = new WikiupNamingDirectoryProcessorContext(WNDI_GLOBAL_CONTEXT);

    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;
    private Document servletContextConf;
    private Document requestContextConf;

    private Map<String, Object> extraParameter = new HashMap<String, Object>();
    private ServletExceptionHandler exceptionHandler;
    private ServletProcessorResponseBuffer responseBuffer = new ServletProcessorResponseBuffer();
    private String URI = null;
    private Context<Object, Object> varContext = new MapContext<Object>();
    private TrashTin tin = new TrashTin();
    private ResponseHeaderSetter headerSetter = new ResponseHeaderSetter(this);

    private BeanStack beanStack = new BeanStack();
    private BeanContainer beanContainer = new Singleton();

    public ServletProcessorContext(HttpServletRequest request, HttpServletResponse response) {
        init(request, response, request.getRequestURI());
    }

    public ServletProcessorContext(HttpServletRequest request, HttpServletResponse response, String uri) {
        init(request, response, uri);
    }

    public ServletProcessorContext(ServletProcessorContext base, String uri) {
        extraParameter = base.extraParameter;
        varContext = new ContextWrapper<Object, Object>(new StackDictionary<Object>().append(base.varContext, varContext), new StackSetter<Object>(base.varContext, varContext));
        headerSetter = null;
        init(base.servletRequest, base.servletResponse, uri);
    }

    private void init(HttpServletRequest request, HttpServletResponse response, String uri) {
        servletRequest = request;
        servletResponse = response;
        awaredBy(globalContext);
        setRequestURI(uri);
    }

    public void setAttribute(String name, Object obj) {
        Dictionaries.setProperty(varContext, StringUtil.splitNamespaces(name), obj);
    }

    public Object getAttribute(String name) {
        return Dictionaries.getProperty(varContext, StringUtil.splitNamespaces(name));
    }

    public void setRequestURI(String uri) {
        int pos = uri.indexOf('?');
        setRequestURI(pos == -1 ? uri : uri.substring(0, pos), pos == -1 ? null : uri.substring(pos + 1));
    }

    public void setRequestURI(String uri, String queryString) {
        ServletProcessorContextConfigure contextConfigure = ServletProcessorContextConfigure.getInstance();
        ServletConfigureMapping requestContext = contextConfigure.getRequestConfigure();
        parseQueryString(queryString);
        URI = getContextPath(StringUtil.first(uri, ';', 0));
        servletContextConf = contextConfigure.mapServletConfigure(this);
        Assert.notNull(servletContextConf, ServiceNotImplementException.class);
        requestContextConf = requestContext.map(this);
        requestContextConf = requestContextConf == null ? Null.getInstance() : requestContextConf;
        exceptionHandler = new ServletExceptionHandler(servletContextConf, requestContextConf);
    }

    public String getRequestURI() {
        return URI;
    }

    public String getRequestURL() {
        String query = servletRequest.getQueryString();
        StringBuffer url = servletRequest.getRequestURL();
        return query != null ? url.append('?').append(query).toString() : url.toString();
    }

    public String getRequestRoot() {
        String url = servletRequest.getRequestURL().toString();
        String uri = servletRequest.getRequestURI();
        return uri.equals("/") ? StringUtil.trimRight(url, "/") : url.substring(0, url.indexOf(uri));
    }

    public void doInit() {
        initContextModules();
        doSetHeader(getServletContextConf().getChild(Constants.Elements.HEADERS));
        doSetHeader(getRequestContextConf().getChild(Constants.Elements.HEADERS));
    }

    public ServletProcessor buildServletProcessor(Document cfg, ServletProcessor def) {
        ServletProcessor p = cfg == null ? def : Wikiup.getInstance().getBean(ServletProcessor.class, cfg);
        awaredBy(p);
        Interfaces.initialize(p, cfg);
        return p;
    }

    public ServletProcessor buildServletProcessor(Document cfg) {
        return buildServletProcessor(cfg, NullProcessor.getInstance());
    }

    public void awaredBy(Iterator<?> iterator) {
        while(iterator.hasNext())
            awaredBy(iterator.next());
    }

    public <E> E awaredBy(E obj) {
        ServletProcessorContextAware aware = Interfaces.unwrap(ServletProcessorContextAware.class, obj);
        if(aware != null)
            aware.setServletProcessorContext(this);
        return obj;
    }

    public void doProcess() {
        servletScope.doServletAction(this);
        requestScope.doServletAction(this);
        servletScope.doServletProcess(this);
        requestScope.doServletProcess(this);
    }

    public void abort() {
        responseBuffer.abort();
    }

    public void flush() {
        responseBuffer.flush(servletResponse);
    }

    public ServletProcessorResponseBuffer getResponseBuffer() {
        return responseBuffer;
    }

    private void doSetHeader(Document headers) {
        if(headers != null && headerSetter != null)
            Dictionaries.setProperties(headers, headerSetter, this);
    }

    public void setContentType(String value) {
        int pos = value.indexOf(";");
        servletResponse.setContentType(pos != -1 ? value.substring(0, pos) : value);
        if(pos != -1)
            servletResponse.setCharacterEncoding(StringUtil.shrinkLeft(value.substring(pos + 1).trim(), "charset="));
    }

    public StringWriter getResponseWriter() {
        return responseBuffer.getResponseWriter();
    }

    public ByteArrayOutputStream getResponseOutputStream() {
        return responseBuffer.getResponseStream();
    }

    public String getResponseText() {
        return responseBuffer.getResponseText();
    }

    private void initContextModules() {
        servletScope = new ProcessorContextContainer();
        requestScope = new ProcessorContextContainer();
        servletScope.init(this, getServletContextConf());
        requestScope.init(this, getRequestContextConf());
    }

    public ProcessorContext buildProcessorContext(Document desc) {
        if(desc != null) {
            ProcessorContext ctx = Wikiup.getInstance().getBean(ProcessorContext.class, desc, this);
            putToTrashTin(ctx);
            return ctx;
        }
        return null;
    }

    public boolean handle(Exception exp) {
        awaredBy(exceptionHandler);
        return exceptionHandler != null && exceptionHandler.handle(exp);
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public HttpSession getSession() {
        return servletRequest.getSession();
    }

    public Document getRequestContextConf() {
        return requestContextConf != null ? requestContextConf : Null.getInstance();
    }

    public Document getServletContextConf() {
        return servletContextConf;
    }

    @Override
    public Object get(String name, Dictionary<?> params) {
        Object obj = globalContext.get(name, params);
        obj = obj != null ? obj : servletScope.get(name, params);
        obj = obj != null ? obj : requestScope.get(name, params);
        obj = obj != null ? obj : beanStack.get(name, params);
        return obj != null ? obj : get(name);
    }

    @Override
    public Object get(String name) {
        Object value = getAttribute(name);
        return value != null ? value : Dictionaries.get(name, globalContext, servletScope, requestScope, beanStack);
    }

    @Override
    public void set(String name, Object value) {
        String[] path = StringUtil.splitNamespaces(name);
        if(path.length == 1)
            setAttribute(name, value);
        else {
            Dictionaries.setProperty(globalContext, path, value);
            Dictionaries.setProperty(servletScope, path, value);
            Dictionaries.setProperty(requestScope, path, value);
        }
    }

    public Map<String, Object> getExtraParameters() {
        return extraParameter;
    }

    public Object getExtraParameter(String name, Object defValue) {
        Object value = extraParameter.get(name);
        return value != null ? value : defValue;
    }

    public Entity getEntity(String name) {
        return getEntity(name, true);
    }

    public Entity getEntity(String name, boolean putToTrash) {
        Entity entity = WikiupEntityManager.getInstance().getEntity(name);
        if(putToTrash)
            putToTrashTin(entity);
        return entity;
    }

    private void putToTrashTin(Object obj) {
        if(obj instanceof Releasable)
            tin.put((Releasable) obj);
    }

    public void release() {
        tin.release();
        servletScope.release();
        requestScope.release();
    }

    public String getParameter(String name, String defValue) {
        String value = servletRequest.getParameter(name);
        return value != null ? value : ValueUtil.toString(getExtraParameter(name, null), defValue);
    }

    public String getParameter(String name) {
        String value = getParameter(name, null);
        return Assert.notNull(value, MissingRequestParameterException.class, name);
    }

    public boolean isPost() {
        return servletRequest.getMethod().equalsIgnoreCase("POST");
    }

    public boolean isGet() {
        return servletRequest.getMethod().equalsIgnoreCase("GET");
    }

    public Document getResponseXML() {
        return getResponseBuffer().getResponseXML();
    }

    public void setProperties(Document properties, Mutable<?> dest) {
        Dictionaries.setProperties(properties, dest, this);
    }

    public void setContextProperties(Document properties, Entity entity, Dictionary<?> dictionary) {
        Dictionaries.setProperties(properties, entity, dictionary);
    }

    public void setContextProperties(Document properties, Entity entity) {
        setContextProperties(properties, entity, this);
    }

    public String getContextAttribute(Document node, String name, String defValue) {
        try {
            String value = StringUtil.evaluateEL(Documents.getDocumentValue(node, name, defValue), this);
            return StringUtil.isEmpty(value) ? defValue : value;
        } catch(Exception ex) {
            return defValue;
        }
    }

    public String getContextAttribute(Document node, String name) {
        String value = getContextAttribute(node, name, null);
        Assert.notNull(value, AttributeException.class, node, name);
        return value;
    }

    @Deprecated
    public String getRealPath(String path) {
        return ServletContextContainer.getInstance().getRealPath(path);
    }

    @Deprecated
    public String getRealPathByURI(String uri) {
        return ServletContextContainer.getInstance().getRealPath(getContextPath(uri));
    }

    public Resource getResource(String uri) {
        return ServletContextContainer.getInstance().getResource(uri);
    }

    public String getContextPath(String uri) {
        String contextPath = StringUtil.trim(servletRequest.getContextPath(), "/") + '/';
        String uriPath = StringUtil.trimLeft(uri, "/");
        if(contextPath.length() > 1) {
            return uriPath.startsWith(contextPath) ? uriPath.substring(contextPath.length()) : uri;
        }
        return uri;
    }

    public String getContextURI(String uri) {
        String contextPath = servletRequest.getContextPath();
        StringBuilder buf = new StringBuilder();
        StringUtil.connect(buf, contextPath, '/');
        return StringUtil.connect(buf, getContextPath(uri), '/').toString();
    }

    public void parseQueryString(String queryString) {
        if(queryString != null) {
            String lines[] = queryString.split("&");
            Mutable<Object> mutable = new MapContext<Object>(extraParameter);
            for(String line : lines)
                Dictionaries.parseNameValuePair((Mutable) mutable, line, '=');
        }
    }

    public EntityPath getEntityPath(Document desc, Dictionary<?> dictionary) {
        EntityPath ePath = new EntityPath(Documents.getAttributeValue(desc, Constants.Attributes.ENTITY_PATH));
        Entity entity = getEntity(ePath.getEntityName());
        setContextProperties(desc, entity, dictionary);
        ePath.setEntity(entity, entity);
        return ePath;
    }

    public String getHeader(String name) {
        return servletRequest.getHeader(name);
    }

    public void setHeader(String name, String value) {
        servletResponse.setHeader(name, value);
    }

    public BeanStack getBeanStack() {
        return beanStack;
    }
    
    public BeanContainer getModelContainer() {
        return new BeanContainerImpl(this);
    }

    public String getRequestPath(boolean stripHandler) {
        String uri = getRequestURI();
        int idx = uri.lastIndexOf('.');
        String l = uri.lastIndexOf('/') < idx ? uri.substring(0, idx) : uri;
        if(stripHandler)
            for(char d : WikiupConfigure.HANDLER_DELIMETERS)
                l = StringUtil.first(l, d, 0);
        return l;
    }

    public File getAssociatedFile(String ext) {
        String uri = getRequestURI();
        String suffix = '.' + ext;
        String name = uri + suffix;
        String fileName = getRealPathByURI(name);
        if(!FileUtil.isExists(fileName) && !FileUtil.getFileExt(uri).equalsIgnoreCase(ext))
            fileName = getRealPathByURI(getRequestPath(true) + suffix);
        return new File(fileName);
    }

    public String getHandlerParameter(char delimter) {
        String path = getRequestPath(false);
        return StringUtil.second(path, delimter, 0);
    }

    @Override
    public <T> T query(Class<T> clazz) {
        return beanContainer.query(clazz);
    }

    private static class ResponseHeaderSetter implements Mutable<String> {
        private ServletProcessorContext context;

        public ResponseHeaderSetter(ServletProcessorContext context) {
            this.context = context;
        }

        public void set(String name, String value) {
            if(name.equalsIgnoreCase("content-type"))
                context.setContentType(value);
            else
                context.servletResponse.setHeader(name, value);
        }
    }

    private static class ProcessorContextContainer extends CompositeProcessorContext implements Releasable, Mutable<Object> {
        private Document configure;
        private NamespaceProcessorContext namespaces = new NamespaceProcessorContext();

        public ProcessorContextContainer() {
            append(namespaces);
        }

        public void init(ServletProcessorContext context, Document configure) {
            this.configure = configure;
            for(Document node : configure.getChildren(Constants.Elements.CONTEXT)) {
                String name = Documents.getId(node);
                ProcessorContext ctx = context.buildProcessorContext(node);
                Assert.notNull(ctx);
                context.awaredBy(ctx);
                if(name != null)
                    namespaces.append(name, ctx);
                else
                    append(ctx);
                Interfaces.initialize(ctx, node);
            }
        }

        private void doServletAction(ServletProcessorContext context) {
            doServletAction(context, Constants.Elements.ACTION);
        }

        private void doServletAction(ServletProcessorContext context, String confEntry) {
            for(Document desc : configure.getChildren(confEntry))
                ActionUtil.doAction(context, desc);
        }

        private void doServletProcess(ServletProcessorContext context) {
            for(Document node : configure.getChildren(Constants.Elements.PROCESSOR)) {
                ServletProcessor processor = context.buildServletProcessor(node, null);
                if(processor != null)
                    processor.process(context);
            }
        }

        public void release() {
            Interfaces.releaseAll(getContexts());
            getContexts().clear();
            configure = Null.getInstance();
        }

        public void set(String name, Object obj) {
            namespaces.set(name, obj);
        }
    }

    private static class BeanContainerImpl implements BeanContainer {
        private CollectionModelProvider modelContainer = new CollectionModelProvider();

        public BeanContainerImpl(ServletProcessorContext context) {
            modelContainer.append(context.getServletRequest(), context.getServletResponse(), context.getResponseWriter(), context.getResponseOutputStream(), context);
        }

        public <E> E query(Class<E> clazz) {
            E e = modelContainer.query(clazz);
            return e != null ? e : Wikiup.getModel(clazz);
        }
    }
    
    public static class BeanStack implements BeanContainer, Dictionary<Object>, ProcessorContext.ByParameters {
        private Stack<BeanContainer> stack = new Stack<BeanContainer>();
        private WikiupTypeTranslator translator = Wikiup.getModel(WikiupTypeTranslator.class);
        
        public void push(Object obj) {
            BeanContainer beanContainer = Interfaces.cast(BeanContainer.class, obj);
            if(beanContainer == null)
                beanContainer = new BeanContainerByTranslator(obj, translator);
            stack.push(beanContainer);
        }
        
        public BeanContainer pop() {
            return stack.pop();
        }
        
        public <T> T peek(Class<T> clazz) {
            BeanContainer beanContainer = stack.peek();
            return beanContainer != null ? beanContainer.query(clazz) : null;
        }

        @Override
        public Object get(String name, Dictionary<?> params) {
            ProcessorContext.ByParameters byParameters = query(ProcessorContext.ByParameters.class);
            return byParameters != null ? byParameters.get(name, params) : null;
        }

        @Override
        public Object get(String name) {
            Dictionary<?> dictionary = query(Dictionary.class);
            return dictionary != null ? dictionary.get(name) : null;
        }

        @Override
        public <T> T query(Class<T> clazz) {
            ListIterator<BeanContainer> iterator = stack.listIterator(stack.size());
            while(iterator.hasPrevious()) {
                BeanContainer beanContainer = iterator.previous();
                T inst = beanContainer.query(clazz);
                if(inst != null)
                    return inst;
            }
            return null;
        }
    }
}
