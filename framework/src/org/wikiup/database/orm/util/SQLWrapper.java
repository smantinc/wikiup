package org.wikiup.database.orm.util;

public class SQLWrapper {
    public SQLWrapper() {
    }

    public SQLWrapper(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    private String prefix;
    private String suffix;
}
