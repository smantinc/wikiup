package org.wikiup.core.impl.translator.lf;

import org.wikiup.core.inf.ext.LogicalTranslator;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class AndLogicalTranslator<E> implements LogicalTranslator<E> {
    private List<LogicalTranslator<E>> filters = null;

    public Boolean translate(E context) {
        if(filters != null) {
            Iterator<LogicalTranslator<E>> iterator = filters.iterator();
            while(iterator.hasNext())
                if(!iterator.next().translate(context))
                    return false;
        }
        return true;
    }

    public void addFilter(LogicalTranslator<E> filter) {
        getFilters(true).add(filter);
    }

    public List<LogicalTranslator<E>> getFilters() {
        return getFilters(false);
    }

    private List<LogicalTranslator<E>> getFilters(boolean create) {
        return filters == null && create ? (filters = new Vector<LogicalTranslator<E>>()) : filters;
    }

    public void removeFilter(LogicalTranslator<E> filter) {
        getFilters(true).remove(filter);
    }
}
