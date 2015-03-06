package org.wikiup.modules.groovy.action;

import groovy.lang.GroovyShell;
import org.wikiup.core.impl.getter.ModelContainerDictionary;
import org.wikiup.core.impl.getter.StackDictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.groovy.GroovyShellPool;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

import java.io.File;
import java.io.IOException;

public class GroovyShellServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        executeShell(context, desc, desc);
        for(Document node : desc.getChildren("script"))
            executeShell(context, node, desc);
    }

    private void executeShell(ServletProcessorContext context, Document desc, Document properties) {
        String src = Documents.getDocumentValue(desc, "src");
        Dictionary<Object> dictionary = new StackDictionary<Object>().append(context, new ModelContainerDictionary(context.getModelContainer()));
        if(src != null) {
            final GroovyShell shell = GroovyShellPool.getInstance().get(Documents.getDocumentValue(desc, "scope", null));
            ContextUtil.setProperties(properties, new Dictionary.Mutable<Object>() {
                public void set(String name, Object obj) {
                    shell.setProperty(name, obj);
                }
            }, dictionary);
            try {
                shell.evaluate(new File(StringUtil.evaluateEL(src, context)));
            } catch(IOException e) {
                Assert.fail(e);
            }
        }
    }
}
