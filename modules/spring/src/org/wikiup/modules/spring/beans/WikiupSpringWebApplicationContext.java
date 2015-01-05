package org.wikiup.modules.spring.beans;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.iterable.ArrayIterable;
import org.wikiup.core.impl.mp.InstanceModelProvider;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.ext.ModelFactory;
import org.wikiup.servlet.beans.ServletContextContainer;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class WikiupSpringWebApplicationContext implements ModelFactory, Iterable<String>, ApplicationContext {
    private ApplicationContext applicationContext;

    public WikiupSpringWebApplicationContext() {
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(Wikiup.getModel(ServletContextContainer.class).getServletContext());
    }

    public BeanFactory get(String name) {
        Object bean = applicationContext.getBean(name);
        return bean != null ? new InstanceModelProvider(bean) : null;
    }

    public Iterator<String> iterator() {
        if(applicationContext != null) {
            Iterable<String> iterable = new ArrayIterable<String>(applicationContext.getBeanDefinitionNames());
            return iterable.iterator();
        }
        return Null.getInstance();
    }

    public String getId() {
        return applicationContext.getId();
    }

    public String getDisplayName() {
        return applicationContext.getDisplayName();
    }

    public long getStartupDate() {
        return applicationContext.getStartupDate();
    }

    public ApplicationContext getParent() {
        return applicationContext.getParent();
    }

    public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
        return applicationContext.getAutowireCapableBeanFactory();
    }

    public void publishEvent(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(applicationEvent);
    }

    public boolean containsBeanDefinition(String s) {
        return applicationContext.containsBeanDefinition(s);
    }

    public int getBeanDefinitionCount() {
        return applicationContext.getBeanDefinitionCount();
    }

    public String[] getBeanDefinitionNames() {
        return applicationContext.getBeanDefinitionNames();
    }

    public String[] getBeanNamesForType(Class aClass) {
        return applicationContext.getBeanNamesForType(aClass);
    }

    public String[] getBeanNamesForType(Class aClass, boolean b, boolean b1) {
        return applicationContext.getBeanNamesForType(aClass, b, b1);
    }

    public Map getBeansOfType(Class aClass) throws BeansException {
        return applicationContext.getBeansOfType(aClass);
    }

    public Map getBeansOfType(Class aClass, boolean b, boolean b1) throws BeansException {
        return applicationContext.getBeansOfType(aClass, b, b1);
    }

    public Resource getResource(String s) {
        return applicationContext.getResource(s);
    }

    public ClassLoader getClassLoader() {
        return applicationContext.getClassLoader();
    }

    public org.springframework.beans.factory.BeanFactory getParentBeanFactory() {
        return applicationContext.getParentBeanFactory();
    }

    public boolean containsLocalBean(String s) {
        return applicationContext.containsLocalBean(s);
    }

    public Object getBean(String s) throws BeansException {
        return applicationContext.getBean(s);
    }

    public Object getBean(String s, Class aClass) throws BeansException {
        return applicationContext.getBean(s, aClass);
    }

    public Object getBean(String s, Object[] objects) throws BeansException {
        return applicationContext.getBean(s, objects);
    }

    public boolean containsBean(String s) {
        return applicationContext.containsBean(s);
    }

    public boolean isSingleton(String s) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(s);
    }

    public boolean isPrototype(String s) throws NoSuchBeanDefinitionException {
        return applicationContext.isPrototype(s);
    }

    public boolean isTypeMatch(String s, Class aClass) throws NoSuchBeanDefinitionException {
        return applicationContext.isTypeMatch(s, aClass);
    }

    public Class getType(String s) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(s);
    }

    public String[] getAliases(String s) {
        return applicationContext.getAliases(s);
    }

    public String getMessage(String s, Object[] objects, String s1, Locale locale) {
        return applicationContext.getMessage(s, objects, s1, locale);
    }

    public String getMessage(String s, Object[] objects, Locale locale) throws NoSuchMessageException {
        return applicationContext.getMessage(s, objects, locale);
    }

    public String getMessage(MessageSourceResolvable messageSourceResolvable, Locale locale) throws NoSuchMessageException {
        return applicationContext.getMessage(messageSourceResolvable, locale);
    }

    public Resource[] getResources(String s) throws IOException {
        return applicationContext.getResources(s);
    }
}
