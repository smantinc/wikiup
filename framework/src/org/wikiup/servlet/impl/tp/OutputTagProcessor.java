package org.wikiup.servlet.impl.tp;

import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.TagProcessor;

import java.io.StringWriter;

public class OutputTagProcessor implements TagProcessor {
    public void process(ServletProcessorContext context, TagProcessor parent, String body, Dictionary<?> parameters, StringWriter writer) {
        StringWriter buf = new StringWriter();
        String with = ValueUtil.toString(parameters.get("with"), null);
        String name = ValueUtil.toString(parameters.get("name"), null);
        if(body != null)
            parent.process(context, parent, body, parameters, buf);
        Dictionary<?> ctx = with != null ? Interfaces.cast(Dictionary.class, context.get(with)) : null;
        Assert.notNull(ctx, "Context doesn't exist or not specified by the 'with' attribute");
        if(name != null)
            writer.write(ValueUtil.toString(ctx.get(name)));
        else
            writer.write(StringUtil.evaluateEL(buf.toString(), ctx));
    }
}
