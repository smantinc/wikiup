package org.wikiup.modules.velocity.processor;

import org.apache.velocity.app.VelocityEngine;
import org.wikiup.core.util.Assert;
import org.wikiup.modules.velocity.WikiupVelocityEngine;
import org.wikiup.modules.velocity.context.WikiupVelocityContext;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.impl.rl.ResponseBufferResourceHandler;
import org.wikiup.servlet.inf.ext.ResourceHandler;

public class VelocityProcessor extends ResponseBufferResourceHandler implements ResourceHandler {
    public void process(ServletProcessorContext context) {
        try {
            VelocityEngine ve = WikiupVelocityEngine.getInstance().getEngine();
            WikiupVelocityContext ctx = new WikiupVelocityContext(context);
            ve.evaluate(ctx, context.getResponseWriter(), "wikiup-velocity", context.getResponseText());
        } catch(Exception ex) {
            Assert.fail(ex);
        }
    }
}
