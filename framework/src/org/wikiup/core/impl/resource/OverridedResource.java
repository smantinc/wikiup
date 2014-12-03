package org.wikiup.core.impl.resource;

import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.inf.Resource;
import org.wikiup.core.util.Interfaces;

import java.io.InputStream;
import java.util.Collection;

public class OverridedResource implements Resource, ModelProvider {
    private Resource resource;
    private Collection<Resource> resources;

    public OverridedResource(Resource resource, Collection<Resource> resources) {
        this.resource = resource;
        this.resources = resources;
    }

    public String getHost() {
        return resource.getHost();
    }

    public String getURI() {
        return resource.getURI();
    }

    public boolean exists() {
        return resource.exists();
    }

    public InputStream open() {
        return resource.open();
    }

    public Collection<Resource> getOverridedResource() {
        return resources;
    }

    public <E> E getModel(Class<E> clazz) {
        return Interfaces.cast(clazz, resource);
    }
}
