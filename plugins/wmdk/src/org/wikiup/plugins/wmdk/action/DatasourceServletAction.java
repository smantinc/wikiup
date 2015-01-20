package org.wikiup.plugins.wmdk.action;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.beans.DataSourceManager;
import org.wikiup.database.impl.datasource.DriverManagerDataSource;
import org.wikiup.database.inf.DataSource;
import org.wikiup.database.orm.inf.SQLDialectInf;
import org.wikiup.plugins.wmdk.util.WMDKUtil;
import org.wikiup.servlet.ServletProcessorContext;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatasourceServletAction {
    final static private String[] CATALOG_SCHEMA_FIELD_NAMES = {"TABLE_CAT", "TABLE_SCHEM"};

    public void srcs(ServletProcessorContext context, DataSourceManager manager) throws SQLException {
        Document resp = context.getResponseXML();
        DataSource ds = Interfaces.getModel(manager.getDataSource(), DataSource.class);
        boolean active = false;
        for(String key : manager) {
            DataSource keyds = Interfaces.getModel(manager.get(key), DataSource.class);
            Document item = resp.addChild("datasource");
            Documents.setAttributeValue(item, "name", key);
            active = !active && ds == keyds;
            Documents.setAttributeValue(item, "active", active);
        }
    }

    public void list(ServletProcessorContext context) throws SQLException {
        String node = context.getParameter("node", "root");
        String[] fields = WMDKUtil.parseCatalogSchema(node);
        if("root".equals(node))
            schemaList(context);
        else
            table(context, fields[0], fields[1], context.getResponseBuffer().getResponseXML());
    }

    public void tableList(ServletProcessorContext context) throws SQLException {
        String[] locs = WMDKUtil.parseCatalogSchema(context.getParameter("l"));
        table(context, locs[0], locs[1], context.getResponseBuffer().getResponseXML());
    }

    public void drop(ServletProcessorContext context) throws SQLException {
        DataSource ds = WMDKUtil.getDatasource(context);
        Connection conn = ds.getConnection();
        try {
            Statement stmt = conn.createStatement();
            SQLDialectInf dialect = ds.getDatabaseDriver().getDialect();
            String[] l = WMDKUtil.parseCatalogSchema(context.getParameter("l"));
            String table = context.getParameter("table");
            stmt.execute("DROP TABLE " + dialect.getLocation(l[0], l[1], table));
            WMDKUtil.success(context);
        } finally {
            if(conn != null)
                conn.close();
        }
    }

    private void table(ServletProcessorContext context, String catalog, String schema, Document desc) throws SQLException {
        Connection conn = WMDKUtil.getConnection(context);
        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet tables = dmd.getTables(catalog, schema, null, null);
        while(tables.next()) {
            Document item = desc.addChild("table");
            Documents.setAttributeValue(item, "text", tables.getString("TABLE_NAME"));
            Documents.setAttributeValue(item, "leaf", true);
        }
        conn.close();
    }

    public void schemaList(ServletProcessorContext context) throws SQLException {
        Connection conn = WMDKUtil.getConnection(context);
        DatabaseMetaData dmd = conn.getMetaData();
        Document response = context.getResponseBuffer().getResponseXML();
        copySchemaToDocument(dmd.getCatalogs(), response, "catalog", 0);
        copySchemaToDocument(dmd.getSchemas(), response, "schema", 1);
        if(!response.getChildren().iterator().hasNext()) {
            Document item = response.addChild("schema");
            Documents.setAttributeValue(item, "text", "main");
            Documents.setAttributeValue(item, "id", "main");
            Documents.setAttributeValue(item, "expandable", true);
        }
        conn.close();
    }

    public void doDefault(ServletProcessorContext context, DataSourceManager manager) {
        String name = context.getParameter("ds");
        DataSource ds = DataSourceManager.getInstance().get(name);
        if(name != null && ds != null)
            manager.setDataSource(ds);
        WMDKUtil.success(context);
    }

    public void configure(ServletProcessorContext context, Document configure) throws IOException {
        String mgr = context.getParameter("ds");
        DriverManagerDataSource ds = Interfaces.getModel(WMDKUtil.getDatasource(context), DriverManagerDataSource.class);
        if(ds != null) {
            ds.setUser(context.getParameter("user"));
            ds.setPassword(context.getParameter("password"));
            ds.getDatabaseDriver().setConnectionURL(context.getParameter("connection-url"));
        }
        Document node = Documents.findMatchesChild(configure, "driver", mgr);
        if(node != null) {
            String location = Documents.getDocumentValue(configure, "bean-location");
            Document template = node.getChild("template");
            if(template != null)
                WMDKUtil.generateTemplatePage(context, Documents.getDocumentValue(template, "uri"), StringUtil.connect(location, Documents.getDocumentValue(node, "bean-file"), '/'));
            WMDKUtil.reboot();
        }
        WMDKUtil.success(context);
    }

    public void properties(ServletProcessorContext context) {
        DriverManagerDataSource ds = Interfaces.getModel(WMDKUtil.getDatasource(context), DriverManagerDataSource.class);
        Document doc = context.getResponseXML();
        if(ds != null) {
            Documents.setAttributeValue(doc, "user", ds.getUser());
            Documents.setAttributeValue(doc, "password", ds.getPassword());
            Documents.setAttributeValue(doc, "connection-url", ds.getDatabaseDriver().getConnectionURL());
        }
    }

    private void copySchemaToDocument(ResultSet resultSet, Document response, String itemName, int columnId) throws SQLException {
        while(resultSet.next()) {
            Document item = response.addChild(itemName);
            String catalog = getResultSetField(resultSet, CATALOG_SCHEMA_FIELD_NAMES[0]);
            String schema = getResultSetField(resultSet, CATALOG_SCHEMA_FIELD_NAMES[1]);
            Documents.setAttributeValue(item, "text", columnId == 0 ? catalog : schema);
            Documents.setAttributeValue(item, "id", ValueUtil.toString(catalog, "") + '@' + ValueUtil.toString(schema, ""));
            Documents.setAttributeValue(item, "expandable", true);
        }
    }

    private String getResultSetField(ResultSet resultSet, String fieldName) {
        try {
            return resultSet.getString(fieldName);
        } catch(SQLException e) {
            return null;
        }
    }
}