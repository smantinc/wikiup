package org.wikiup.core.impl.setter;

import org.wikiup.core.inf.Setter;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.ContextUtil;

import java.lang.reflect.Method;

public class BeanPropertySetter implements Setter<Object> {

    private Object bean;
    private Class<?> beanClass;

    public BeanPropertySetter(Object bean) {
        this(bean, bean.getClass());
    }

    public BeanPropertySetter(Object bean, Class<?> clazz) {
        this.bean = bean;
        beanClass = clazz;
    }

    public void set(String name, Object obj) {
        Method method = ContextUtil.getBeanPropertySetMethod(beanClass, name, obj);
        try {
            if(method != null)
                method.invoke(bean, obj);
        } catch(Exception ex) {
            Assert.fail(ex);
        }
    }
}
