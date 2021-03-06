package org.wikiup.database.orm.imp.dialect.translator;

import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.ValueUtil;

import java.sql.Timestamp;
import java.util.Date;

public class DateTimeFieldFilter implements Translator<Object, Object> {
    public Object translate(Object object) {
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
