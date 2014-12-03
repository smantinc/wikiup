package org.wikiup.servlet.impl.jndi;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupNamingDirectory;
import org.wikiup.core.util.StringUtil;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.util.Hashtable;

public class WikiupNamingDirectoryContext implements Context {
    public WikiupNamingDirectoryContext() {
    }

    public Object addToEnvironment(String propName, Object propVal) throws NamingException {
        return propVal;
    }

    public void bind(String name, Object obj) throws NamingException {
        WikiupNamingDirectory.getInstance().set(name, obj);
    }

    public void bind(Name name, Object obj) throws NamingException {
        WikiupNamingDirectory.getInstance().set(name.toString(), obj);
    }

    public void close() throws NamingException {
    }

    public Name composeName(Name name, Name prefix) throws NamingException {
        return null;
    }

    public String composeName(String name, String prefix) throws NamingException {
        return StringUtil.connect(name, prefix, '/');
    }

    public Context createSubcontext(String name) throws NamingException {
        return this;
    }

    public Context createSubcontext(Name name) throws NamingException {
        return this;
    }

    public void destroySubcontext(Name name) throws NamingException {
    }

    public void destroySubcontext(String name) throws NamingException {
    }

    public Hashtable getEnvironment() throws NamingException {
        return null;
    }

    public String getNameInNamespace() throws NamingException {
        return "";
    }

    public NameParser getNameParser(Name name) throws NamingException {
        return null;
    }

    public NameParser getNameParser(String name) throws NamingException {
        return null;
    }

    public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
        return null;
    }

    public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
        return null;
    }

    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        return null;
    }

    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        return null;
    }

    public Object lookup(String name) throws NamingException {
        return Wikiup.getInstance().get(name);
    }

    public Object lookup(Name name) throws NamingException {
        return lookup(name.toString());
    }

    public Object lookupLink(Name name) throws NamingException {
        return null;
    }

    public Object lookupLink(String name) throws NamingException {
        return null;
    }

    public void rebind(Name name, Object obj) throws NamingException {
        bind(name, obj);
    }

    public void rebind(String name, Object obj) throws NamingException {
        bind(name, obj);
    }

    public Object removeFromEnvironment(String propName) throws NamingException {
        return null;
    }

    public void rename(Name oldName, Name newName) throws NamingException {
    }

    public void rename(String oldName, String newName) throws NamingException {
    }

    public void unbind(String name) throws NamingException {
    }

    public void unbind(Name name) throws NamingException {
    }
}
