package org.wikiup.modules.jython.util;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.jython.JythonInterpreterPool;

public class JythonUtil {
    static public Object toJava(Object obj) {
        PyObject pyObject = Interfaces.cast(PyObject.class, obj);
        Object javaObject = pyObject != null ? pyObject.__tojava__(Object.class) : obj;
        return javaObject != null ? javaObject : obj;
    }

    static public void exec(PythonInterpreter interpreter, Dictionary<?> context, Document desc) {
        prepareScriptContext(interpreter, context, desc);
        execScript(interpreter, context, desc);
        for(Document doc : desc.getChildren("script"))
            execScript(interpreter, context, doc);
    }

    static public void prepareScriptContext(final PythonInterpreter interpreter, Dictionary<?> context, Document desc) {
        Dictionaries.setProperties(desc, new Dictionary.Mutable<Object>() {
            public void set(String name, Object object) {
                interpreter.set(name, object);
            }
        }, context);
    }

    static public void execScript(PythonInterpreter interpreter, Dictionary<?> context, Document desc) {
        String src = StringUtil.evaluateEL(Documents.getAttributeValue(desc, "src", null), context);
        if(!StringUtil.isEmpty(src))
            interpreter.execfile(src);
        String cdata = StringUtil.trim(ValueUtil.toString(desc));
        if(!StringUtil.isEmpty(cdata))
            interpreter.exec(cdata);
    }

    static public void releasePythonInterpreter(Document doc) {
        if(Documents.getAttributeBooleanValue(doc, "auto-release", false))
            releasePythonInterpreter(Documents.getAttributeValue(doc, "scope", null));
    }

    static public void releasePythonInterpreter(String id) {
        if(id != null)
            JythonInterpreterPool.getInstance().release(id);
    }

    static public PythonInterpreter getPythonInterpreter(Document doc) {
        return getPythonInterpreter(Documents.getAttributeValue(doc, "scope", null));
    }

    static public PythonInterpreter getPythonInterpreter(String id) {
        return JythonInterpreterPool.getInstance().get(id);
    }
}
