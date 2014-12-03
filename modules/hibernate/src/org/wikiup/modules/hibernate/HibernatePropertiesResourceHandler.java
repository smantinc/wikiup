package org.wikiup.modules.hibernate;

import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.inf.Resource;
import org.wikiup.core.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernatePropertiesResourceHandler implements ResourceHandler {
    public void handle(Resource resource) {
        InputStream is = null;
        Properties properties = new Properties();
        try {
            try {
                is = resource.open();
                properties.load(is);
                HibernateEntityManager.getInstance().getConfiguration().setProperties(properties);
            } finally {
                if(is != null)
                    is.close();
            }
        } catch(IOException ex) {
            Assert.fail(ex);
        }
    }

    public void finish() {
    }
}
