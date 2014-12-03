package org.wikiup.plugins.wmdk.context;

import org.wikiup.core.impl.getter.BeanPropertyGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.inf.EntityMetadata;
import org.wikiup.plugins.wmdk.util.WMDKUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.impl.context.ProcessorContextSupport;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScaffoldGeneratedEntityProcessorContext extends ProcessorContextSupport implements ServletProcessorContextAware {
    private EntityMetadata entityMetadata;
    private ServletProcessorContext context;

    @Override
    public Object get(String name) {
        return ContextUtil.getBeanProperty(entityMetadata, name);
    }

    public List<Getter<Object>> getFields() {
        List<Getter<Object>> list = new ArrayList<Getter<Object>>();
        for(FieldMetadata fm : entityMetadata.getProperties())
            list.add(new BeanPropertyGetter(fm));
        return list;
    }

    public List<Getter<Object>> getPrimaryKeys() {
        List<Getter<Object>> list = new ArrayList<Getter<Object>>();
        for(FieldMetadata fm : entityMetadata.getProperties())
            if(fm.isPrimaryKey())
                list.add(new BeanPropertyGetter(fm));
        return list;
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
        entityMetadata = (EntityMetadata) context.getAttribute("var:scaffold-entityMetadata");
        if(entityMetadata == null) {
            Connection conn = null;
            try {
                try {
                    conn = WMDKUtil.getConnection(context);
                    entityMetadata = WMDKUtil.getEntityMetadata(context, conn);
                    context.setAttribute("var:scaffold-entityMetadata", entityMetadata);
                } finally {
                    if(conn != null)
                        conn.close();
                }
            } catch(SQLException e) {
                Assert.fail(e);
            }
        }
    }

    public Document getDomainTemplate() {
        Document doc = Documents.create("templates");
        String manager = context.getParameter("manager");
        Document managerConfigure = WMDKUtil.findEntityManagerConfigureNode(manager);
        String domainName = StringUtil.getCamelName(context.getParameter("table"), '_');
        String uri = StringUtil.connect(Documents.getDocumentValue(managerConfigure, "target"), domainName + ".xml", '/');

        Documents.setAttributeValue(doc, "domain-name", domainName);
        Documents.setAttributeValue(doc, "uri", uri);
        if(FileUtil.isExists(context.getRealPath(uri)))
            Documents.setAttributeValue(doc, "exists", true);
        return doc;
    }

    public Document getPreviewTemplates() {
        String table = context.getParameter("table");
        String entityName = StringUtil.getCamelName(table, '_');
        return WMDKUtil.getScaffoldTemplates(context, entityName);
    }

}
