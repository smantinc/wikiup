package org.wikiup.modules.velocity.context;

import org.wikiup.database.orm.inf.EntityModel;


class Util {
    static public Object toVelocityObject(String name, Object obj) {
        Object wikiup = obj;
        if(obj != null) {
            if(obj instanceof EntityModel)
                wikiup = new org.wikiup.modules.velocity.context.WikiupEntityVelocityContext((EntityModel) wikiup);
//      else if(obj instanceof ProcessorContext)
//        wikiup = new WikiupAccessorVelocityContext((ProcessorContext) obj);
//      else if(obj instanceof GetterInf)
//        wikiup = new WikiupGetterVelocityContext(name, (GetterInf) obj);
        }
        return wikiup;
    }
}
