package org.wikiup.modules.ruby.context;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.wikiup.core.impl.dictionary.ModelContainerDictionary;
import org.wikiup.core.impl.dictionary.StackDictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.modules.ruby.util.RubyUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;

import java.io.File;

public class RubyProcessorContext implements ProcessorContext, DocumentAware, ServletProcessorContextAware {
    private ScriptingContainer scriptingContainer = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
    private ServletProcessorContext context;

    public Object get(String name) {
        return RubyUtil.toJava(scriptingContainer.get(name));
    }

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        return Interfaces.getModelContainer(get(name));
    }

    public void aware(Document desc) {
        File script = context.getAssociatedFile("rb");
        scriptingContainer.clear();
        if(script.exists()) {
            Dictionary<Object> dictionary = new StackDictionary<Object>().append(context, new ModelContainerDictionary(context.getModelContainer()));
            ContextUtil.setProperties(desc, new Mutable<Object>() {
                public void set(String name, Object obj) {
                    scriptingContainer.put(name, obj);
                }
            }, dictionary);
            RubyUtil.runScriptlet(scriptingContainer, script.getAbsolutePath());
        }
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
