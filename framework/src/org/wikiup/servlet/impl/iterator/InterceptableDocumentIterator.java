package org.wikiup.servlet.impl.iterator;

import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletInterceptor;

import java.util.Iterator;
import java.util.Stack;

public class InterceptableDocumentIterator implements Iterator<Document> {
    private Stack<Iterator<Document>> iteratorStack = new Stack<Iterator<Document>>();
    private ServletProcessorContext context;
    private String nodeName;

    private Document next = null;

    public InterceptableDocumentIterator(Iterator<Document> iterator, ServletProcessorContext context, String nodeName) {
        iteratorStack.push(iterator);
        this.context = context;
        this.nodeName = nodeName;
    }

    public boolean hasNext() {
        return doNext() != null;
    }

    public Document next() {
        Document n = doNext();
        next = null;
        return n;
    }

    private Document doNext() {
        if(next == null)
            while((next = getNextDocument()) != null) {
                if(StringUtil.compare(next.getName(), "interceptor")) {
                    if(doIntercept(next))
                        iteratorStack.push(next.getChildren().iterator());
                } else if(nodeName == null || StringUtil.compare(next.getName(), nodeName))
                    break;
            }
        return next;
    }

    public void remove() {
    }

    private Document getNextDocument() {
        while(!iteratorStack.isEmpty())
            if(iteratorStack.peek().hasNext())
                return iteratorStack.peek().next();
            else
                iteratorStack.pop();
        return null;
    }

    private boolean doIntercept(Document desc) {
        ServletInterceptor interceptor = Wikiup.getInstance().getBean(ServletInterceptor.class, desc);
        Assert.notNull(interceptor);
        Interfaces.initialize(interceptor, desc);
        return interceptor.intercept(context);
    }
}
