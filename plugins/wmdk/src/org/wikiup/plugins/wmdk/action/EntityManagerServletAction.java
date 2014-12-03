package org.wikiup.plugins.wmdk.action;

import org.wikiup.core.impl.getter.BeanPropertyGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.inf.DataSourceInf;
import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.WikiupEntityManager;
import org.wikiup.database.orm.inf.EntityManager;
import org.wikiup.database.orm.inf.EntityMetadata;
import org.wikiup.database.orm.inf.SQLDialectInf;
import org.wikiup.plugins.wmdk.metadata.DatabaseEntityMetadata;
import org.wikiup.plugins.wmdk.util.WMDKUtil;
import org.wikiup.servlet.ServletProcessorContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class EntityManagerServletAction {
    public void entities(ServletProcessorContext context, WikiupEntityManager domainManager) {
        Document resp = context.getResponseXML();
        for(String managerName : domainManager) {
            EntityManager manager = domainManager.getEntityManager(managerName);
            for(EntityMetadata metadata : manager.getEntityMetadatas()) {
                Document node = resp.addChild("entity");
                Documents.setChildValue(node, "id", managerName + ':' + metadata.getName());
                Documents.setChildValue(node, "name", metadata.getName());
                Documents.setChildValue(node, "manager", managerName);
                Documents.setChildValue(node, "catalog", metadata.getCatalog());
                Documents.setChildValue(node, "schema", metadata.getSchema());
                Documents.setChildValue(node, "table", metadata.getTable());
            }
        }
    }

    public void list(ServletProcessorContext context, WikiupEntityManager domainManager) {
        Document resp = context.getResponseXML();
        for(String key : domainManager) {
            Document node = resp.addChild("metadata-manager");
            EntityManager manager = domainManager.getEntityManager(key);
            Documents.setAttributeValue(node, "name", key);
            Documents.setAttributeValue(node, "class", manager.getClass().getName());
            Documents.setAttributeValue(node, "active", manager == domainManager.getDefaultEntityManager());
        }
    }

    public void doDefault(ServletProcessorContext context, WikiupEntityManager m) {
        String manager = context.getParameter("manager");
        EntityManager mgr = m.get(manager);
        if(mgr != null)
            m.setDefaultEntityManager(mgr);
        WMDKUtil.success(context);
    }

    public void describe(ServletProcessorContext context) throws SQLException {
        String manager = context.getParameter("manager", null);
        Connection conn = WMDKUtil.getConnection(context);
        try {
            DatabaseEntityMetadata metadata = WMDKUtil.getEntityMetadata(context, conn);
            Document desc = WikiupEntityManager.getInstance().getEntityManager(manager).describe(metadata);
            Document resp = Documents.merge(context.getResponseBuffer().getResponseXML(desc.getName(), true), desc);
            WMDKUtil.decorateEntityDescription(resp, manager, new BeanPropertyGetter(metadata));
        } finally {
            if(conn != null)
                conn.close();
        }
    }

    public void fieldset(ServletProcessorContext context, Document desc) {
        Document resp = context.getResponseXML();
        Documents.merge(resp, desc);
    }

    public void define(ServletProcessorContext context, Document desc) throws SQLException {
        DataSourceInf ds = WMDKUtil.getDatasource(context);
        Connection conn = ds.getConnection();
        try {
            Statement stmt = conn.createStatement();
            Document fieldset = desc.getChild("fieldset");
            SQLDialectInf dialect = ds.getDatabaseDriver().getDialect();
            String[] l = WMDKUtil.parseCatalogSchema(context.getParameter("l"));
            String domainName = context.getParameter("domain-name", null);
            String table = context.getParameter("table", null);
            String def = context.getParameter("definition");
            String[] fields = def.split("\\|");
            StringBuffer buf = new StringBuffer();
            int i;
            table = StringUtil.isEmpty(table) ? domainName : table;
            Assert.isTrue(!StringUtil.isEmpty(table));
            buf.append("CREATE TABLE " + dialect.getLocation(l[0], l[1], table));
            buf.append(" (");
            for(i = 0; i < fields.length; i++) {
                String[] f = fields[i].split(",");
                FieldMetadata fm = new FieldMetadata();
                Document node = Documents.findMatchesChild(fieldset, "name", f[1]);
                fm.setFieldName(f[0]);
                fm.setFieldType(node != null ? Documents.getDocumentInteger(node, "sql-type", Types.OTHER) : Types.OTHER);
                fm.setIdentity(i == 0);
                buf.append(dialect.getDefinition(fm));
                buf.append(", ");
            }
            buf.setLength(buf.length() - 2);
            buf.append(")");
            try {
                stmt.execute(buf.toString());
            } catch(SQLException ex) {
            }
            WMDKUtil.success(context);
        } finally {
            if(conn != null)
                conn.close();
        }
    }

    public void meta(ServletProcessorContext context, EntityManager entityManager) {
        String name = context.getParameter("name");
        EntityMetadata meta = entityManager.getEntityMetadata(name);
        Document desc = meta != null ? Interfaces.getModel(meta, Document.class) : null;
        if(desc != null)
            Documents.merge(context.getResponseXML(), desc);
    }

    public void show(ServletProcessorContext context, HttpServletResponse resp, WikiupEntityManager domainManager) throws IOException {
        String name = context.getParameter("name");
        String manager = context.getParameter("manager", null);
        String uri = WMDKUtil.getDomainScaffoldWelcomePage(context, name);
        if(uri != null && FileUtil.isExists(context.getRealPath(uri)))
            resp.sendRedirect(context.getContextURI(uri));
        else
            meta(context, manager != null ? domainManager.getEntityManager(manager) : domainManager);
    }
}
