package org.wikiup.database.orm.imp.dialect.filter;

import org.wikiup.core.inf.Filter;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.ValueUtil;

import java.sql.Timestamp;

public class TimestampFieldFilter implements Filter<Object, Timestamp> {
    public Timestamp filter(Object object) {
        long time = -1;
        if(ValueUtil.isInteger(object.toString()))
            time = ValueUtil.toLong(object.toString(), 0);
        else if(object instanceof java.util.Date)
            time = ((java.util.Date) object).getTime();
        return time != -1 ? new Timestamp(time) : Interfaces.cast(Timestamp.class, object);
    }
}
