package org.wikiup.core.impl.translator;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Translator;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ValueOfTranslator implements Translator<Object, Object>, DocumentAware {
    private Class toClass;

    public Object translate(Object object) {
        if(object != null) {
            try {
                if(toClass.isAssignableFrom(object.getClass()))
                    return object;
                Method method = toClass.getMethod("valueOf", object.getClass());
                return method.invoke(null, object);
            } catch(IllegalAccessException e) {
            } catch(NoSuchMethodException e) {
                Method method = null;
                try {
                    method = toClass.getMethod("valueOf", String.class);
                    return method.invoke(null, object.toString());
                } catch(NoSuchMethodException e1) {
                    Assert.fail(e1);
                } catch(InvocationTargetException e1) {
                    Assert.fail(e.getCause());
                } catch(IllegalAccessException e1) {
                    Assert.fail(e1);
                }
            } catch(InvocationTargetException e) {
                Assert.fail(e.getCause());
            }
        }
        return null;
    }

    public void aware(Document desc) {
        toClass = Interfaces.getClass(Documents.getDocumentValue(desc, "to-class"));
    }
}
