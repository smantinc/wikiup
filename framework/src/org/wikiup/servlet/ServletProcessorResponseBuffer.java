package org.wikiup.servlet;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class ServletProcessorResponseBuffer implements BeanFactory, Releasable {
    private Map<Class<?>, Object> values = new HashMap<Class<?>, Object>();

    private StringWriter responseWriter;
    private ByteArrayOutputStream responseStream;
    private boolean aborted;

    public void abort() {
        aborted = true;
    }

    public StringWriter getResponseWriter() {
        if(aborted)
            return new StringWriter();
        setString(responseWriter == null ? null : responseWriter.toString());
        return responseWriter = new StringWriter();
    }

    public ByteArrayOutputStream getResponseStream() {
        if(aborted)
            return new ByteArrayOutputStream();
        setBytes(responseStream == null ? null : responseStream.toByteArray());
        return responseStream = new ByteArrayOutputStream();
    }

    public String getResponseText(boolean release) {
        return release ? getString() : (responseWriter != null ? responseWriter.toString() : getString());
    }

    public String getResponseText() {
        return getResponseText(true);
    }

    public byte[] getResponseBytes(boolean release) {
        return release ? getBytes() : (responseStream != null ? responseStream.toByteArray() : getBytes());
    }

    public byte[] getResponseBytes() {
        return getResponseBytes(true);
    }

    public void release() {
        responseWriter = null;
        responseStream = null;
    }

    public void flush(HttpServletResponse response) {
        try {
            String text = responseWriter != null ? responseWriter.toString() : null;
            if(!StringUtil.isEmpty(text))
                response.getWriter().print(text);
            else if(responseStream != null)
                response.getOutputStream().write(responseStream.toByteArray());
        } catch(IOException ex) {
        }
        release();
    }

    public void copy(ServletProcessorResponseBuffer buffer) {
        String text = buffer.getResponseText();
        if(text != null)
            responseWriter.write(text);
        else {
            byte bytes[] = buffer.getResponseBytes();
            if(bytes != null)
                responseStream.write(bytes, 0, bytes.length);
        }
    }

    public Document getResponseXML(String name, boolean autoCreate) {
        if(getDocument() == null && autoCreate)
            setDocument(Documents.create(name));
        return getDocument();
    }

    public Document getResponseXML() {
        return getResponseXML("root", true);
    }

    public <E> E query(Class<E> clazz) {
        E instance = null;
        for(Object obj : values.values())
            if((instance = Interfaces.cast(clazz, obj)) != null)
                return instance;
        return null;
    }

    public <E> E getObject(Class<E> clazz) {
        return Interfaces.cast(clazz, values.get(clazz));
    }

    public <E> void setObject(Class<E> clazz, E instance) {
        values.put(clazz, instance);
    }

    public String getString() {
        return ValueUtil.toString(getObject(String.class), "");
    }

    public void setString(String string) {
        setObject(String.class, string);
    }

    public byte[] getBytes() {
        return getObject(byte[].class);
    }

    public void setBytes(byte[] bytes) {
        setObject(byte[].class, bytes);
    }

    public Document getDocument() {
        return getObject(Document.class);
    }

    public void setDocument(Document doc) {
        setObject(Document.class, doc);
    }

    public Resource getResource() {
        return getObject(Resource.class);
    }

    public void setResource(Resource resource) {
        setObject(Resource.class, resource);
    }
}
