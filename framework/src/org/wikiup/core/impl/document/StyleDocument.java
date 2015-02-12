package org.wikiup.core.impl.document;

import java.util.LinkedHashMap;
import java.util.Map;

import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.inf.ext.Context;

public class StyleDocument extends Context2Document {

	public StyleDocument(Context<?, ?> context, Iterable<String> iterable) {
		super(context, iterable);
	}

	public static StyleDocument parse(String style) {
		String[] styles = style.split(";");
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		if(styles.length > 0)
			for(String s : styles) {
				String[] keyValues = s.split(":");
                map.put(keyValues[0].trim(), keyValues.length == 2 ? keyValues[1].trim() : null);
			}
		return new StyleDocument(new MapContext<Object>(map), map.keySet());
	}
}
