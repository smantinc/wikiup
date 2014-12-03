package org.wikiup.servlet.impl.jndi;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import java.util.Hashtable;

public class WikiupNamingDirectoryContextFactory implements InitialContextFactory {
    public Context getInitialContext(Hashtable<?, ?> environment) throws NamingException {
        return new WikiupNamingDirectoryContext();
    }
}
