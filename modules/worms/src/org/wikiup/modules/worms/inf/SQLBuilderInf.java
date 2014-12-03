package org.wikiup.modules.worms.inf;

import org.wikiup.database.orm.util.SQLStatement;


public interface SQLBuilderInf {
    public SQLStatement buildSelectSQL();

    public SQLStatement buildUpdateSQL();

    public SQLStatement buildDeleteSQL();

    public SQLStatement buildInsertSQL();
}
