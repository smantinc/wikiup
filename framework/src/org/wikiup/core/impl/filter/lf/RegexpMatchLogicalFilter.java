package org.wikiup.core.impl.filter.lf;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.LogicalFilter;
import org.wikiup.core.util.Documents;

import java.util.regex.Pattern;

public class RegexpMatchLogicalFilter implements LogicalFilter<String>, DocumentAware {
    private Pattern pattern;

    public RegexpMatchLogicalFilter() {
    }

    public RegexpMatchLogicalFilter(String regexp) {
        setRegexp(regexp);
    }

    public Boolean translate(String str) {
        return pattern != null ? pattern.matcher(str).matches() : false;
    }

    public void aware(Document desc) {
        setRegexp(Documents.getDocumentValue(desc, "regexp", null));
    }

    public void setRegexp(String regexp) {
        pattern = regexp != null ? Pattern.compile(regexp) : null;
    }
}
