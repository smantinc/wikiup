package org.wikiup.servlet.impl.context.method;

import org.wikiup.core.inf.Getter;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ext.ContextMethodSupport;

public class Cut extends ContextMethodSupport {
    public Object invoke(ServletProcessorContext context, Getter<?> params) {
        String delimiter = getStringParameter(params, "delimiter", null);
        int maxLen = getIntegerParameter(params, "max-length", 12);
        int offset = getIntegerParameter(params, "from", 0);
        int end = getIntegerParameter(params, "to", Integer.MAX_VALUE);
        String value = getStringParameter(params, "value", "");
        String padding = getStringParameter(params, "padding", "...");
        String cutValue = value.substring(offset, Math.min(value.length(), Math.min(offset + maxLen, end)));
        if(delimiter != null) {
            int index = getIntegerParameter(params, "column", 0);
            String values[] = value.split(delimiter);
            cutValue = values.length > index ? values[index] : values[0];
        }
        if(value.length() > maxLen)
            cutValue += padding;
        return cutValue;
    }
}
