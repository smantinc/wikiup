package org.wikiup.modules.jython;

import org.python.util.PythonInterpreter;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class JythonInterpreterPool extends WikiupDynamicSingleton<JythonInterpreterPool> implements Context<PythonInterpreter, Object> {
    private Map<String, Object> scopes = new HashMap<String, Object>();

    static public JythonInterpreterPool getInstance() {
        return getInstance(JythonInterpreterPool.class);
    }

    public void firstBuilt() {
        scopes = new HashMap<String, Object>();
    }

    public PythonInterpreter get(String name) {
        PythonInterpreter interpreter = null;
        StringUtil.StringPair sp = name != null ? StringUtil.pair(name, ':', 0) : null;
        if(sp != null && sp.first != null) {
            Context<PythonInterpreter, PythonInterpreter> ctx = Interfaces.cast(Context.class, scopes.get(sp.first));
            if(ctx != null) {
                interpreter = ctx.get(sp.second);
                if(interpreter == null)
                    ctx.set(sp.second, interpreter = new PythonInterpreter());
            }
        }
        return interpreter != null ? interpreter : new PythonInterpreter();
    }

    public void release(String name) {
        PythonInterpreter interpreter = null;
        StringUtil.StringPair sp = name != null ? StringUtil.pair(name, ':', 0) : null;
        if(sp != null && sp.first != null) {
            Context<PythonInterpreter, PythonInterpreter> ctx = Interfaces.cast(Context.class, scopes.get(sp.first));
            if(ctx != null) {
                interpreter = ctx.get(sp.second);
                ctx.set(sp.second, null);
            }
        }
        if(interpreter != null)
            interpreter.cleanup();
    }

    public void set(String name, Object obj) {
        scopes.put(name, obj);
    }
}
