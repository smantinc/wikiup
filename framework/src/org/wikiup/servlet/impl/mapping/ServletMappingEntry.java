package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.impl.document.MergedDocument;
import org.wikiup.core.impl.translator.lf.AndLogicalTranslator;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.impl.mapping.filter.RequestMethodTranslator;

public class ServletMappingEntry extends AndLogicalTranslator<ServletProcessorContext> {
    private Document document;
    private boolean isAbstract;
    private ServletMappingEntry extendedFrom;

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public ServletMappingEntry(Document document) {
        String forMethod = Documents.getDocumentValue(document, "for-method", null);
        this.document = document;
        if(forMethod != null)
            addFilter(new RequestMethodTranslator(forMethod));
    }

    public String getSuperName() {
        return Documents.getAttributeValue(document, "extends", null);
    }

    public String getURIPattern() {
        String uriPattern = Documents.getDocumentValue(document, "uri-pattern", null);
        return uriPattern != null ? uriPattern : (extendedFrom != null ? extendedFrom.getURIPattern() : null);
    }

    @Override
    public Boolean translate(ServletProcessorContext context) {
        return super.translate(context) && (extendedFrom != null ? extendedFrom.translate(context) : true);
    }

    public Document getDocument() {
        return extendedFrom != null ? new MergedDocument(document, extendedFrom.getDocument()) : document;
    }

    public String getName() {
        return Documents.getId(document);
    }

    public void extend(ServletMappingEntry sup) {
        extendedFrom = sup;
        if(StringUtil.compare(getURIPattern(), sup.getURIPattern()))
            sup.setAbstract(true);
    }
}
