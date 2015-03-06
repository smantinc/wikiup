package org.wikiup.modules.jython.action;

import org.wikiup.core.impl.getter.ModelContainerDictionary;
import org.wikiup.core.impl.getter.StackDictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.modules.jython.util.JythonUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class PythonServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        Dictionary<Object> gettable = new StackDictionary<Object>().append(context, new ModelContainerDictionary(context.getModelContainer()));
        JythonUtil.exec(JythonUtil.getPythonInterpreter(desc), gettable, desc);
        JythonUtil.releasePythonInterpreter(desc);
    }
}
