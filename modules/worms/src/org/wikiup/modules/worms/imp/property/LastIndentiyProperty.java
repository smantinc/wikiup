package org.wikiup.modules.worms.imp.property;

import org.wikiup.database.inf.DatabaseDriver;
import org.wikiup.database.orm.util.SQLUtil;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.component.Component;
import org.wikiup.modules.worms.imp.component.Property;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LastIndentiyProperty extends Property {
    public LastIndentiyProperty(Component owner) {
        super(owner);
    }

    @Override
    public Object getObject() {
        WormsEntity entity = (WormsEntity) getOwner();
        Connection conn = entity.getConnection();
        DatabaseDriver driver = entity.getDataSource().getDatabaseDriver();
        String sql = driver.getDialect().getLastIdentitySQL();
        ResultSet rs = SQLUtil.sqlQuery(conn, sql);
        try {
            try {
                if(rs.next())
                    return rs.getObject(1);
            } finally {
                if(rs != null)
                    rs.close();
            }
        } catch(SQLException e) {
        }
        return "0";
    }
}
