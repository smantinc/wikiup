package org.wikiup.modules.lucene.context;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.mp.IterableModelProvider;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.modules.lucene.Lucene;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class QueryResultProcessorContext implements ServletProcessorContextAware, ProcessorContext, DocumentAware {
    private ServletProcessorContext context;
    private Query query;
    private int pageSize;

    private Lucene lucene = Wikiup.getModel(Lucene.class);

    public void aware(Document desc) {
        pageSize = Documents.getAttributeIntegerValue(desc, "page-size", 20);
        List<String> fields = new LinkedList<String>();
        List<BooleanClause.Occur> occurs = new LinkedList<BooleanClause.Occur>();
        for(Document node : desc.getChildren("field")) {
            fields.add(Documents.getId(node));
            occurs.add(BooleanClause.Occur.valueOf(Documents.getAttributeValue(node, "occur", "SHOULD").toUpperCase()));
        }
        try {
            query = MultiFieldQueryParser.parse(lucene.getVersion(), context.getContextAttribute(desc, "query"), fields.toArray(new String[fields.size()]), occurs.toArray(new BooleanClause.Occur[occurs.size()]), getAnalyzer());
        } catch(ParseException e) {
            Assert.fail(e);
        }
    }

    public Analyzer getAnalyzer() {
        return new StandardAnalyzer(lucene.getVersion());
    }

    public Object get(String name) {
        return null;
    }

    public BeanFactory getModelContainer(String name, Getter<?> params) {
        Lucene lucene = Wikiup.getModel(Lucene.class);
        try {
            return new IterableModelProvider(lucene.search(name, query, pageSize));
        } catch(IOException e) {
            Assert.fail(e);
        }
        return null;
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
