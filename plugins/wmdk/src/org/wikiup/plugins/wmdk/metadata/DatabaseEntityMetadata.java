package org.wikiup.plugins.wmdk.metadata;

import org.wikiup.core.util.StringUtil;
import org.wikiup.database.beans.DataSourceManager;
import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.inf.EntityMetadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseEntityMetadata implements EntityMetadata {
    private List<FieldMetadata> fields = new ArrayList<FieldMetadata>();

    private String table;
    private String catalog;
    private String schema;
    private String name;

    public DatabaseEntityMetadata(Connection conn, String catalog, String schema, String table, String name) throws SQLException {
        DatabaseMetaData dmd = conn.getMetaData();
        ResultSet columns = dmd.getColumns(catalog, schema, table, null);
        ResultSet primary = dmd.getPrimaryKeys(catalog, schema, table);
        Set<String> primarys = new HashSet<String>();
        while(primary.next())
            primarys.add(primary.getString("COLUMN_NAME").toLowerCase());
        this.table = table;
        this.schema = schema;
        this.catalog = catalog;
        this.name = name;
        while(columns.next()) {
            FieldMetadata fm = new FieldMetadata();
            fm.setFieldName(columns.getString("COLUMN_NAME").toLowerCase());
            fm.setFieldType(columns.getInt("DATA_TYPE"));
            fm.setPrimaryKey(primarys.contains(fm.getFieldName()));
            fields.add(fm);
        }
        columns.close();
        primary.close();
    }

    public DatabaseEntityMetadata(Connection conn, String catalog, String schema, String table) throws SQLException {
        this(conn, catalog, schema, table, StringUtil.getCamelName(table, '_'));
    }

    public Iterable<FieldMetadata> getProperties() {
        return fields;
    }

    public String getName() {
        return name;
    }

    public String getTable() {
        return table;
    }

    public String getSchema() {
        return schema;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getLocation() {
        return DataSourceManager.getInstance().getDatabaseDriver().getDialect().getLocation(catalog, schema, table);
    }
}
