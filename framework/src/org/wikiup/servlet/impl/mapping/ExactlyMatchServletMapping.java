package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.util.StringUtil;

public class ExactlyMatchServletMapping extends AbstractServletMapping {
    public Boolean filter(String pattern) {
        return pattern.matches("[\\w\\.\\d\\-/]+");
    }

    @Override
    protected String getMappingKey(String uri) {
        return StringUtil.shrinkLeft(uri, "/");
    }
}
