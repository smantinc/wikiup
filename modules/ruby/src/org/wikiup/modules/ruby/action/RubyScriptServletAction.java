package org.wikiup.modules.ruby.action;

import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;
import org.wikiup.core.impl.dictionary.ModelContainerDictionary;
import org.wikiup.core.impl.dictionary.StackDictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Dictionaries;
import org.wikiup.modules.ruby.util.RubyUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class RubyScriptServletAction implements ServletAction {
    public void doAction(ServletProcessorContext context, Document desc) {
        Dictionary<Object> dictionary = new StackDictionary<Object>().append(context, new ModelContainerDictionary(context.getModelContainer()));
        final ScriptingContainer scriptingContainer = new ScriptingContainer(LocalVariableBehavior.PERSISTENT);
        Dictionaries.setProperties(desc, new Dictionary.Mutable<Object>() {
            public void set(String name, Object obj) {
                scriptingContainer.put(name, obj);
            }
        }, dictionary);
        for(Document script : desc.getChildren("script"))
            RubyUtil.runScriptlet(scriptingContainer, context.getContextAttribute(script, "src", null));
    }
}
