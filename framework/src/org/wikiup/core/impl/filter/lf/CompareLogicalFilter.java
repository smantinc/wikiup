package org.wikiup.core.impl.filter.lf;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.LogicalFilter;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;

public class CompareLogicalFilter implements LogicalFilter<String>, DocumentAware {
    private String condition;

    public Boolean translate(String str) {
        return StringUtil.compare(condition, str);
    }

    public void aware(Document desc) {
        condition = Documents.getAttributeValue(desc, "condition", null);
    }
}
