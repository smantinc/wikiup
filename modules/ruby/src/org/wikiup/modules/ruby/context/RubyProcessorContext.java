package org.wikiup.modules.ruby.context;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.wikiup.core.impl.getter.ModelContainerGetter;
import org.wikiup.core.impl.getter.StackGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.BeanFactory;
import org.wikiup.core.inf.Setter;
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

    public BeanFactory getModelContainer(String name, Getter<?> params) {
        return Interfaces.getModelContainer(get(name));
    }

    public void aware(Document desc) {
        File script = context.getAssociatedFile("rb");
        scriptingContainer.clear();
        if(script.exists()) {
            Getter<Object> getter = new StackGetter<Object>().append(context, new ModelContainerGetter(context.getModelContainer()));
            ContextUtil.setProperties(desc, new Setter<Object>() {
                public void set(String name, Object obj) {
                    scriptingContainer.put(name, obj);
                }
            }, getter);
            RubyUtil.runScriptlet(scriptingContainer, script.getAbsolutePath());
        }
    }

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }
}
