package org.wikiup.framework.bean;

import org.wikiup.Wikiup;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.bean.WikiupDynamicSingleton;
import org.wikiup.framework.bootstrap.Bootstrap;
import org.wikiup.framework.bootstrap.inf.ResourceHandler;
import org.wikiup.core.impl.Null;
import org.wikiup.core.impl.resource.JarFileResource;
import org.wikiup.core.impl.resource.OverridedResource;
import org.wikiup.core.inf.Attribute;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.inf.ext.Resource;
import org.wikiup.core.inf.ext.Context;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.Interfaces;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;

public class WikiupPluginManager extends WikiupDynamicSingleton<WikiupPluginManager> implements Context<WikiupPluginManager.Plugin, Object>, ResourceHandler, Iterable<String>, Releasable {
    private Map<String, Plugin> pluginByName;
    private Map<String, Plugin> pluginByJar;

    private Set<String> toBeRemoved;

    public void firstBuilt() {
        pluginByName = new HashMap<String, Plugin>();
        pluginByJar = new HashMap<String, Plugin>();
        toBeRemoved = new HashSet<String>();
    }

    public Plugin get(String name) {
        return pluginByName.get(name);
    }

    public void set(String name, Object obj) {
        create(name, null).instance = obj;
    }

    public void handle(Resource resource) {
        Document desc = Documents.loadFromResource(resource);
        if(desc != null) {
            String name = Documents.getId(desc);
            Plugin plugin = create(name, desc);
            plugin.setStatus(Plugin.Status.mounted);
            JarFileResource jarFileResource = Interfaces.cast(JarFileResource.class, resource);
            if(jarFileResource != null) {
                plugin.setJarFile(jarFileResource.getJar());
                pluginByJar.put(jarFileResource.getJar().getName(), plugin);
            }
        }
    }

    public Plugin getPluginByJar(JarFile jar) {
        return pluginByJar.get(jar.getName());
    }

    private Plugin create(String name, Document doc) {
        Plugin plugin = pluginByName.get(name);
        if(plugin == null)
            pluginByName.put(name, (plugin = new Plugin(doc != null ? doc : Null.getInstance())));
        return plugin;
    }

    public void finish() {
        Iterator<Resource> iterator = Bootstrap.getInstance().getBootstrapResource().iterator();
        loadPluginConfigure();
        while(iterator.hasNext()) {
            Resource resource = iterator.next();
            JarFileResource jarFileResource = Interfaces.cast(JarFileResource.class, resource);
            doPluginJarResourceFilter(jarFileResource, iterator);
            OverridedResource overridedResource = Interfaces.cast(OverridedResource.class, resource);
            if(overridedResource != null && overridedResource.getOverridedResource().size() == 1) {
                jarFileResource = Interfaces.cast(JarFileResource.class, overridedResource.getOverridedResource().iterator().next());
                doPluginJarResourceFilter(jarFileResource, iterator);
            }
        }

        for(Plugin plugin : pluginByName.values()) {
            String required = plugin.getRequiredPlugin();
            if(required != null)
                for(String req : required.split(";")) {
                    Plugin reqplg = get(req);
                    if(reqplg == null || reqplg.isDisabled())
                        plugin.setStatus(Plugin.Status.disabled);
                }
        }

        toBeRemoved.clear();
    }

    private void doPluginJarResourceFilter(JarFileResource jarFileResource, Iterator iterator) {
        if(jarFileResource != null) {
            Plugin plugin = getPluginByJar(jarFileResource.getJar());
            if((plugin != null && plugin.isDisabled()) || toBeRemoved.contains(jarFileResource.getJar().getName()))
                iterator.remove();
        }
    }

    private void loadPluginConfigure() {
        Iterable<Resource> iterable = Wikiup.getModel(WikiupResourceLoader.class).get(WikiupConfigure.PLUGIN_CONF);
        if(iterable != null)
            for(Resource res : iterable) {
                Document configure = Documents.loadFromResource(res);
                for(Document node : configure.getChildren())
                    if(Documents.getAttributeBooleanValue(node, "disabled", false)) {
                        Plugin plugin = get(Documents.getId(node));
                        if(plugin != null)
                            plugin.setStatus(Plugin.Status.disabled);
                    } else if(Documents.getAttributeBooleanValue(node, "removed", false)) {
                        Plugin plugin = pluginByName.remove(Documents.getId(node));
                        if(plugin != null) {
                            toBeRemoved.add(plugin.getJarFile().getName());
                            pluginByJar.remove(plugin.getJarFile().getName());
                        }
                    }
            }
    }

    public Iterator<String> iterator() {
        return pluginByName.keySet().iterator();
    }

    public void release() {
        pluginByName.clear();
        pluginByJar.clear();
    }

    public static class Plugin implements Context<Object, Object>, Iterable<String> {
        private Object instance;
        private Document document;

        public enum Status {
            unmounted, mounted, disabled, incompatible
        }

        public enum Type {
            core, module, plugin, extension
        }

        public Plugin(Document document) {
            this.document = document;
            setStatus(Status.unmounted);
        }

        public Object get(String name) {
            Attribute attr = document.getAttribute(name);
            return attr != null ? attr.getObject() : null;
        }

        public void set(String name, Object obj) {
            Documents.setAttributeValue(document, name, obj);
        }

        public Iterator<String> iterator() {
            Set<String> set = new HashSet<String>();
            for(Attribute attr : document.getAttributes())
                set.add(attr.getName());
            for(Document doc : document.getChildren())
                set.add(doc.getName());
            return set.iterator();
        }

        public Document getDocument() {
            return document;
        }

        public Object getInstance() {
            return instance;
        }

        public JarFile getJarFile() {
            return (JarFile) get("jarFile");
        }

        public Status getStatus() {
            return (Status) get("status");
        }

        public Type getType() {
            return (Type) get("type");
        }

        public boolean isDisabled() {
            return Status.disabled == getStatus();
        }

        public String getRequiredPlugin() {
            return Documents.getDocumentValue(document, "required-plugin", null);
        }

        public void setJarFile(JarFile jarFile) {
            try {
                set("jarFile", new ModuleJarFile(jarFile.getName()));
            } catch(IOException e) {
                set("jarFile", jarFile);
            }
        }

        public void setStatus(Status status) {
            set("status", status);
        }

        public void setType(Type type) {
            set("type", type);
        }

        private static class ModuleJarFile extends JarFile {
            public ModuleJarFile(String file) throws IOException {
                super(file);
            }

            @Override
            public String toString() {
                return getName();
            }
        }
    }
}
