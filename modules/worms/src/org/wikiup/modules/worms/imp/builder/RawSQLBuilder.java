package org.wikiup.modules.worms.imp.builder;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.modules.worms.WormsEntity;
import org.wikiup.modules.worms.imp.FieldProperty;

public class RawSQLBuilder extends RelativeSQLBuilder {
    public RawSQLBuilder(Document data, WormsEntity origin, Dictionary<?> accessor) {
        super(data, origin, accessor);
    }

    @Override
    protected String buildSelectClause() {
        StringBuilder clause = new StringBuilder();
        for(Document node : document.getChildren()) {
            FieldProperty field = new FieldProperty(entity, node);
            if(!field.isCriteria())
                buildSelectPhrase(clause, field);
        }
        return clause.toString();
    }
}
