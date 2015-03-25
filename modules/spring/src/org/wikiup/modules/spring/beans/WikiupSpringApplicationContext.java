package org.wikiup.modules.spring.beans;

import org.springframework.context.support.AbstractXmlApplicationContext;
import org.wikiup.framework.bootstrap.Bootstrap;
import org.wikiup.framework.bootstrap.inf.ResourceHandler;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.iterable.ArrayItems;
import org.wikiup.core.impl.mp.InstanceModelProvider;
import org.wikiup.core.impl.translator.lf.RegexpMatchLogicalTranslator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.LogicalTranslator;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.Documents;
import org.wikiup.modules.spring.SpringResource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WikiupSpringApplicationContext extends AbstractXmlApplicationContext implements ModelFactory, Iterable<String>, ResourceHandler, DocumentAware {
    private List<SpringResource> resources = new ArrayList<SpringResource>();
    private ArrayItems<String> definitionNames;

    public void handle(Resource resource) {
        resources.add(new SpringResource(resource));
    }

    public void finish() {
        refresh();
        definitionNames = new ArrayItems<String>(getBeanDefinitionNames());
    }

    public BeanContainer get(String name) {
        Object bean = getBean(name);
        return bean != null ? new InstanceModelProvider(bean) : null;
    }

    @Override
    protected org.springframework.core.io.Resource[] getConfigResources() {
        return resources.toArray(new org.springframework.core.io.Resource[resources.size()]);
    }

    public Iterator<String> iterator() {
        return definitionNames != null ? definitionNames.iterator() : Null.getInstance();
    }

    public void aware(Document desc) {
        for(Document node : desc.getChildren("configure-location")) {
            String location = Documents.getDocumentValue(node);
            LogicalTranslator<String> filter = new RegexpMatchLogicalTranslator(location);
            Bootstrap.getInstance().getBootstrapResource().loadResources(this, filter);
        }
    }
}
