package org.wikiup.modules.hibernate;

import org.wikiup.core.bootstrap.Bootstrap;
import org.wikiup.core.bootstrap.inf.ResourceHandler;
import org.wikiup.core.inf.Resource;

public class HibernateEntityResourceHandler implements ResourceHandler {
    public void finish() {
        Bootstrap.onReady(new Runnable() {
            public void run() {
                HibernateEntityManager.getInstance().buildSessionFactory();
            }
        });
    }

    public void handle(Resource resource) {
        try {
            HibernateEntityManager.getInstance().loadResource(resource);
        } catch(Exception e) {
        }
    }
}
