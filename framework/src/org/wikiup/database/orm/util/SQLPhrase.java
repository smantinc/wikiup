package org.wikiup.database.orm.util;

public class SQLPhrase {
    protected StringBuffer SQL = new StringBuffer();

    public SQLPhrase() {
    }

    public SQLPhrase(String sql) {
        SQL.append(sql);
    }

    public StringBuffer getSQL() {
        return SQL;
    }

    public SQLPhrase append(String sql) {
        SQL.append(sql);
        return this;
    }

    public SQLPhrase shrink(int delta) {
        SQL.setLength(SQL.length() - delta);
        return this;
    }

    @Override
    public String toString() {
        return SQL.toString();
    }
}
