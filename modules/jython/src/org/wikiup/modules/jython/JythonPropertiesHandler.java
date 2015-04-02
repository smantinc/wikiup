package org.wikiup.modules.jython;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.python.core.PySystemState;
import org.wikiup.core.impl.dictionary.NoNullDictionary;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.framework.bean.WikiupNamingDirectory;
import org.wikiup.framework.bootstrap.inf.ResourceHandler;
import org.wikiup.modules.jython.util.PythonClassLoader;

public class JythonPropertiesHandler implements ResourceHandler {
    private Properties properties = new Properties();

    public void handle(Resource resource) {
        InputStream is = null;
        try {
            Properties p = new Properties();
            is = resource.open();
            p.load(is);
            Dictionary<?> dict = new NoNullDictionary<Object>(WikiupNamingDirectory.getInstance());
            for(Object key : p.keySet())
                properties.setProperty(key.toString(), StringUtil.evaluateEL(p.getProperty(key.toString()), dict));
        } catch(IOException ex) {
            Assert.fail(ex);
        } finally {
            StreamUtil.close(is);
        }
    }

    public void finish() {
        PySystemState.initialize(System.getProperties(), properties, null, new PythonClassLoader(Thread.currentThread().getContextClassLoader()));
    }
}
