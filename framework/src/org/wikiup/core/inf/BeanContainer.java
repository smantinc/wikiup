package org.wikiup.core.inf;

public interface BeanContainer {
    /**
     * BeanFactory was inspired by <a href="http://picocontainer.codehaus.org/">PicoContainer</a>,
     * which is an Inversion of Control (IoC) container for components honor the Dependency Injection pattern.
     * <p/>
     * The basic idea is, BeanFactory is a container from which we can query an instance by its class type,
     * regardless of how this one should be built. Which means, the instance we queried from might
     * be a singleton or being created everytime when we query. We just don't care.
     * All the instance initialization mechanism is up to its container's implementation.
     *
     * @param clazz Given class type used to query an instance from.
     * @return Object which is an instance of type clazz, or null if not one found.
     */
    public <T> T query(Class<T> clazz);
}
