package org.wikiup.servlet.impl.context.util;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormat implements Getter<Object> {
    private Locale locale;

    public DateTimeFormat() {
        locale = Locale.getDefault();
    }

    public DateTimeFormat(Locale locale) {
        this.locale = locale;
    }

    public Object get(String name) {
        if(name.length() == 5) {
            String locals[] = name.split("\\-");
            if(locals.length == 2)
                return new DateTimeFormat(new Locale(locals[0], locals[1]));
        }
        String params[] = StringUtil.split(name, ',');
        return format(params[0], params);
    }

    private String format(String pattern, String[] params) {
        Date date = null;
        switch(params.length) {
            case 4:
                date = ValueUtil.toDate(params[2], params[3]);
                date = date != null ? new Date(date.getTime() + ValueUtil.getTimeMillis(params[1])) : null;
                break;
            case 3:
                date = ValueUtil.toDate(params[2]);
                date = date != null ? new Date(date.getTime() + ValueUtil.getTimeMillis(params[1])) : null;
                break;
            case 2:
                date = new Date(System.currentTimeMillis() + ValueUtil.getTimeMillis(params[1]));
                break;
            case 1:
                date = new Date();
                break;
        }
        return date != null ? (new SimpleDateFormat(pattern, locale)).format(date) : null;
    }

    @Override
    public String toString() {
        return ValueUtil.toString(get(WikiupConfigure.DATE_TIME_PATTERN[0]));
    }
}
