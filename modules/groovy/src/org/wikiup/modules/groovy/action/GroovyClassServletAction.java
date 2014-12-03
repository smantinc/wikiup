package org.wikiup.modules.groovy.action;

import groovy.lang.GroovyClassLoader;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

import java.io.File;
import java.io.IOException;

public class GroovyClassServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        GroovyClassLoader gcl = new GroovyClassLoader();
        try {
            Class clazz = gcl.parseClass(new File(StringUtil.evaluateEL(Documents.getDocumentValue(desc, "src"), context)));
            String target = Documents.getDocumentValue(desc, "target", null);
            if(target != null)
                context.set(target, clazz);
        } catch(IOException e) {
            Assert.fail(e);
        }
    }
}