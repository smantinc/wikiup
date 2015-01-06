package org.wikiup.modules.jython.context;

import org.python.util.PythonInterpreter;
import org.wikiup.core.impl.getter.ModelContainerGetter;
import org.wikiup.core.impl.getter.StackGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Interfaces;
import org.wikiup.modules.jython.util.JythonUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.io.File;

public class WikiupPythonProcessorContext implements ProcessorContext, DocumentAware, ServletProcessorContextAware {
    private PythonInterpreter interpreter;
    private ServletProcessorContext context;

    public Object get(String name) {
        return interpreter != null ? JythonUtil.toJava(interpreter.get(name)) : null;
    }

    public BeanContainer getModelContainer(String name, Getter<?> params) {
        return Interfaces.getModelContainer(get(name));
    }

    public void aware(Document desc) {
        File script = context.getAssociatedFile("py");
        if(script.exists()) {
            interpreter = JythonUtil.getPythonInterpreter(desc);
            JythonUtil.prepareScriptContext(interpreter, new StackGetter<Object>().append(context, new ModelContainerGetter(context.getModelContainer())), desc);
            interpreter.execfile(script.getAbsolutePath());
        }
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
