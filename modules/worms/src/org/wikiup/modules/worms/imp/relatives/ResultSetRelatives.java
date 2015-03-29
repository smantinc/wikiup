package org.wikiup.modules.worms.imp.relatives;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.inf.Relatives;
import org.wikiup.modules.worms.WormsEntity;

public abstract class ResultSetRelatives extends EntityRelatives implements Relatives.OneToMany {
    private Map<String, Attribute> fields = new HashMap<String, Attribute>();
    private ResultSet resultSet;

    public void init(Document data, WormsEntity entity) {
        setEntity(entity);
    }

    public void setResultSet(ResultSet result) {
        try {
            ResultSetMetaData meta = result.getMetaData();
            int count = meta.getColumnCount();
            resultSet = result.next() ? result : null;
            if(resultSet != null)
                for(int i = 1; i <= count; i++) {
                    String name = meta.getColumnName(i);
                    fields.put(name, new ResultSetFieldValue(name));
                }
        }
        catch(SQLException ex) {
            Assert.fail(ex);
        }
    }

    @Override
    public Iterator<Relatives> iterator() {
        try {
            if(resultSet != null)
                resultSet.beforeFirst();
            else
                return Null.getInstance();
        }
        catch(SQLException e) {
            Assert.fail(e);
        }
        return new IteratorImpl(this);
    }

    private static class IteratorImpl implements Iterator<Relatives> {
        private ResultSetRelatives resultSetRelatives;

        public IteratorImpl(ResultSetRelatives resultSetRelatives) {
            this.resultSetRelatives = resultSetRelatives;
        }

        @Override
        public boolean hasNext() {
            try {
                return resultSetRelatives.resultSet.next();
            }
            catch(SQLException e) {
                Assert.fail(e);
            }
            return false;
        }

        @Override
        public Relatives next() {
            return resultSetRelatives;
        }

        @Override
        public void remove() {
        }
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return fields.values();
    }

    @Override
    public void release() {
        try {
            resultSet.close();
        }
        catch(SQLException ex) {
            Assert.fail(ex);
        }
    }

    private class ResultSetFieldValue implements Attribute {
        private String name;

        public ResultSetFieldValue(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return ValueUtil.toString(getObject());
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
        }

        @Override
        public Object getObject() {
            try {
                return resultSet.getObject(name);
            }
            catch(SQLException ex) {
                Assert.fail(ex);
            }
            return null;
        }

        @Override
        public void setObject(Object obj) {
        }
    }

    @Override
    public Iterable<Attribute> getProperties() {
        return fields.values();
    }

    @Override
    public Attribute get(String name) {
        return fields.get(name);
    }
}
