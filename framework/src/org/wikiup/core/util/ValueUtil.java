package org.wikiup.core.util;

import org.wikiup.framework.bean.WikiupConfigure;
import org.wikiup.core.inf.Attribute;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ValueUtil {
    static public int toInteger(Object value, int def) {
        try {
            return value != null ? Integer.parseInt(value.toString()) : def;
        } catch(NumberFormatException ex) {
            return def;
        }
    }

    static public float toFloat(Object value, float def) {
        try {
            return value != null ? Float.parseFloat(value.toString()) : def;
        } catch(NumberFormatException ex) {
            return def;
        }
    }

    static public double toDouble(Object value, double def) {
        try {
            return value != null ? Double.parseDouble(value.toString()) : def;
        } catch(NumberFormatException ex) {
            return def;
        }
    }

    static public long toLong(String value, long def) {
        try {
            return value != null ? Long.parseLong(value) : def;
        } catch(NumberFormatException ex) {
            return def;
        }
    }

    static public boolean toBoolean(Object obj, boolean def) {
        String value = toString(obj, null);
        return value != null ? (def ? !value.equalsIgnoreCase("false") : value.equalsIgnoreCase("true")) : def;
    }

    static public String toString(Attribute attr) {
        return attr != null ? toString(attr.getObject(), null) : null;
    }

    static public String toString(Object obj) {
        return toString(obj, null);
    }

    static public String toString(Object obj, String def) {
        return obj != null ? obj.toString() : def;
    }

    static public String toHexString(byte b[]) {
        StringBuilder buffer = new StringBuilder();
        int i;
        for(i = 0; i < b.length; i++)
            appendHexString(buffer, b[i]);
        return buffer.toString();
    }

    static public void appendHexString(StringBuilder sb, byte b) {
        int value = b < 0 ? b + 0x100 : b;
        sb.append(toHexCharacter(value / 0x10));
        sb.append(toHexCharacter(value % 0x10));
    }

    static public char toHexCharacter(int value) {
        return toHexCharacter(value, false);
    }

    static public char toHexCharacter(int value, boolean capital) {
        return (char) (value > 9 ? (value - 10 + (capital ? 'A' : 'a')) : '0' + value);
    }

    static public int toHex(String value) {
        int ret = 0, i, len = value.length();
        value = value.toLowerCase();
        for(i = 0; i < len; i++)
            ret = ((ret << 4) + toHex(value.charAt(i)));
        return ret;
    }

    static public int toHex(char value) {
        if(value <= '9')
            return value - '0';
        if(value <= 'F')
            return value - 'A' + 10;
        return value - 'a' + 10;
    }

    static public Date toDate(String value, String pattern) {
        try {
            return value != null ? (new SimpleDateFormat(pattern, Locale.US)).parse(value) : null;
        } catch(ParseException ex) {
            return null;
        }
    }

    static public String getFormattedDate(Date date, String pattern, Locale locale) {
        return (new SimpleDateFormat(pattern, locale)).format(date);
    }

    static public String getFormattedDate(Date date, String pattern) {
        return (new SimpleDateFormat(pattern)).format(date);
    }

    static public String getFormattedDate(Date date) {
        return new SimpleDateFormat(WikiupConfigure.DATE_TIME_PATTERN[0]).format(date);
    }

    static public Date toDate(String value) {
        for(String pattern : WikiupConfigure.DATE_TIME_PATTERN) {
            Date date = toDate(value, pattern);
            if(date != null)
                return date;
        }
        return null;
    }

    static public long getTimeMillis(String name) {
        if(isInteger(name))
            return toLong(name, 0);
        long offset = 0;
        boolean minus = name.charAt(0) == '-';
        offset += getTimeMillis(name, 'd', 86400000, minus);
        offset += getTimeMillis(name, 'h', 3600000, minus);
        offset += getTimeMillis(name, 'm', 60000, minus);
        offset += getTimeMillis(name, 's', 1000, minus);
        return offset;
    }

    static private long getTimeMillis(String name, char unit, long times, boolean minus) {
        int pos = name.indexOf(unit);
        long millis = 0;
        if(pos != -1) {
            int s = pos - 1;
            while(s >= 0 && ValueUtil.isNumber(name.charAt(s)))
                s--;
            millis = ValueUtil.toInteger(name.substring(s + 1, pos), 0) * times;
        }
        return minus ? 0 - millis : millis;
    }

    static public boolean isInteger(String number) {
        int i;
        if(StringUtil.isEmpty(number))
            return false;
        for(i = (number.charAt(0) == '-' || number.charAt(0) == '+' ? 1 : 0); i < number.length(); i++)
            if(!isNumber(number.charAt(i)))
                return false;
        return true;
    }

    static public boolean isNumber(char c) {
        return c >= '0' && c <= '9';
    }
}
