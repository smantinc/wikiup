package org.wikiup.plugins.wmdk.context;

import java.io.File;
import java.io.FilenameFilter;

import org.wikiup.core.impl.mp.DocumentModelProvider;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class JspTagLibProcessorContext implements ProcessorContext, ServletProcessorContextAware {
    private ServletProcessorContext context;

    @Override
    public BeanContainer get(String name) {
        String uri = name;
        File path = new File(context.getRealPath(uri));
        Document doc = Documents.create("taglibs");
        if(path.exists() && path.isDirectory()) {
            File[] files = path.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".tld");
                }
            });
            for(File file : files) {
                Document node = doc.addChild("taglib");
                Documents.setAttributeValue(node, "uri", StringUtil.connect(uri, file.getName(), '/'));
            }
        }
        return new DocumentModelProvider(doc);
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
