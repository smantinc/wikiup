package org.wikiup.core.impl.wndi;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.context.ThreadLocalContext;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.util.Documents;

import java.util.Map;

public class ThreadLocalWikiupNamingDirectory extends ThreadLocalContext implements DocumentAware {
    public void aware(Document desc) {
        Map<String, ThreadLocal<Object>> threadLocals = getThreadLocals();
        for(Document node : desc.getChildren())
            threadLocals.put(Documents.getId(node), new ThreadLocalContainer(node));
    }

    static private class ThreadLocalContainer extends ThreadLocal<Object> {
        private Document desc;

        public ThreadLocalContainer(Document desc) {
            this.desc = desc;
        }

        @Override
        protected Object initialValue() {
            return Wikiup.getInstance().getBean(Object.class, desc);
        }
    }
}
