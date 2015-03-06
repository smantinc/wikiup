package org.wikiup.plugins.wmdk.context;

import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.getter.dl.ByAttributeNameSelector;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;
import org.wikiup.plugins.wmdk.util.WMDKUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.impl.context.ProcessorContextSupport;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.util.Iterator;

public class ResourceExportProcessorContext extends ProcessorContextSupport implements ServletProcessorContextAware {
    private ServletProcessorContext context;

    public Iterator<Document> getPlugins() {
        Document doc = WMDKUtil.copyPluginList(Documents.create("root"));
        Document plugins = WMDKUtil.getPluginConfigure(context);
        Dictionary<Document> dictionary = new ByAttributeNameSelector(plugins != null ? plugins : Null.getInstance(), "name");
        for(Document node : doc.getChildren()) {
            String module = Documents.getAttributeValue(node, "name");
            Document plugin = dictionary.get(module);
            if(plugin != null)
                Documents.setAttributeValue(node, "disabled", Documents.getAttributeBooleanValue(plugin, "disabled", false));
        }
        return doc.getChildren().iterator();
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
