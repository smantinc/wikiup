package org.wikiup.modules.worms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.iterator.BufferedIterator;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.exception.RecordNotFoundException;
import org.wikiup.database.inf.DataSource;
import org.wikiup.database.orm.inf.EntityModel;
import org.wikiup.database.orm.inf.Relatives;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.modules.worms.imp.FieldProperty;
import org.wikiup.modules.worms.imp.builder.EntitySQLBuilder;
import org.wikiup.modules.worms.imp.component.Component;
import org.wikiup.modules.worms.imp.component.Property;
import org.wikiup.modules.worms.imp.relatives.NonePropertyAttributeIterable;
import org.wikiup.modules.worms.inf.SQLBuilderInf;

public class WormsEntity extends Component implements EntityModel, Iterable<WormsEntity> {
    private Connection connection;
    private ResultSet resultSet = null;
    private DataSource dataSource;

    private String tableName;
    private String schemaName;
    private SQLBuilderInf SQLBuilder;
    private boolean dirty;

    private Set<String> keySet = new HashSet<String>();

    public WormsEntity(DataSource dataSource) {
        try {
            connection = dataSource.getConnection();
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
        this.dataSource = dataSource;
    }

    public WormsEntity(DataSource dataSource, Connection conn) {
        connection = conn;
        this.dataSource = dataSource;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getTable() {
        return tableName;
    }

    public Set<String> getKeySet() {
        return keySet;
    }

    public String getSchema() {
        return schemaName;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isEmpty() {
        return resultSet == null;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setResultSet(ResultSet rs, boolean forward, boolean alert) throws RecordNotFoundException {
        closeResultSet();
        try {
            boolean hasRecord = !forward || rs.next();
            Assert.isTrue(hasRecord || !alert, RecordNotFoundException.class, getName());
            resultSet = hasRecord ? rs : null;
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
        setDirty(false);
    }

    public ResultSet getResultSet(boolean refresh) {
        if(refresh)
            refresh(false, true, false);
        return resultSet;
    }

    public void delete() {
        SQLUtil.sqlExecute(getConnection(), SQLBuilder.buildDeleteSQL());
    }

    public void insert() {
        SQLUtil.sqlExecute(getConnection(), SQLBuilder.buildInsertSQL());
    }

    public void initialize(Document desc) {
        Document child = desc.getChild("entity");
        Document data = child != null ? child : desc;
        setDirty(ValueUtil.toBoolean(Documents.getAttributeValue(data, "dirty", null), true));
        setName(Documents.getAttributeValue(desc, "name"));
        tableName = Documents.getAttributeValue(data, "table", null);
        schemaName = Documents.getAttributeValue(data, "schema", Documents.getAttributeValue(data, "catalog", null));
        loadProperties(data);
        SQLBuilder = new EntitySQLBuilder(data, this, this);
    }

    private void loadProperties(Document child) {
        for(Document node : child.getChildren()) {
            String name = Documents.getAttributeValue(node, "name");
            Property item = node.getName().equals("field") ? new FieldProperty(this, node) : addProperty(node);
            addProperty(name, item);
        }
    }

    public void update() {
        SQLUtil.sqlExecute(getConnection(), SQLBuilder.buildUpdateSQL());
    }

    public void release() {
        try {
            closeResultSet();
            if(connection != null)
                connection.close();
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
    }

    public void refresh(boolean force, boolean forward, boolean alert) {
        try {
            if((force || isDirty()) && getTable() != null)
                setResultSet(SQLUtil.sqlQuery(getConnection(), SQLBuilder.buildSelectSQL(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY), forward, alert);
        } catch(SQLException e) {
            Assert.fail(e);
        }
    }

    private void closeResultSet() {
        try {
            if(resultSet != null)
                resultSet.close();
            resultSet = null;
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
    }

    public Relatives getRelatives(String name, Dictionary<?> props) {
        Document node = WormsEntityManager.getInstance().getEntityRelativeDescription(getName(), name);
        return node != null ? WormsEntityRelativesFactory.getInstance().buildEntityRelatives(node, this, props == null ? this : props) : null;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void select() {
        refresh(dirty, true, true);
    }

    public Iterable<Attribute> getAttributes() {
        return new NonePropertyAttributeIterable<Attribute>(getProperties());
    }

    public void bind(Object object) {
    }

    public Iterator<WormsEntity> iterator() {
        if(resultSet != null)
            return new BufferedIterator<WormsEntity>(new UnbufferedWormsEntityIterator(this), this);
        return Null.getInstance();
    }

    private static class UnbufferedWormsEntityIterator implements Iterator<WormsEntity> {
        private WormsEntity entity;

        private UnbufferedWormsEntityIterator(WormsEntity entity) {
            this.entity = entity;
        }

        public boolean hasNext() {
            return true;
        }

        public WormsEntity next() {
            try {
                entity.refresh(false, false, false);
                ResultSet rs = entity.getResultSet(false);
                return rs != null && rs.next() ? entity : null;
            } catch(Exception ex) {
                return null;
            }
        }

        public void remove() {
        }
    }
}
