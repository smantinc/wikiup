package org.wikiup.database.orm.util;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLStatement extends SQLPhrase {
    private Map<String, Object> variables = new HashMap<String, Object>();
    static private final Pattern VARIABLE_PATTERN = Pattern.compile("\\?\\((\\w+)\\)");

    private List<SQLPhrase> phrases;
    private List<SQLWrapper> wrappers;

    public SQLStatement() {
    }

    public SQLStatement(String sql) {
        super(sql);
    }

    public void addParameter(String name, Object value) {
        variables.put(name, value);
    }

    public String translate(final Dictionary<?> parameters, String sql) {
        Dictionary<String> dictionary = new Dictionary<String>() {
            public String get(String name) {
                addParameter(name, ValueUtil.toString(parameters.get(name)));
                return "?(" + name + ")";
            }
        };
        return StringUtil.evaluateEL(sql, dictionary);
    }

    public PreparedStatement getPreparedStatement(Connection conn) {
        try {
            List<Object> params = new ArrayList<Object>();
            PreparedStatement stmt = conn.prepareStatement(getPreparedSQL(params), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            fillPreparedStatement(params, stmt);
            return stmt;
        } catch(SQLException ex) {
            Assert.fail(ex);
        }
        return null;
    }

    private void fillPreparedStatement(List<Object> params, PreparedStatement stmt) throws SQLException {
        int i;
        for(i = 0; i < params.size(); i++)
            stmt.setObject(i + 1, params.get(i));
    }

    public PreparedStatement getPreparedStatement(Connection conn, int rType, int cType) throws SQLException {
        List<Object> params = new ArrayList<Object>();
        PreparedStatement stmt = conn.prepareStatement(getPreparedSQL(params), rType, cType);
        fillPreparedStatement(params, stmt);
        return stmt;
    }

    private String getPreparedSQL(List<Object> param) {
        Matcher m = VARIABLE_PATTERN.matcher(getPlainSQL());
        StringBuffer buf = new StringBuffer();
        while(m.find()) {
            param.add(variables.get(m.group(1)));
            m.appendReplacement(buf, "?");
        }
        m.appendTail(buf);
        return buf.toString();
    }

    public String getPlainSQL() {
        StringBuilder buf = new StringBuilder(SQL.toString());
        if(phrases != null)
            for(SQLPhrase phrase : phrases)
                StringUtil.connect(buf, phrase.toString(), ' ');
        if(wrappers != null)
            for(SQLWrapper wrapper : wrappers)
                buf.insert(0, wrapper.getPrefix()).append(wrapper.getSuffix());
        return buf.toString();
    }

    public String getEvaluatedSQL() {
        String plainSQL = getPlainSQL();
        return variables.isEmpty() ? plainSQL : StringUtil.evaluateEL(plainSQL, new SQLParameterDictionary(variables));
    }

    public List<SQLPhrase> getPhrases() {
        return phrases != null ? phrases : (phrases = new Vector<SQLPhrase>());
    }

    public List<SQLWrapper> getWrappers() {
        return wrappers != null ? wrappers : (wrappers = new Vector<SQLWrapper>());
    }

    public void appendPhrase(SQLPhrase phrase) {
        getPhrases().add(phrase);
    }

    public void appendPhrase(String phrase) {
        getPhrases().add(new SQLPhrase(phrase));
    }

    public void appendWrapper(String prefix, String suffix) {
        getWrappers().add(new SQLWrapper(prefix, suffix));
    }

    private static class SQLParameterDictionary implements Dictionary<String> {
        private Map<String, ?> parameters;

        public SQLParameterDictionary(Map<String, ?> parameter) {
            parameters = parameter;
        }

        public String get(String name) {
            return "'" + ValueUtil.toString(parameters.get(name)) + "'";
        }
    }
}
