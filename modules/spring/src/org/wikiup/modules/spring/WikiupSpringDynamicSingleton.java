package org.wikiup.modules.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.wikiup.framework.bean.WikiupDynamicSingleton;
import org.wikiup.core.impl.iterable.IterableCollection;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.core.util.Interfaces;
import org.wikiup.modules.spring.mc.BeanFactoryModelProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WikiupSpringDynamicSingleton extends WikiupDynamicSingleton<WikiupSpringDynamicSingleton> implements Dictionary.Mutable<ApplicationContext>, ApplicationContext, Iterable<String>, ModelFactory, DocumentAware, BeanContainer {
    private Map<String, ApplicationContext> contexts;

    public void firstBuilt() {
        contexts = new HashMap<String, ApplicationContext>();
    }

    public BeanContainer get(String name) {
        org.springframework.beans.factory.BeanFactory beanFactory = getBeanFactory(name, false);
        return beanFactory != null ? new BeanFactoryModelProvider(beanFactory, name) : null;
    }

    public void set(String name, ApplicationContext obj) {
        contexts.put(name, obj);
    }

    public Object getBean(String s) throws BeansException {
        return getBeanFactory(s).getBean(s);
    }

    public Object getBean(String s, Class aClass) throws BeansException {
        return getBeanFactory(s).getBean(s, aClass);
    }

    public Object getBean(String s, Object[] objects) throws BeansException {
        return getBeanFactory(s).getBean(s, objects);
    }

    public boolean containsBean(String s) {
        return getBeanFactory(s, false) != null;
    }

    public boolean isSingleton(String s) throws NoSuchBeanDefinitionException {
        return getBeanFactory(s).isSingleton(s);
    }

    public boolean isPrototype(String s) throws NoSuchBeanDefinitionException {
        return getBeanFactory(s).isPrototype(s);
    }

    public boolean isTypeMatch(String s, Class aClass) throws NoSuchBeanDefinitionException {
        return getBeanFactory(s).isTypeMatch(s, aClass);
    }

    public Class getType(String s) throws NoSuchBeanDefinitionException {
        return getBeanFactory(s).getType(s);
    }

    public String[] getAliases(String s) {
        List<String> aliases = new ArrayList<String>();
        for(ApplicationContext ctx : contexts.values())
            aliases.addAll(Arrays.asList(ctx.getAliases(s)));
        return aliases.toArray(new String[aliases.size()]);
    }

    public String getId() {
        String id;
        for(ApplicationContext ctx : contexts.values())
            if((id = ctx.getId()) != null)
                return id;
        return null;
    }

    public String getDisplayName() {
        String displayName;
        for(ApplicationContext ctx : contexts.values())
            if((displayName = ctx.getDisplayName()) != null)
                return displayName;
        return null;
    }

    public long getStartupDate() {
        long startupDate;
        for(ApplicationContext ctx : contexts.values())
            if((startupDate = ctx.getStartupDate()) > 0)
                return startupDate;
        return 0;
    }

    public ApplicationContext getParent() {
        ApplicationContext parent;
        for(ApplicationContext ctx : contexts.values())
            if((parent = ctx.getParent()) != null)
                return parent;
        return null;
    }

    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        AutowireCapableBeanFactory autowire;
        for(ApplicationContext ctx : contexts.values())
            if((autowire = ctx.getAutowireCapableBeanFactory()) != null)
                return autowire;
        return null;
    }

    public void publishEvent(ApplicationEvent applicationEvent) {
        for(ApplicationContext ctx : contexts.values())
            ctx.publishEvent(applicationEvent);
    }

    public boolean containsBeanDefinition(String s) {
        for(ApplicationContext ctx : contexts.values())
            if(ctx.containsBeanDefinition(s))
                return true;
        return false;
    }

    public int getBeanDefinitionCount() {
        int count = 0;
        for(ApplicationContext ctx : contexts.values())
            count += ctx.getBeanDefinitionCount();
        return count;
    }

    public String[] getBeanDefinitionNames() {
        List<String> names = new ArrayList<String>();
        for(ApplicationContext ctx : contexts.values())
            names.addAll(Arrays.asList(ctx.getBeanDefinitionNames()));
        return names.toArray(new String[names.size()]);
    }

    public String[] getBeanNamesForType(Class aClass) {
        List<String> aliases = new ArrayList<String>();
        for(ApplicationContext ctx : contexts.values())
            aliases.addAll(Arrays.asList(ctx.getBeanNamesForType(aClass)));
        return aliases.toArray(new String[aliases.size()]);
    }

    public String[] getBeanNamesForType(Class aClass, boolean b, boolean b1) {
        List<String> aliases = new ArrayList<String>();
        for(ApplicationContext ctx : contexts.values())
            aliases.addAll(Arrays.asList(ctx.getBeanNamesForType(aClass, b, b1)));
        return aliases.toArray(new String[aliases.size()]);
    }

    public Map getBeansOfType(Class aClass) throws BeansException {
        for(ApplicationContext ctx : contexts.values())
            try {
                return ctx.getBeansOfType(aClass);
            } catch(BeansException e) {
            }
        return null;
    }

    public Map getBeansOfType(Class aClass, boolean b, boolean b1) throws BeansException {
        for(ApplicationContext ctx : contexts.values())
            try {
                return ctx.getBeansOfType(aClass, b, b1);
            } catch(BeansException e) {
            }
        return null;
    }

    public Resource getResource(String s) {
        Resource r;
        for(ApplicationContext ctx : contexts.values())
            if((r = ctx.getResource(s)) != null)
                return r;
        return null;
    }

    public ClassLoader getClassLoader() {
        ClassLoader classLoader;
        for(ApplicationContext ctx : contexts.values())
            if((classLoader = ctx.getClassLoader()) != null)
                return classLoader;
        return null;
    }

    public org.springframework.beans.factory.BeanFactory getParentBeanFactory() {
        org.springframework.beans.factory.BeanFactory beanFactory;
        for(ApplicationContext ctx : contexts.values())
            if((beanFactory = ctx.getParentBeanFactory()) != null)
                return beanFactory;
        return null;
    }

    public boolean containsLocalBean(String s) {
        return false;
    }

    public String getMessage(String s, Object[] objects, String s1, Locale locale) {
        String message;
        for(ApplicationContext ctx : contexts.values())
            if((message = ctx.getMessage(s, objects, s1, locale)) != null)
                return message;
        return null;
    }

    public String getMessage(String s, Object[] objects, Locale locale) throws NoSuchMessageException {
        for(ApplicationContext ctx : contexts.values())
            try {
                return ctx.getMessage(s, objects, locale);
            } catch(NoSuchMessageException e) {
            }
        throw new NoSuchMessageException(s, locale);
    }

    public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale) throws NoSuchMessageException {
        for(ApplicationContext ctx : contexts.values())
            try {
                return ctx.getMessage(messageSourceResolvable, locale);
            } catch(NoSuchMessageException e) {
            }
        throw new NoSuchMessageException(messageSourceResolvable.getDefaultMessage(), locale);
    }

    public Resource[] getResources(String s) throws IOException {
        List<Resource> resources = new ArrayList<Resource>();
        for(ApplicationContext ctx : contexts.values())
            resources.addAll(Arrays.asList(ctx.getResources(s)));
        return resources.toArray(new Resource[resources.size()]);
    }

    private ApplicationContext getBeanFactory(String id) throws NoSuchBeanDefinitionException {
        return getBeanFactory(id, true);
    }

    private ApplicationContext getBeanFactory(String id, boolean alert) throws NoSuchBeanDefinitionException {
        for(ApplicationContext ctx : contexts.values())
            if(ctx.containsBean(id))
                return ctx;
        if(alert)
            throw new NoSuchBeanDefinitionException(id);
        return null;
    }

    public Iterator<String> iterator() {
        IterableCollection<String> iterable = new IterableCollection<String>();
        for(ApplicationContext ctx : contexts.values())
            iterable.append(Interfaces.<String>foreach(ctx));
        return iterable.iterator();
    }

    public <E> E query(Class<E> clazz) {
        for(ApplicationContext ctx : contexts.values()) {
            String[] names = ctx.getBeanNamesForType(clazz);
            if(names.length > 0)
                return Interfaces.cast(clazz, ctx.getBean(names[0], clazz));
        }
        return null;
    }
}
