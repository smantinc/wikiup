package org.wikiup.modules.ruby.action;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.wikiup.core.impl.getter.ModelContainerGetter;
import org.wikiup.core.impl.getter.StackGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.Setter;
import org.wikiup.core.util.ContextUtil;
import org.wikiup.modules.ruby.util.RubyUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class RubyScriptServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        Getter<Object> getter = new StackGetter<Object>().append(context, new ModelContainerGetter(context.getModelContainer()));
        final ScriptingContainer scriptingContainer = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
        ContextUtil.setProperties(desc, new Setter<Object>() {
            public void set(String name, Object obj) {
                scriptingContainer.put(name, obj);
            }
        }, getter);
        for(Document script : desc.getChildren("script"))
            RubyUtil.runScriptlet(scriptingContainer, context.getContextAttribute(script, "src", null));
    }
}
