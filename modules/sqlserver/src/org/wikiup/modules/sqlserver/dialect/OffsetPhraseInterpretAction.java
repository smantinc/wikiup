package org.wikiup.modules.sqlserver.dialect;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.orm.inf.DialectInterpretAction;
import org.wikiup.database.orm.util.SQLStatement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OffsetPhraseInterpretAction implements DialectInterpretAction {
    public void doAction(SQLStatement stmt, Document param) {
        String offset = Documents.getAttributeValue(param, "offset");
        String sql = stmt.getSQL().toString();
        String orderBy = getPatternMatcher(sql, "(order by\\s+.+)");
        String sel = StringUtil.trim(getPatternMatcher(sql, "select\\s+(?:top\\s+[^\\s]+)(.*)from"));
        String limit = getPatternMatcher(sql, "top ([^\\s]+)");
        String table = getPatternMatcher(sql, "from\\s+([^\\s]+)");
        String s = StringUtil.format("SELECT {5} FROM (SELECT TOP ({1} + {2}) ROW_NUMBER() OVER({3}) AS _rownum, {0} FROM {4}) AS _resultset WHERE _rownum > {2}", sel, limit, offset, orderBy != null ? orderBy : "order by " + getFirstField(sel), table, stripSelectNames(sel));
        stmt.getSQL().setLength(0);
        stmt.getSQL().append(s);
    }

    private String stripSelectNames(String sel) {
        String[] fields = sel.split(",");
        int i;
        for(i = 0; i < fields.length; i++) {
            String field = fields[i];
            String[] names = field.split("\\s");
            fields[i] = names[names.length - 1];
        }
        return StringUtil.join(fields, ", ");
    }

    private String getPatternMatcher(String sql, String regexp) {
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String getFirstField(String sel) {
        String[] fields = sel.split(",");
        return fields[0].split("\\s+")[0];
    }
}
