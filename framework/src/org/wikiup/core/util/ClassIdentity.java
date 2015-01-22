package org.wikiup.core.util;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupBeanFactory;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.ext.ModelFactory;

public class ClassIdentity {
    private String namespace;
    private String name;

    private String id;

    public ClassIdentity(String identity) {
        setId(identity);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        int idx = id != null ? id.lastIndexOf(':') : -1;
        this.id = id;
        namespace = idx != -1 ? StringUtil.trimLeft(id.substring(0, idx), ":") : null;
        namespace = StringUtil.isEmpty(namespace) ? null : namespace;
        name = idx != -1 ? id.substring(idx + 1) : id;
    }

    public static ClassIdentity obtain(String id) {
        return new ClassIdentity(id);
    }

    public static ClassIdentity obtain(Document desc) {
        return obtain(Documents.getAttributeValue(desc, WikiupConfigure.DEFAULT_FACTORY_ATTRIBUTE, null));
    }
}
