package org.wikiup.modules.jython.action;

import org.wikiup.core.impl.getter.ModelContainerGetter;
import org.wikiup.core.impl.getter.StackGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.modules.jython.util.JythonUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class PythonServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        Getter<Object> gettable = new StackGetter<Object>().append(context, new ModelContainerGetter(context.getModelContainer()));
        JythonUtil.exec(JythonUtil.getPythonInterpreter(desc), gettable, desc);
        JythonUtil.releasePythonInterpreter(desc);
    }
}
