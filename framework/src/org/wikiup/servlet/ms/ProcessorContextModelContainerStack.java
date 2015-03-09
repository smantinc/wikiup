package org.wikiup.servlet.ms;

import java.util.Iterator;
import java.util.Stack;

import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.XPath;
import org.wikiup.servlet.inf.ProcessorContext;

@Deprecated
public class ProcessorContextModelContainerStack implements ProcessorContext {
    private Stack<ProcessorContextModelContainer> contextStack = new Stack<ProcessorContextModelContainer>();

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        return getModelContainerFromStack(name, params);
    }

    public BeanContainer getModelContainerFromStack(String name, Dictionary<?> params) {
        int i;
        for(i = contextStack.size() - 1; i >= 0; i--) {
            ProcessorContextModelContainer container = contextStack.get(i);
            BeanContainer mc = container.getModelContainer(name, params);
            if(mc != null)
                return mc;
        }
        return null;
    }

    public Object getValueFromContextStack(String name, Dictionary<?> params) {
        int i = contextStack.size();
        Object value = getValueByXPath(name, params);
        while(value == null && i > 0)
            value = contextStack.get(--i).get(name);
        return value;
    }

    private Object getValueByXPath(String xpath, Dictionary<?> params) {
        XPath xp = new XPath(xpath);
        String cond = xp.getCondition();
        if(xp.isXPath() && cond != null) {
            BeanContainer modelProvider = getModelContainer(xp.getPath(), params);
            Document doc = modelProvider != null ? modelProvider.query(Document.class) : null;
            return doc != null ? Documents.getAttributeValue(doc, cond, null) : null;
        }
        return null;
    }

    public Iterator<BeanContainer> getIteratorFromContextStack(String name) {
        ProcessorContextModelContainer container = peek();
        return container != null ? container.getIterator(name) : Null.getInstance();
    }

    public Object get(String name) {
        return getValueFromContextStack(name, null);
    }

    public Iterator<BeanContainer> getIterator(String name) {
        return contextStack.empty() ? Null.getInstance() : contextStack.peek().getIterator(name);
    }

    public ProcessorContextModelContainer push(ProcessorContextModelContainer token) {
        return contextStack.push(token);
    }

    public ProcessorContextModelContainer pop() {
        return contextStack.pop();
    }

    public ProcessorContextModelContainer peek() {
        return contextStack.empty() ? null : contextStack.peek();
    }
}
