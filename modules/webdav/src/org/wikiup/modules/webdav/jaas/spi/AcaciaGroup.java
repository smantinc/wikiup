package org.wikiup.modules.webdav.jaas.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

class MembersEnumeration implements Enumeration {
    private Iterator iterator;

    public MembersEnumeration(Iterator iter) {
        iterator = iter;
    }

    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    public Object nextElement() {
        return iterator.next();
    }
}

public final class AcaciaGroup implements Group {
    private final HashSet Members = new HashSet();

    public boolean addMember(Principal princ) {
        return Members.add(princ);
    }

    public boolean isMember(Principal member) {
        return Members.contains(member);
    }

    public Enumeration members() {
        return new MembersEnumeration(Members.iterator());
    }

    public boolean removeMember(Principal princ) {
        return Members.remove(princ);
    }

    public String getName() {
        return "group";
    }
}
