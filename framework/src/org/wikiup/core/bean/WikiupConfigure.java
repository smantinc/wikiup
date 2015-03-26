package org.wikiup.core.bean;

import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class WikiupConfigure extends WikiupDynamicSingleton<WikiupConfigure> implements Dictionary<String> {
    static public char OPEN_BRACKET;
    static public char CLOSE_BRACKET;

    static public char VARIABLE_TOKEN;
    static public String CHARACTER_PATTERN;

    static public Set<Character> NAMESPACE_SPLITTER = new HashSet<Character>();

    static public String CHAR_SET;

    static public String TRIM_CHAR_SET = " \t\r\n";

    static public String[] DATE_TIME_PATTERN;
    static public String[] DATE_PATTERN;

    static public char[] HANDLER_DELIMETERS = {'!', '$'};

    static public String PLUGIN_CONF;

    private Document document;
    private Properties properties;

    private Properties loadProperties() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("wikiup/wikiup.properties");
        Properties prop = new Properties();
        if(url != null) {
            InputStream is = null;
            try {
                is = url.openStream();
                prop.load(is);
            } catch(IOException ex) {
            } finally {
                StreamUtil.close(is);
            }
        }
        init(prop);
        return prop;
    }

    public String getProperty(String name, String def) {
        return properties.getProperty(name, def);
    }

    private void init(Properties p) {
        VARIABLE_TOKEN = getCharacter(getDefaultProperty(p, "wikiup.variable.head-token", null), '$');
        OPEN_BRACKET = getCharacter(getDefaultProperty(p, "wikiup.variable.open-bracket", null), '{');
        CLOSE_BRACKET = getCharacter(getDefaultProperty(p, "wikiup.variable.close-bracket", null), '}');

        CHARACTER_PATTERN = getDefaultProperty(p, "wikiup.variable.character-pattern", "\\?+!@\\w\\d\\.-:_");

        String nsSplitter = getDefaultProperty(p, "wikiup.variable.namespace-splitter", ":./\\");
        for(char c : nsSplitter.toCharArray())
            NAMESPACE_SPLITTER.add(c);

        CHAR_SET = getDefaultProperty(p, "wikiup.charset", "utf-8");

        DATE_TIME_PATTERN = getDefaultProperty(p, "wikiup.pattern.date-time", "MM/dd/yyyy HH:mm:ss.S;MM/dd/yyyy HH:mm:ss;yyyy-MM-dd HH:mm:ss.S;yyyy-MM-dd HH:mm:ss").split(";");
        DATE_PATTERN = getDefaultProperty(p, "wikiup.pattern.date", "MM/dd/yyyy;yyyy-MM-dd").split(";");

        PLUGIN_CONF = getDefaultProperty(p, "wikiup.plugin.conf", "/wikiup/wmdk/plugins.xml");
    }

    public static WikiupConfigure getInstance() {
        return getInstance(WikiupConfigure.class);
    }

    public void firstBuilt() {
        document = Documents.create("wikiup");
        properties = loadProperties();
    }

    public String get(String name, String def) {
        return Documents.getDocumentValueByXPath(document, name, def);
    }

    public String get(String name) {
        return get(name, null);
    }

    public Document lookup(String path) {
        return Documents.getDocumentByXPath(document, path);
    }

    public void mergeConfigure(String path[], Document doc) {
        Document node = Documents.touchDocument(document, path, path.length);
        Documents.mergeAttribute(node, doc);
        Documents.mergeChildren(node, doc.getChildren(), true);
    }

    private char getCharacter(String value, char def) {
        return StringUtil.isEmpty(value) ? def : value.charAt(0);
    }

    private String getDefaultProperty(Properties p, String key, String defValue) {
        String value = p.getProperty(key, null);
        if(value == null && defValue != null)
            p.setProperty(key, (value = defValue));
        return value;
    }
}
