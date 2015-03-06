package org.wikiup.modules.jsp;

import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.servlet.ServletProcessorContext;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

public class JspHttpServletResponse implements HttpServletResponse {
    private HttpServletResponse response;
    private ServletProcessorContext context;
    private ExpressionLanguage<Dictionary<?>, String> el;

    public JspHttpServletResponse(HttpServletResponse response, ServletProcessorContext context, ExpressionLanguage<Dictionary<?>, String> el) {
        this.response = response;
        this.context = context;
        this.el = el;
    }

    public void addCookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    public boolean containsHeader(String s) {
        return response.containsHeader(s);
    }

    public String encodeURL(String s) {
        return response.encodeURL(s);
    }

    public String encodeRedirectURL(String s) {
        return response.encodeRedirectURL(s);
    }

    public String encodeUrl(String s) {
        return response.encodeUrl(s);
    }

    public String encodeRedirectUrl(String s) {
        return response.encodeRedirectUrl(s);
    }

    public void sendError(int i, String s) throws IOException {
        response.sendError(i, s);
    }

    public void sendError(int i) throws IOException {
        response.sendError(i);
    }

    public void sendRedirect(String s) throws IOException {
        response.sendRedirect(s);
    }

    public void setDateHeader(String s, long l) {
        response.setDateHeader(s, l);
    }

    public void addDateHeader(String s, long l) {
        response.addDateHeader(s, l);
    }

    public void setHeader(String s, String s1) {
        response.setHeader(s, s1);
    }

    public void addHeader(String s, String s1) {
        response.addHeader(s, s1);
    }

    public void setIntHeader(String s, int i) {
        response.setIntHeader(s, i);
    }

    public void addIntHeader(String s, int i) {
        response.addIntHeader(s, i);
    }

    public void setStatus(int i) {
        response.setStatus(i);
    }

    public void setStatus(int i, String s) {
        response.setStatus(i, s);
    }

    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    public String getContentType() {
        return response.getContentType();
    }

    public ServletOutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        Writer writer = new JspHttpServletResponseWriter();
        return new PrintWriter(writer);
    }

    public void setCharacterEncoding(String s) {
        response.setCharacterEncoding(s);
    }

    public void setContentLength(int i) {
        response.setContentLength(i);
    }

    public void setContentType(String s) {
        response.setContentType(s);
    }

    public void setBufferSize(int i) {
        response.setBufferSize(i);
    }

    public int getBufferSize() {
        return response.getBufferSize();
    }

    public void flushBuffer() throws IOException {
        response.flushBuffer();
    }

    public void resetBuffer() {
        response.resetBuffer();
    }

    public boolean isCommitted() {
        return response.isCommitted();
    }

    public void reset() {
        response.reset();
    }

    public void setLocale(Locale locale) {
        response.setLocale(locale);
    }

    public Locale getLocale() {
        return response.getLocale();
    }

    private class JspHttpServletResponseWriter extends Writer {
        private Writer writer;

        public JspHttpServletResponseWriter() {
            writer = context.getResponseWriter();
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
            writer.write(el.evaluate(context, new String(cbuf, off, len)));
        }

        @Override
        public void flush() throws IOException {
            writer.flush();
        }

        @Override
        public void close() throws IOException {
            writer.close();
        }
    }
}
