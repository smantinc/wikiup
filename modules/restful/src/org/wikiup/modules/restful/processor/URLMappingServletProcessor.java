package org.wikiup.modules.restful.processor;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.getter.dl.ByAttributeNameSelector;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletProcessor;

public class URLMappingServletProcessor implements ServletProcessor {
    private static Document MAPPING = WikiupConfigure.getInstance().lookup("wk/url-mapping");
    private static Dictionary<Document> MAPPING_PATTERNS = MAPPING != null ? new ByAttributeNameSelector(MAPPING, "name", "pattern") : Null.getInstance();

    public void process(ServletProcessorContext context) {
        String uri = context.getRequestURI();
        mapPattern(context, uri);
    }

    private void mapPattern(ServletProcessorContext context, String uri) {
        for(Document node : MAPPING.getChildren("mapping"))
            if(map(context, uri, Documents.getDocumentValue(node, "from"), Documents.getDocumentValue(node, "to")))
                return;
    }

    private boolean map(ServletProcessorContext context, String uri, String from, String to) {
        Document f = MAPPING_PATTERNS.get(from);
        Document t = MAPPING_PATTERNS.get(to);
        if(f != null && t != null) {
            Object[] vars = StringUtil.scan(StringUtil.trim(Documents.getDocumentValue(f, "uri"), "/"), StringUtil.trim(uri, "/"));
            if(vars != null) {
                ServletProcessorContext c = new ServletProcessorContext(context, StringUtil.format(Documents.getDocumentValue(t, "uri"), vars));
                c.doInit();
                c.doProcess();
                c.release();
                context.getResponseWriter().write(c.getResponseBuffer().getResponseText(false));
                return true;
            }
        }
        return false;
    }
}
