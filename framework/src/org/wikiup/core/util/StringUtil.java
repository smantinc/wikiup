package org.wikiup.core.util;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.bean.WikiupExpressionLanguage;
import org.wikiup.core.impl.iterable.ArrayIterable;
import org.wikiup.core.inf.Dictionary;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    private final static byte[] ESCAPE_MASK = {
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
            0x3F, 0x3F, 0x3F, 0x3F
    };

    static public boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    static public boolean isNotEmpty(String str) {
        return str != null && str.length() > 0;
    }

    static public String join(Object[] objects, String connector) {
        return join(new ArrayIterable<Object>(objects), connector);
    }

    static public String join(Iterable<?> iterable, String connector) {
        String result = null;
        if(iterable != null) {
            StringBuilder buffer = new StringBuilder();
            for(Object obj : iterable)
                buffer.append(obj).append(connector);

            result = buffer.length() > 0 ? buffer.substring(0, buffer.length() - connector.length()) : "";
        }
        return result;
    }

    static public String connect(String str, String appendix, char connector) {
        return connect(new StringBuilder(str), appendix, connector).toString();
    }

    static public StringBuilder connect(StringBuilder buffer, String appendix, char connector) {
        if(buffer.length() == 0)
            buffer.append(connector);
        if(buffer.charAt(buffer.length() - 1) != connector)
            buffer.append(connector);
        if(appendix.length() > 0 && appendix.charAt(0) == connector)
            appendix = appendix.substring(1);
        return buffer.append(appendix);
    }

    static public StringBuilder connect(StringBuilder buffer, String appendix, String connector) {
        if(!buffer.toString().endsWith(connector))
            buffer.append(connector);
        return buffer.append(appendix.startsWith(connector) ? appendix.substring(connector.length()) : appendix);
    }

    static public boolean compare(String str1, String str2) {
        return str1 != null && str2 != null ? str1.equals(str2) : false;
    }

    static public boolean compareIgnoreCase(String str1, String str2) {
        return str1 != null && str2 != null ? str1.equalsIgnoreCase(str2) : false;
    }

    static public String trim(String str) {
        return trimRight(trimLeft(str, WikiupConfigure.TRIM_CHAR_SET), WikiupConfigure.TRIM_CHAR_SET);
    }

    static public String trim(String str, String charSet) {
        return trimRight(trimLeft(str, charSet), charSet);
    }

    static public String trimLeft(String str, String charset) {
        if(str != null) {
            int i;
            char[] chars = str.toCharArray();
            for(i = 0; i < chars.length; i++)
                if(charset.indexOf(chars[i]) == -1)
                    return str.substring(i);
        }
        return str != null ? "" : null;
    }

    static public String trimRight(String str, String charset) {
        if(str != null) {
            int i;
            char[] chars = str.toCharArray();
            for(i = chars.length - 1; i >= 0; i--)
                if(charset.indexOf(chars[i]) == -1)
                    return str.substring(0, i + 1);
        }
        return str != null ? "" : null;
    }

    static public String evaluateEL(String str, Dictionary<?> context) {
        return str != null ? ValueUtil.toString(WikiupExpressionLanguage.getInstance().evaluate(context, str), "") : null;
    }

    static public String[] separate(String str, String reg) {
        List<String> list = new ArrayList<String>();
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(str);
        while(matcher.find())
            list.add(str.substring(matcher.start(), matcher.end()));
        return list.toArray(new String[list.size()]);
    }

    static public boolean test(Object test) {
        String condition = trim(ValueUtil.toString(test));
        int pos = condition != null ? condition.indexOf('=') : -1;
        if(pos != -1) {
            String lv = condition.substring(0, pos - 1);
            String rv = condition.substring(pos + 2);
            boolean eq = compare(StringUtil.trim(lv), trim(rv));
            return condition.charAt(pos - 1) == '!' ? !eq : eq;
        }
        String cond = trimLeft(condition, "!\t ");
        boolean value = ValueUtil.toBoolean(cond, !StringUtil.isEmpty(cond));
        boolean reversed = condition != null ? condition.startsWith("!") : false;
        return reversed ? !value : value;
    }

    static public String[] split(String s, char splitter) {
        List<String> list = new ArrayList<String>();
        int pos = 0;
        do {
            int next = s.indexOf(splitter, pos);
            String str = next != -1 ? s.substring(pos, next) : s.substring(pos);
            if(pos > 1 && s.charAt(pos - 2) == '\\') {
                String replace = list.get(list.size() - 1);
                replace = replace.length() > 0 ?
                        replace.substring(0, replace.length() - 1) + splitter + str :
                        splitter + str;
                list.set(list.size() - 1, replace);
            } else
                list.add(str);
            pos = next != -1 ? next + 1 : next;
        } while(pos != -1);
        return list.toArray(new String[list.size()]);
    }

    static public String shrinkLeft(String str, String shrink) {
        return shrink != null ? (str.startsWith(shrink) ? str.substring(shrink.length()) : str) : str;
    }

    static public String shrinkRight(String str, String shrink) {
        return shrink != null ? (str.endsWith(shrink) ? str.substring(0, str.length() - shrink.length()) : str) : str;
    }

    static public String unescape(String s) {
        return unescape(s, '%');
    }

    public static String escape(String s) {
        StringBuilder sbuf = new StringBuilder();
        int len = s.length();
        for(int i = 0; i < len; i++) {
            int ch = s.charAt(i);
            if(ch == ' ')
                sbuf.append('+');
            else if('A' <= ch && ch <= 'Z')
                sbuf.append((char) ch);
            else if('a' <= ch && ch <= 'z')
                sbuf.append((char) ch);
            else if('0' <= ch && ch <= '9')
                sbuf.append((char) ch);
            else if(ch == '-' || ch == '_'
                    || ch == '.' || ch == '!'
                    || ch == '~' || ch == '*'
                    || ch == '\\' || ch == '('
                    || ch == ')')
                sbuf.append((char) ch);
            else if(ch <= 0x7f) {
                sbuf.append('%');
                ValueUtil.appendHexString(sbuf, (byte) ch);
            } else {
                sbuf.append('%');
                sbuf.append('u');
                ValueUtil.appendHexString(sbuf, (byte) (ch >>> 8));
                ValueUtil.appendHexString(sbuf, (byte) (0xff & ch));
            }
        }
        return sbuf.toString();
    }

    static public String unescape(String s, char escape) {
        StringBuilder sbuf = new StringBuilder();
        int i = 0;
        int len = s.length();
        while(i < len) {
            int ch = s.charAt(i);
            if(ch == escape) {
                int cint = 0;
                if('u' != s.charAt(i + 1)) { // %XX
                    cint = (cint << 4) | ESCAPE_MASK[s.charAt(i + 1)];
                    cint = (cint << 4) | ESCAPE_MASK[s.charAt(i + 2)];
                    i += 2;
                } else { // %uXXXX
                    cint = (cint << 4) | ESCAPE_MASK[s.charAt(i + 2)];
                    cint = (cint << 4) | ESCAPE_MASK[s.charAt(i + 3)];
                    cint = (cint << 4) | ESCAPE_MASK[s.charAt(i + 4)];
                    cint = (cint << 4) | ESCAPE_MASK[s.charAt(i + 5)];
                    i += 5;
                }
                sbuf.append((char) cint);
            } else
                sbuf.append((char) ch);
            i++;
        }
        return sbuf.toString();
    }

    static public String replaceAll(String s, char f, char t) {
        int i;
        char p[] = new char[s.length()];
        for(i = 0; i < p.length; i++)
            p[i] = s.charAt(i) == f ? t : s.charAt(i);
        return new String(p);
    }

    static public String[] splitNamespaces(String name) {
        char buf[] = name.toCharArray();
        int i, e = 0, len = buf.length;
        char splitter = 0;
        List<String> list = new ArrayList<String>();
        for(i = 0; i < len; i++) {
            char c = buf[i];
            if(splitter == c || (splitter == 0 && WikiupConfigure.NAMESPACE_SPLITTER.contains(c))) {
                if(splitter == 0)
                    splitter = buf[i];
                if(e < i)
                    list.add(name.substring(e, i));
                e = i + 1;
            }
        }
        list.add(name.substring(e));
        return list.toArray(new String[list.size()]);
    }

    static public String getCamelName(String name, char splitter) {
        StringBuilder buf = new StringBuilder();
        int i, len = name.length();
        for(i = 0; i < len; i++)
            buf.append(name.charAt(i) != splitter ? name.charAt(i) : Character.toUpperCase(name.charAt(++i)));
        return buf.toString();
    }

    static public String first(String str, char s, int offset) {
        int idx = str.indexOf(s, offset);
        return idx != -1 ? str.substring(offset, idx) : str;
    }

    static public String second(String str, char s, int offset) {
        int idx = str.indexOf(s, offset);
        return idx != -1 ? str.substring(idx + 1) : null;
    }

    static public StringPair pair(String str, char s, int offset) {
        int idx = str.indexOf(s, offset);
        return idx != -1 ? new StringPair(str.substring(offset), idx) : null;
    }

    static public String format(String str, Object... args) {
        StringBuilder buf = new StringBuilder();
        int i, len = str.length();
        for(i = 0; i < len; i++) {
            if(i > 0 && str.charAt(i - 1) == '\\')
                buf.setLength(buf.length() - 1);
            else if(str.charAt(i) == '{') {
                String idx = first(str, '}', i + 1);
                int o = ValueUtil.toInteger(idx, -1);
                Assert.notNull(idx);
                Assert.isTrue(o != -1);
                buf.append(o < args.length ? args[o] : "{" + o + "}");
                i += (idx.length() + 1);
                continue;
            }
            buf.append(str.charAt(i));
        }
        return buf.toString();
    }

    static public String[] scan(String format, String str) {
        ArrayList<String> vars = new ArrayList<String>();
        int i = 0, ilen = format.length(), j = 0, jlen = str.length();
        while(i < ilen && j < jlen) {
            if(format.charAt(i) == '{') {
                int l = format.indexOf('}', i + 1);
                int idx = ValueUtil.toInteger(l != -1 ? format.substring(i + 1, l) : null, -1);
                if(idx != -1) {
                    int m = l != ilen - 1 ? str.indexOf(format.charAt(l + 1), j) : jlen;
                    while(vars.size() < idx + 1)
                        vars.add("");
                    vars.set(idx, str.substring(j, m));
                    j = m;
                    i = l + 1;
                } else
                    break;
            } else if(format.charAt(i++) != str.charAt(j++))
                break;
        }
        return i == ilen && j == jlen ? vars.toArray(new String[vars.size()]) : null;
    }

    static public String generateRandomString(int length) {
        return generateRandomString(length, -1);
    }

    static public String generateRandomString(int length, long seed) {
        int i;
        final String CHAR_SET = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rnd = seed == -1 ? new Random() : new Random(seed);
        StringBuilder buffer = new StringBuilder();
        for(i = 0; i < length; i++)
            buffer.append(CHAR_SET.charAt(rnd.nextInt(CHAR_SET.length())));
        return buffer.toString();
    }

    static public String md5(String origin) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = ValueUtil.toHexString(md.digest(origin.getBytes()));
        } catch(NoSuchAlgorithmException ex) {
            Assert.fail(ex);
        }
        return resultString;
    }

    static public class StringPair {
        public String first;
        public String second;

        public StringPair(String str, int offset) {
            first = str.substring(0, offset);
            second = str.substring(offset + 1);
        }

        public StringPair(String first, String second) {
            this.first = first;
            this.second = second;
        }
    }
}
