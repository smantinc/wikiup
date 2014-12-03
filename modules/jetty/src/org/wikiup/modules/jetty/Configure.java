package org.wikiup.modules.jetty;

import java.util.Properties;

public class Configure {
    private Properties properties;

    public Configure(Properties properties) {
        this.properties = properties;
    }

    public String getWebroot() {
        return properties.getProperty("wikiup.module.jetty.webroot", "webroot");
    }

    public String getStaticStaticResourceSuffixs() {
        return properties.getProperty("wikiup.module.jetty.static-resource-suffix", "js;css");
    }

    public String getWebapps() {
        return properties.getProperty("wikiup.module.jetty.webapps", null);
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("wikiup.module.jetty.port", "8080"));
    }

    public String getContextPath() {
        return properties.getProperty("wikiup.module.jetty.context-path", "/");
    }

    public boolean isJoin() {
        return Boolean.parseBoolean(properties.getProperty("wikiup.module.jetty.join", "false"));
    }

    public boolean isEnableJsp() {
        boolean enabled = Boolean.parseBoolean(properties.getProperty("wikiup.module.jetty.enablejsp", "true"));
        try {
            Class.forName("org.apache.jasper.servlet.JspServlet");
        } catch(ClassNotFoundException e) {
            return false;
        }
        return enabled;
    }
}
