package org.wikiup.core.impl.translator.lf;

import org.wikiup.core.inf.ext.LogicalFilter;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class AndLogicalFilter<E> implements LogicalFilter<E> {
    private List<LogicalFilter<E>> filters = null;

    public Boolean translate(E context) {
        if(filters != null) {
            Iterator<LogicalFilter<E>> iterator = filters.iterator();
            while(iterator.hasNext())
                if(!iterator.next().translate(context))
                    return false;
        }
        return true;
    }

    public void addFilter(LogicalFilter<E> filter) {
        getFilters(true).add(filter);
    }

    public List<LogicalFilter<E>> getFilters() {
        return getFilters(false);
    }

    private List<LogicalFilter<E>> getFilters(boolean create) {
        return filters == null && create ? (filters = new Vector<LogicalFilter<E>>()) : filters;
    }

    public void removeFilter(LogicalFilter<E> filter) {
        getFilters(true).remove(filter);
    }
}
