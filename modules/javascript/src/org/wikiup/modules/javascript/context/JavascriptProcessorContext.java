package org.wikiup.modules.javascript.context;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Decompiler;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.UintMap;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.UniqueTag;
import org.mozilla.javascript.Wrapper;
import org.wikiup.core.impl.dictionary.ModelContainerDictionary;
import org.wikiup.core.impl.dictionary.StackDictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;


public class JavascriptProcessorContext implements ProcessorContext, DocumentAware, ServletProcessorContextAware {
    private ServletProcessorContext context;
    private Context ctx = Context.enter();
    private Scriptable scope = ctx.initStandardObjects();

    public Object get(String name) {
        Object obj = scope.get(name, scope);
        return jsToJava(obj, ctx);
    }

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        return Interfaces.getModelContainer(get(name));
    }

    public void aware(Document desc) {
        File script = context.getAssociatedFile("ssjs");
        if(script.exists()) {
            Reader reader = null;
            try {
                Mutable<Object> mutable = new Mutable<Object>() {
                    public void set(String name, Object obj) {
                        Object jsOut = Context.javaToJS(obj, scope);
                        ScriptableObject.putProperty(scope, name, jsOut);
                    }
                };

                Dictionaries.setProperties(desc, mutable, new StackDictionary<Object>().append(context, new StackDictionary<Object>().append(context, new ModelContainerDictionary(context.getModelContainer()))));

                reader = new FileReader(script);
                ctx.evaluateReader(scope, reader, script.getName(), 1, null);
            } catch(IOException e) {
                Assert.fail(e);
            } finally {
                StreamUtil.close(reader);
            }
        }
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    private Object jsToJava(Object jsObj, Context context) {
        if(jsObj == null || jsObj == Undefined.instance || jsObj == ScriptableObject.NOT_FOUND)
            return null;
        if(jsObj instanceof Wrapper)
            return ((Wrapper) jsObj).unwrap();
        return jsObj instanceof ScriptableObject ? scriptableToJava((ScriptableObject) jsObj, context) : jsObj;
    }

    private Object scriptableToJava(ScriptableObject scriptObj, Context context) {
        if(ScriptRuntime.isArrayObject(scriptObj)) {
            final Object[] array = context.getElements(scriptObj);
            //If scriptObj is a associative arry, array.length will always 0
            //So if scriptObj is empty or index array, return true, else return false
            if(scriptObj.getIds().length == 0 || array.length == scriptObj.getIds().length) {
                for(int i = 0; i < array.length; i++)
                    array[i] = jsToJava(array[i], context);
                return Arrays.asList(array);
            } else
                return jsToJavaMap(scriptObj, context);
        } else {
            final String jsClassName = scriptObj.getClassName();
            if("Object".equals(jsClassName))
                return jsToJavaMap(scriptObj, context);
            else if("Function".equals(jsClassName) && scriptObj instanceof NativeFunction) {
                final NativeFunction func = (NativeFunction) scriptObj;
                return Decompiler.decompile(func.getEncodedSource(), Decompiler.TO_SOURCE_FLAG, new UintMap());
            } else {
                Class clazz = null;
                try {
                    clazz = (Class) ScriptRuntime.class.getDeclaredField(jsClassName + "Class").get(scriptObj);
                } catch(Exception e) {
                    Assert.fail(e);
                }
                return Context.jsToJava(scriptObj, clazz);
            }
        }
    }

    private Object jsToJavaMap(ScriptableObject scriptObj, Context context) {
        final Object[] ids = scriptObj.getIds();
        final HashMap<Object, Object> map = new HashMap<Object, Object>();
        for(Object id : ids) {
            final String key = id.toString();
            Object value = scriptObj.get(key, scriptObj);
            if(value.getClass().equals(UniqueTag.class))
                value = scriptObj.get(Integer.parseInt(key), scriptObj);
            map.put(id, jsToJava(value, context));
        }
        return map;
    }
}
