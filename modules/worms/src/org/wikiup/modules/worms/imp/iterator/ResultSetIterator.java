package org.wikiup.modules.worms.imp.iterator;

import org.wikiup.core.impl.iterator.BufferedIterator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

public class ResultSetIterator<E> extends BufferedIterator<E> {
    public ResultSetIterator(ResultSet rs, E instance) {
        super(new UnbufferedResultIterator<E>(rs, instance), instance);
    }

    private static class UnbufferedResultIterator<T> implements Iterator<T> {
        private ResultSet rs;
        private T instance;

        private UnbufferedResultIterator(ResultSet rs, T instance) {
            this.instance = instance;
            this.rs = rs;
        }

        public boolean hasNext() {
            return true;
        }

        public T next() {
            try {
                return rs.next() ? instance : null;
            } catch(SQLException e) {
                return null;
            }
        }

        public void remove() {
        }
    }
}
