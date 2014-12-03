package org.wikiup.servlet.impl.mapping;

import org.wikiup.core.util.FileUtil;

public class SuffixServletMapping extends AbstractServletMapping {
    public Boolean filter(String pattern) {
        return pattern.matches("\\*\\.[\\w\\d-/]+");
    }

    @Override
    protected String getMappingKey(String uriPattern) {
        return FileUtil.getFileExt(uriPattern);
    }
}
