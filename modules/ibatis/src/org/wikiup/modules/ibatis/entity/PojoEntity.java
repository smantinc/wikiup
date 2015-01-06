package org.wikiup.modules.ibatis.entity;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.attribute.BeanProperty;
import org.wikiup.core.impl.iterable.BeanProperties;
import org.wikiup.core.impl.translator.TypeCastFilter;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.ValueUtil;

import java.util.HashMap;
import java.util.Map;

public class PojoEntity extends AbstractEntity implements BeanContainer {
    private Object object;
    private Map<String, Attribute> attributes = new HashMap<String, Attribute>();

    public PojoEntity(String name, Object object) {
        super(name);
        this.object = object;
        for(BeanProperty property : new BeanProperties(object))
            attributes.put(property.getName(), new PojoEntityAttribute(property));
    }

    public Attribute get(String name) {
        return attributes.get(name);
    }

    public Iterable<Attribute> getAttributes() {
        return attributes.values();
    }

    public <E> E query(Class<E> clazz) {
        return Interfaces.cast(clazz, object);
    }

    private static class PojoEntityAttribute implements Attribute {
        private BeanProperty property;

        public PojoEntityAttribute(BeanProperty property) {
            this.property = property;
        }

        public String getName() {
            return property.getName();
        }

        public void setName(String name) {
            property.setName(name);
        }

        public Object getObject() {
            return property.getObject();
        }

        public void setObject(Object obj) {
            Class<?> propertyClass = property.getPropertyClass();
            if(obj != null && !propertyClass.equals(obj.getClass()))
                obj = Wikiup.getModel(TypeCastFilter.class).cast(propertyClass, obj);
            property.setObject(obj);
        }

        @Override
        public String toString() {
            return ValueUtil.toString(getObject(), "");
        }
    }
}
