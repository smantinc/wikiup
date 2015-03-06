package org.wikiup.modules.jython.context;

import org.python.util.PythonInterpreter;
import org.wikiup.core.impl.mp.InstanceModelProvider;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.modules.jython.util.JythonUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

public class PythonProcessorContext implements ProcessorContext, ServletProcessorContextAware, DocumentAware {
    private ServletProcessorContext context;
    private PythonInterpreter interpreter;

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        return new InstanceModelProvider(get(name));
    }

    public Object get(String name) {
        return JythonUtil.toJava(name.startsWith("eval:") ? interpreter.eval(name.substring(5)) : interpreter.get(name));
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public void aware(Document desc) {
        interpreter = JythonUtil.getPythonInterpreter(desc);
        JythonUtil.exec(interpreter, context, desc);
    }
}
