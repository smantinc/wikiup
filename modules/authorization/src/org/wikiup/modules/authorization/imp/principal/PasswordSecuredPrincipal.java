package org.wikiup.modules.authorization.imp.principal;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.authorization.inf.Principal;

public class PasswordSecuredPrincipal implements Principal, Dictionary.Mutable<Object> {
    private String id;
    private Dictionary<?> nameDictionary;
    private Dictionary<?> passwordDictionary;
    private boolean validated;

    public PasswordSecuredPrincipal() {
    }

    public PasswordSecuredPrincipal(Dictionary<?> nameDictionary, Dictionary<?> passwordDictionary) {
        this.nameDictionary = nameDictionary;
        this.passwordDictionary = passwordDictionary;
    }

    public Dictionary<?> getNameGetter() {
        return nameDictionary;
    }

    public void setNameGetter(Dictionary<?> nameDictionary) {
        this.nameDictionary = nameDictionary;
    }

    public String getPassword() {
        return ValueUtil.toString(passwordDictionary.get(id));
    }

    public Dictionary<?> getPasswordGetter() {
        return passwordDictionary;
    }

    public void setPasswordGetter(Dictionary<?> passwordDictionary) {
        this.passwordDictionary = passwordDictionary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean validate() {
        return validated;
    }

    public boolean isSupervisor() {
        return false;
    }

    public boolean isAnonymous() {
        return false;
    }

    public String getName() {
        return ValueUtil.toString(nameDictionary.get(id));
    }

    public void setPassword(String password) {
        validated = StringUtil.compare(password, ValueUtil.toString(passwordDictionary.get(id)));
    }

    public Object get(String name) {
        return ContextUtil.getBeanProperty(this, name);
    }

    public void set(String name, Object obj) {
        ContextUtil.setBeanProperty(this, name, obj);
    }
}