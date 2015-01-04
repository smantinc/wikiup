package org.wikiup.extensions.wmdk.aquacure;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.impl.wndi.DefaultWikiupNamingDirectory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ConsoleServletAction {
    static final private String PWD_COOKIE_NAME = "_wmdk_console_pwd";

    public Document ls(ServletProcessorContext context) {
        Document doc = Documents.create("root");
        String path = context.getParameter("arg", null);
        path = StringUtil.isEmpty(path) ? getPwd(context.getServletRequest()) : path;
        WikiupNamingDirectory dir = WikiupNamingDirectory.getInstance();
        folderList(StringUtil.isEmpty(path) ? dir : dir.get(path), doc);
        return doc;
    }

    public Map<String, Object> pwd(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        String pwd = getPwd(request);
        map.put("pwd", StringUtil.isEmpty(pwd) ? "/" : pwd);
        return map;
    }

    public void cd(HttpServletRequest request, HttpServletResponse response) {
        String path = StringUtil.trim(request.getParameter("arg"), " /\r\n\t");
        if(path != null)
            if("..".equals(StringUtil.trim(path, "/\\ "))) {
                String pwd = StringUtil.trim(ValueUtil.toString(getPwd(request), ""), "/\\ ");
                int idx = pwd.lastIndexOf('/');
                response.addCookie(new Cookie(PWD_COOKIE_NAME, idx != -1 ? pwd.substring(0, idx) : ""));
            } else {
                String cd = StringUtil.trim(path.startsWith("/") ? path : StringUtil.connect(ValueUtil.toString(getPwd(request), ""), path, '/'), "/\\");
                if(Wikiup.getInstance().get(Getter.class, cd) != null)
                    response.addCookie(new Cookie(PWD_COOKIE_NAME, cd));
            }
    }

    public Map<String, Object> get(ServletProcessorContext context) {
        String path = context.getParameter("arg", null);
        Map<String, Object> map = new HashMap<String, Object>();
        Getter<Object> dir = getPwdContext(context);
        Object obj = getContent(path, dir);
        map.put("value", ValueUtil.toString(obj));
        return map;
    }

    public Map<String, Object> type(ServletProcessorContext context) {
        String path = context.getParameter("arg", null);
        Map<String, Object> map = new HashMap<String, Object>();
        Getter<Object> dir = getPwdContext(context);
        Object obj = getContent(path, dir);
        map.put("value", obj != null ? obj.getClass().toString() : null);
        return map;
    }

    private Object getContent(String path, Getter<Object> dir) {
        return StringUtil.isEmpty(path) ? dir : (path.startsWith("/") ? Wikiup.getInstance().get(path) : dir.get(StringUtil.trim(path, " \t'\"")));
    }

    private void folderList(Object folder, Document doc) {
        Getter<Object> getter = Interfaces.cast(Getter.class, folder);
        Iterable<Object> iterable = Interfaces.cast(Iterable.class, folder);
        if(getter != null && iterable != null)
            for(Object name : iterable) {
                Document item = doc.addChild("item");
                Object obj = getter.get(name != null ? name.toString() : null);
                Documents.setAttributeValue(item, "name", name);
                if(obj != null && !(obj instanceof DefaultWikiupNamingDirectory))
                    Documents.setAttributeValue(item, "class", obj.getClass());
                Documents.setAttributeValue(item, "folder", obj instanceof Getter);
            }
    }

    private String getPwd(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null)
            for(Cookie cookie : cookies)
                if(PWD_COOKIE_NAME.equals(cookie.getName()))
                    return cookie.getValue();
        return null;
    }

    private Getter<Object> getPwdContext(ServletProcessorContext context) {
        String pwd = StringUtil.trim(getPwd(context.getServletRequest()), "/");
        return pwd != null ? context.awaredBy(Wikiup.getInstance().get(Getter.class, pwd)) : null;
    }
}
