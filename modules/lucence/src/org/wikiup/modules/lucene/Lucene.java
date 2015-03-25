package org.wikiup.modules.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wikiup.core.Wikiup;
import org.wikiup.framework.bean.WikiupDynamicSingleton;
import org.wikiup.framework.bean.WikiupNamingDirectory;
import org.wikiup.core.impl.setter.BeanPropertySetter;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.Documents;
import org.wikiup.modules.lucene.util.LuceneDocument;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

public class Lucene extends WikiupDynamicSingleton<Lucene> {

    private String repository;
    private Version version;
    private Dictionary<Analyzer> analyzers;

    @Override
    public void firstBuilt() {
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getRepository() {
        return repository;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = Version.valueOf(version);
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setAnalyzers(Dictionary<Analyzer> analyzers) {
        this.analyzers = analyzers;
    }

    public Analyzer getAnalyzer(String name) {
        return analyzers != null ? analyzers.get(name) : null;
    }

    public Dictionary<Analyzer> getAnalyzers() {
        return analyzers;
    }

    public IndexSearcher getIndexSearcher(String path) throws IOException {
        return new IndexSearcher(FSDirectory.open(new File(repository, path)));
    }

    public IndexWriter getIndexWriter(String path, Analyzer analyzer) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34, analyzer != null ? analyzer : new StandardAnalyzer(Version.LUCENE_34));
        return new IndexWriter(FSDirectory.open(new File(repository, path)), config);
    }

    public Iterable<Document> search(String path, Query query, int n) throws IOException {
        IndexSearcher indexSearch = getIndexSearcher(path);
        List<Document> docs = new LinkedList<Document>();
        for(ScoreDoc scoreDoc : indexSearch.search(query, n).scoreDocs) {
            org.apache.lucene.document.Document doc = indexSearch.doc(scoreDoc.doc);
            docs.add(new LuceneDocument(doc));
        }
        return docs;
    }

    public void index(String path, Analyzer analyzer, Document doc) throws IOException {
        IndexWriter writer = getIndexWriter(path, analyzer);
        index(writer, doc, analyzer);
        writer.commit();
        writer.close();
    }

    public void index(String path, Analyzer analyzer, Iterable<Document> docs, int pageSize) throws IOException {
        IndexWriter writer = getIndexWriter(path, analyzer);
        int counter = 0;
        for(Document doc : docs) {
            index(writer, doc, analyzer);
            if(++counter > pageSize) {
                counter = 0;
                writer.commit();
            }
        }
        writer.commit();
        writer.close();
    }

    private void index(IndexWriter writer, Document doc, Analyzer analyzer) throws IOException {
        Reader reader = null;
        org.apache.lucene.document.Document ldoc = new org.apache.lucene.document.Document();
        Term term = null;
        for(Document node : doc.getChildren()) {
            boolean primaryKey = Documents.getAttributeBooleanValue(node, "lucene:primary-key", false);
            boolean store = Documents.getAttributeBooleanValue(node, "lucene:store", true);
            String index = Documents.getAttributeValue(node, "lucene:index", "ANALYZED");
            String value = Documents.getAttributeValue(node, "value", null);
            if(value != null) {
                String id = Documents.getId(node);
                ldoc.add(new Field(id, value, store ? Field.Store.YES : Field.Store.NO, Field.Index.valueOf(index.toUpperCase())));
                if(primaryKey)
                    term = new Term(id, value);
            } else {
                Attribute attr = node.getAttribute("reader");
                if(attr != null) {
                    reader = (Reader) attr.getObject();
                    ldoc.add(new Field(Documents.getId(node), reader));
                }
            }
        }
        if(term == null)
            writer.addDocument(ldoc);
        else
            writer.updateDocument(term, ldoc, analyzer);
        if(reader != null)
            reader.close();
    }

    @Override
    public void aware(Document desc) {
        Dictionary.Mutable<Object> mutable = new BeanPropertySetter(this);
        WikiupNamingDirectory wnd = Wikiup.getModel(WikiupNamingDirectory.class);
        Dictionaries.setProperties(desc, mutable, wnd);
    }
}