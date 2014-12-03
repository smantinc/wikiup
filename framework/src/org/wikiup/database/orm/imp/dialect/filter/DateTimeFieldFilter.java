package org.wikiup.database.orm.imp.dialect.filter;

import org.wikiup.core.inf.Filter;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.ValueUtil;

import java.sql.Timestamp;
import java.util.Date;

public class DateTimeFieldFilter implements Filter<Object, Object> {
    public Object filter(Object object) {
        Timestamp ts = null;
        if(object != null) {
            if(ValueUtil.isInteger(object.toString()))
                ts = new Timestamp(ValueUtil.toLong(object.toString(), 0));
            else {
                Date date = Interfaces.cast(Date.class, object);
                if(date == null)
                    ValueUtil.toDate(object.toString());
                ts = new Timestamp(date != null ? date.getTime() : 0);
            }
        }
        return ts == null ? object : ts;
    }
}
