package org.wikiup.modules.authorization.imp.principal;

import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.authorization.inf.Principal;

public class PasswordSecuredPrincipal implements Principal, Setter<Object> {
    private String id;
    private Getter<?> nameGetter;
    private Getter<?> passwordGetter;
    private boolean validated;

    public PasswordSecuredPrincipal() {
    }

    public PasswordSecuredPrincipal(Getter<?> nameGetter, Getter<?> passwordGetter) {
        this.nameGetter = nameGetter;
        this.passwordGetter = passwordGetter;
    }

    public Getter<?> getNameGetter() {
        return nameGetter;
    }

    public void setNameGetter(Getter<?> nameGetter) {
        this.nameGetter = nameGetter;
    }

    public String getPassword() {
        return ValueUtil.toString(passwordGetter.get(id));
    }

    public Getter<?> getPasswordGetter() {
        return passwordGetter;
    }

    public void setPasswordGetter(Getter<?> passwordGetter) {
        this.passwordGetter = passwordGetter;
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
        return ValueUtil.toString(nameGetter.get(id));
    }

    public void setPassword(String password) {
        validated = StringUtil.compare(password, ValueUtil.toString(passwordGetter.get(id)));
    }

    public Object get(String name) {
        return ContextUtil.getBeanProperty(this, name);
    }

    public void set(String name, Object obj) {
        ContextUtil.setBeanProperty(this, name, obj);
    }
}