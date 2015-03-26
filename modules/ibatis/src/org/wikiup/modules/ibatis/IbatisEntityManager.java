package org.wikiup.modules.ibatis;


import com.ibatis.common.util.PaginatedList;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;
import com.ibatis.sqlmap.client.SqlMapSession;
import com.ibatis.sqlmap.client.event.RowHandler;
import com.ibatis.sqlmap.engine.execution.BatchException;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;
import com.ibatis.sqlmap.engine.mapping.statement.MappedStatement;
import org.wikiup.Wikiup;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.framework.bean.WikiupResourceLoader;
import org.wikiup.framework.bootstrap.Bootstrap;
import org.wikiup.framework.bootstrap.inf.ResourceHandler;
import org.wikiup.core.impl.resource.FileResource;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.exception.InsufficientPrimaryKeys;
import org.wikiup.database.inf.DataSource;
import org.wikiup.database.orm.FieldMetadata;
import org.wikiup.database.orm.inf.EntityManager;
import org.wikiup.database.orm.inf.EntityMetadata;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.database.orm.inf.SQLDialect;
import org.wikiup.database.orm.util.SQLStatement;
import org.wikiup.modules.ibatis.entity.IbatisEntity;
import org.wikiup.modules.ibatis.meta.IbatisEntityMetadata;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class IbatisEntityManager extends WikiupDynamicSingleton<IbatisEntityManager> implements EntityManager, DocumentAware, Releasable, SqlMapClient, ResourceHandler {
    private SqlMapClient sqlMap;
    private ExtendedSqlMapClient extendedSqlMap;

    private Set<Resource> sqlMapResources;
    private String configureLocation;

    public void appMapResource(Resource resource) {
        sqlMapResources.add(resource);
    }

    public EntityModel getEntityModel(String name) {
        return new IbatisEntity(this, name);
    }

    public EntityMetadata getEntityMetadata(String name) {
        return new IbatisEntityMetadata(name);
    }

    @Override
    public void aware(Document desc) {
        configureLocation = Documents.getDocumentValue(desc, "config-location", configureLocation);
    }

    public void initialize() {
        try {
            Reader reader = new StringReader(getSQLMapConfigure(configureLocation));
            if(sqlMapResources.size() > 0)
                Bootstrap.onReady(new SQLMapClientLoader(reader));
        } catch(IOException ex) {
            Assert.fail(ex);
        }
    }

    private String getSQLMapConfigure(String resource) throws IOException {
        WikiupResourceLoader rl = Wikiup.getModel(WikiupResourceLoader.class);
        for(Resource r : rl.get(resource)) {
            String text = StreamUtil.loadText(new InputStreamReader(r.open(), WikiupConfigure.CHAR_SET));
            if(text != null) {
                int pos = text.lastIndexOf("</sqlMapConfig>");
                if(pos != -1) {
                    StringBuilder buf = new StringBuilder(text.substring(0, pos));
                    Iterator<Resource> iterator = sqlMapResources.iterator();
                    while(iterator.hasNext()) {
                        Resource res = iterator.next();
                        if(res.exists()) {
                            FileResource fr = Interfaces.getModel(res, FileResource.class);
                            if(fr == null) {
                                buf.append("\n<sqlMap resource=\"");
                                buf.append(StringUtil.trimLeft(res.getURI(), "/"));
                                buf.append("\"/>");
                            } else {
                                buf.append("\n<sqlMap url=\"");
                                buf.append("file:///" + fr.getFile().getAbsolutePath().replace('\\', '/').replace(':', '|'));
                                buf.append("\"/>");
                            }
                        } else
                            iterator.remove();
                    }
                    buf.append(text.substring(pos));
                    text = buf.toString();
                }
            }
            return text;
        }
        return null;
    }

    public void firstBuilt() {
        sqlMapResources = new HashSet<Resource>();
    }

    public Document describe(EntityMetadata metadata) {
        Document root = Documents.create("sqlMap");
        SQLStatement stmt = new SQLStatement();
        String whereClause = buildWhereClause(metadata);
        SQLDialect dialect = Wikiup.getModel(DataSource.class).getDatabaseDriver().getDialect();
        Document parameters = Documents.create("param");

        Documents.setAttributeValue(parameters, "limit", "$limit$");
        Documents.setAttributeValue(parameters, "offset", "$offset$");

        Document select = root.addChild("select");
        Documents.setAttributeValue(select, "id", metadata.getName());
        Documents.setAttributeValue(select, "parameterClass", "hashmap");
        Documents.setAttributeValue(select, "resultClass", "hashmap");
        stmt.append("\nSELECT");
        for(FieldMetadata fm : metadata.getProperties())
            stmt.append(" ").append(fm.getFieldName()).append(" AS \"").append(getPropertyName(fm)).append("\",");
        stmt.shrink(1);
        stmt.append(" FROM ");
        appendSchemaTable(dialect, metadata, stmt);
        stmt.append(whereClause);
        stmt.append("\n");
        select.setObject(stmt.getEvaluatedSQL());

        Document all = root.addChild("select");
        Documents.setAttributeValue(all, "id", metadata.getName() + ".all");
        Documents.setAttributeValue(all, "parameterClass", "hashmap");
        Documents.setAttributeValue(all, "resultClass", "hashmap");
        stmt = new SQLStatement();
        stmt.append("\nSELECT");
        for(FieldMetadata fm : metadata.getProperties())
            stmt.append(" ").append(fm.getFieldName()).append(" AS \"").append(getPropertyName(fm)).append("\",");
        stmt.shrink(1);
        stmt.append(" FROM ");
        appendSchemaTable(dialect, metadata, stmt);
        stmt.append("\n");
        dialect.getInterpretor("limit").doAction(stmt, parameters);
        dialect.getInterpretor("offset").doAction(stmt, parameters);
        all.setObject(stmt.getPlainSQL());


        Document update = root.addChild("update");
        Documents.setAttributeValue(update, "id", metadata.getName() + ".update");
        Documents.setAttributeValue(update, "parameterClass", "hashmap");
        stmt = new SQLStatement();
        stmt.append("UPDATE ");
        appendSchemaTable(dialect, metadata, stmt);
        stmt.append(" SET ");
        for(FieldMetadata fm : metadata.getProperties())
            if(!fm.isPrimaryKey())
                stmt.append(" ").append(fm.getFieldName()).append(" = #").append(getPropertyName(fm)).append("#,");
        stmt.shrink(1);
        stmt.append(whereClause);
        update.setObject(stmt.getPlainSQL());

        Document insert = root.addChild("insert");
        Documents.setAttributeValue(insert, "id", metadata.getName() + ".insert");
        Documents.setAttributeValue(insert, "parameterClass", "hashmap");
        stmt = new SQLStatement();
        stmt.append("INSERT INTO ");
        appendSchemaTable(dialect, metadata, stmt);
        stmt.append("(");
        for(FieldMetadata fm : metadata.getProperties())
            if(!fm.isPrimaryKey())
                stmt.append(fm.getFieldName()).append(",");
        stmt.shrink(1);
        stmt.append(") VALUES(");
        for(FieldMetadata fm : metadata.getProperties())
            if(!fm.isPrimaryKey())
                stmt.append("#").append(getPropertyName(fm)).append("#,");
        stmt.shrink(1);
        stmt.append(")");
        insert.setObject(stmt.getPlainSQL());

        Document delete = root.addChild("delete");
        Documents.setAttributeValue(delete, "id", metadata.getName() + ".delete");
        Documents.setAttributeValue(delete, "parameterClass", "hashmap");
        stmt = new SQLStatement();
        stmt.append("DELETE FROM ");
        appendSchemaTable(dialect, metadata, stmt);
        stmt.append(whereClause);
        delete.setObject(stmt.getPlainSQL());
        return root;
    }

    public Iterable<EntityMetadata> getEntityMetadatas() {
        List<EntityMetadata> entities = new ArrayList<EntityMetadata>();
        for(Resource res : sqlMapResources) {
            String name = FileUtil.getFileName(res.getURI(), '/');
            entities.add(new IbatisEntityMetadata(name));
        }
        return entities;
    }

    private String getPropertyName(FieldMetadata fm) {
        return StringUtil.getCamelName(fm.getFieldName(), '_');
    }

    private void appendSchemaTable(SQLDialect dialect, EntityMetadata metadata, SQLStatement stmt) {
        stmt.append(dialect.getLocation(metadata.getCatalog(), metadata.getSchema(), metadata.getTable()));
    }

    private String buildWhereClause(EntityMetadata metadata) {
        StringBuilder buf = new StringBuilder();
        buf.append(" WHERE");
        for(FieldMetadata fm : metadata.getProperties())
            if(fm.isPrimaryKey())
                buf.append(' ').append(fm.getFieldName()).append("=#").append(getPropertyName(fm)).append("# AND");
        Assert.isTrue(buf.length() > 6, InsufficientPrimaryKeys.class, metadata.getTable());
        buf.setLength(buf.length() - 4);
        return buf.toString();
    }

    public Class<?> getStatementParameterClass(String id) {
        MappedStatement stmt = extendedSqlMap.getMappedStatement(id);
        return stmt != null ? stmt.getParameterClass() : null;
    }

    public void release() {
        sqlMap = null;
        sqlMapResources.clear();
    }

    public SqlMapSession openSession() {
        return sqlMap.openSession();
    }

    public SqlMapSession openSession(Connection connection) {
        return sqlMap.openSession(connection);
    }

    public SqlMapSession getSession() {
        return sqlMap.getSession();
    }

    public void flushDataCache() {
        sqlMap.flushDataCache();
    }

    public void flushDataCache(String s) {
        sqlMap.flushDataCache(s);
    }

    public void startTransaction() throws SQLException {
        sqlMap.startTransaction();
    }

    public void startTransaction(int i) throws SQLException {
        sqlMap.startTransaction(i);
    }

    public void commitTransaction() throws SQLException {
        sqlMap.commitTransaction();
    }

    public void endTransaction() throws SQLException {
        sqlMap.endTransaction();
    }

    public void setUserConnection(Connection connection) throws SQLException {
        sqlMap.setUserConnection(connection);
    }

    public Connection getUserConnection() throws SQLException {
        return sqlMap.getUserConnection();
    }

    public Connection getCurrentConnection() throws SQLException {
        return sqlMap.getCurrentConnection();
    }

    public javax.sql.DataSource getDataSource() {
        return sqlMap.getDataSource();
    }

    public Object insert(String s, Object o) throws SQLException {
        return sqlMap.insert(s, o);
    }

    public Object insert(String s) throws SQLException {
        return sqlMap.insert(s);
    }

    public int update(String s, Object o) throws SQLException {
        return sqlMap.update(s, o);
    }

    public int update(String s) throws SQLException {
        return sqlMap.update(s);
    }

    public int delete(String s, Object o) throws SQLException {
        return sqlMap.delete(s, o);
    }

    public int delete(String s) throws SQLException {
        return sqlMap.delete(s);
    }

    public Object queryForObject(String s, Object o) throws SQLException {
        return sqlMap.queryForObject(s, o);
    }

    public Object queryForObject(String s) throws SQLException {
        return sqlMap.queryForObject(s);
    }

    public Object queryForObject(String s, Object o, Object o1) throws SQLException {
        return sqlMap.queryForObject(s, o, o1);
    }

    public List queryForList(String s, Object o) throws SQLException {
        return sqlMap.queryForList(s, o);
    }

    public List queryForList(String s) throws SQLException {
        return sqlMap.queryForList(s);
    }

    public List queryForList(String s, Object o, int i, int i1) throws SQLException {
        return sqlMap.queryForList(s, o, i, i1);
    }

    public List queryForList(String s, int i, int i1) throws SQLException {
        return sqlMap.queryForList(s, i, i1);
    }

    public void queryWithRowHandler(String s, Object o, RowHandler rowHandler) throws SQLException {
        sqlMap.queryWithRowHandler(s, o, rowHandler);
    }

    public void queryWithRowHandler(String s, RowHandler rowHandler) throws SQLException {
        sqlMap.queryWithRowHandler(s, rowHandler);
    }

    public PaginatedList queryForPaginatedList(String s, Object o, int i) throws SQLException {
        return sqlMap.queryForPaginatedList(s, o, i);
    }

    public PaginatedList queryForPaginatedList(String s, int i) throws SQLException {
        return sqlMap.queryForPaginatedList(s, i);
    }

    public Map queryForMap(String s, Object o, String s1) throws SQLException {
        return sqlMap.queryForMap(s, o, s1);
    }

    public Map queryForMap(String s, Object o, String s1, String s2) throws SQLException {
        return sqlMap.queryForMap(s, o, s1, s2);
    }

    public void startBatch() throws SQLException {
        sqlMap.startBatch();
    }

    public int executeBatch() throws SQLException {
        return sqlMap.executeBatch();
    }

    public List executeBatchDetailed() throws SQLException, BatchException {
        return sqlMap.executeBatchDetailed();
    }

    public void handle(Resource resource) {
        appMapResource(resource);
    }

    public void finish() {
        initialize();
    }

    private class SQLMapClientLoader implements Runnable {
        private Reader reader;

        public SQLMapClientLoader(Reader reader) {
            this.reader = reader;
        }

        public void run() {
            sqlMap = SqlMapClientBuilder.buildSqlMapClient(reader);
            extendedSqlMap = Interfaces.cast(ExtendedSqlMapClient.class, sqlMap);
        }
    }
}
