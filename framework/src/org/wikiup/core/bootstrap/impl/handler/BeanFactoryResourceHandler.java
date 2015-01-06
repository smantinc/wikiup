package org.wikiup.core.bootstrap.impl.handler;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupBeanFactory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.Resource;

public class BeanFactoryResourceHandler extends DirectoryDocumentResourceHandler {

    private WikiupBeanFactory beanFactory = Wikiup.getModel(WikiupBeanFactory.class);

    @Override
    protected void loadDirectoryResource(Resource resource, Document doc, String[] path) {
        beanFactory.loadBeans(doc);
    }
}