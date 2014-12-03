package org.wikiup.modules.groovy.action;

import groovy.lang.GroovyShell;
import org.wikiup.core.impl.getter.ModelContainerGetter;
import org.wikiup.core.impl.getter.StackGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Setter;
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
        Getter<Object> getter = new StackGetter<Object>().append(context, new ModelContainerGetter(context.getModelContainer()));
        if(src != null) {
            final GroovyShell shell = GroovyShellPool.getInstance().get(Documents.getDocumentValue(desc, "scope", null));
            ContextUtil.setProperties(properties, new Setter<Object>() {
                public void set(String name, Object obj) {
                    shell.setProperty(name, obj);
                }
            }, getter);
            try {
                shell.evaluate(new File(StringUtil.evaluateEL(src, context)));
            } catch(IOException e) {
                Assert.fail(e);
            }
        }
    }
}
