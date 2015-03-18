package org.wikiup.modules.worms.imp.relatives;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.worms.WormsEntity;

public abstract class ResultSetRelatives extends EntityRelatives {
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
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
    }

    @Override
    public Iterable<Attribute> getAttributes() {
        return fields.values();
    }

    public void release() {
        try {
            resultSet.close();
        } catch(SQLException ex) {
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

        public String getName() {
            return name;
        }

        public void setName(String name) {
        }

        public Object getObject() {
            try {
                return resultSet.getObject(name);
            } catch(SQLException ex) {
                Assert.fail(ex);
            }
            return null;
        }

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
