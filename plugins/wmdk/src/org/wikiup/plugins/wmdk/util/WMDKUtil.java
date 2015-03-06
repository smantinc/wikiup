package org.wikiup.plugins.wmdk.util;

import org.wikiup.core.Wikiup;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.bean.WikiupPluginManager;
import org.wikiup.core.bootstrap.Bootstrap;
import org.wikiup.core.impl.document.DocumentWithGetter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.database.beans.DataSourceManager;
import org.wikiup.database.inf.DataSource;
import org.wikiup.database.orm.WikiupEntityManager;
import org.wikiup.database.orm.inf.EntityManager;
import org.wikiup.plugins.wmdk.metadata.DatabaseEntityMetadata;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.beans.ServletContextContainer;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

public class WMDKUtil {
    private static final Document ENTITY_MANAGER_DECORATIONS = WikiupConfigure.getInstance().lookup("wmdk/entity-manager/decorations");
    private static final Document ENTITY_MANAGER_CONFIGURATIONS = WikiupConfigure.getInstance().lookup("wmdk/entity-manager/configurations");
    private static final Document PLUGIN_DEPENDENCIES = loadPluginDependencies();
    private static final Document SCAFFOLD_TEMPLATES = WikiupConfigure.getInstance().lookup("wmdk/templates");

    public static void reboot() {
        DataSource defaultDatasource = DataSourceManager.getInstance().getDataSource();
        EntityManager defaultEntityManager = WikiupEntityManager.getInstance().getDefaultEntityManager();
        String defDSName = null, defManName = null;
        for(String name : DataSourceManager.getInstance().getDataSources().keySet())
            if(DataSourceManager.getInstance().get(name) == defaultDatasource)
                defDSName = name;
        for(String name : WikiupEntityManager.getInstance().getEntityManagers().keySet())
            if(WikiupEntityManager.getInstance().get(name) == defaultEntityManager)
                defManName = name;
        reload();
        if(DataSourceManager.getInstance().getDataSources().containsKey(defDSName))
            DataSourceManager.getInstance().setDataSource(DataSourceManager.getInstance().get(defDSName));
        if(WikiupEntityManager.getInstance().getEntityManagers().containsKey(defManName))
            WikiupEntityManager.getInstance().setDefaultEntityManager(WikiupEntityManager.getInstance().get(defManName));
    }

    static public void reload() {
        ServletContext servletContext = ServletContextContainer.getInstance().getServletContext();
        Wikiup.getInstance().release();
        Bootstrap.release();
        ServletContextContainer.getInstance().setServletContext(servletContext);
        Bootstrap.getInstance().bootstrap(true);
    }

    public static Document copyPluginList(Document doc) {
        return copyPluginList(doc, null);
    }

    public static Document copyPluginList(Document doc, Document plugins) {
        WikiupPluginManager pm = Wikiup.getModel(WikiupPluginManager.class);
        for(String moduleName : pm) {
            Document node = doc.addChild("plugin");
            WikiupPluginManager.Plugin plugin = pm.get(moduleName);
            copyPluginNode(plugins, moduleName, node, true);
            copyPluginNode(PLUGIN_DEPENDENCIES, moduleName, node, false);
            if(plugin != null)
                Documents.mergeAttribute(node, plugin.getDocument());
            if(Documents.getAttributeBooleanValue(node, "hidden", false))
                doc.removeNode(node);
        }
        return doc;
    }

    public static void decorateEntityDescription(Document target, String manager, Dictionary<?> parameters) {
        Document node = Documents.findMatchesChild(ENTITY_MANAGER_DECORATIONS, "manager", manager);
        if(node != null) {
            boolean mergeConfigure = Documents.getAttributeBooleanValue(node, "merge-configure", true);
            for(Document item : node.getChildren()) {
                Document doc = new DocumentWithGetter(item, parameters);
                Documents.merge(mergeConfigure ? Documents.touchChild(target, doc.getName()) : target.addChild(doc.getName()), doc);
            }
        }
    }

    public static String getDomainScaffoldWelcomePage(ServletProcessorContext context, String domainName) {
        Document templates = WMDKUtil.getScaffoldTemplates(context, domainName);
        for(Document node : templates.getChildren()) {
            String uri = Documents.getDocumentValue(node, "uri");
            if(uri.contains("index.") && FileUtil.isExists(context.getRealPathByURI(uri)))
                return uri;
        }
        return null;
    }

    private static void copyPluginNode(Document configure, String moduleName, Document node, boolean activeInfo) {
        Document plugin = configure != null ? findPluginNode(configure, moduleName) : null;
        if(plugin != null)
            Documents.mergeAttribute(node, plugin);
        if(activeInfo) {
            Documents.setAttributeValue(node, "disabled", plugin != null ? Documents.getAttributeBooleanValue(plugin, "disabled", false) : false);
            Documents.setAttributeValue(node, "removed", plugin != null ? Documents.getAttributeBooleanValue(plugin, "removed", false) : false);
        }
    }

    public static Document findEntityManagerConfigureNode(String managerName) {
        return Documents.findMatchesChild(ENTITY_MANAGER_CONFIGURATIONS, "configuration", "name", managerName);
    }

    private static Document findPluginNode(Document configure, String moduleName) {
        for(Document node : configure.getChildren()) {
            String name = Documents.getId(node);
            if(name != null && moduleName.startsWith(name))
                return node;
        }
        return null;
    }

    public static void generateTemplatePage(ServletProcessorContext context, String source, String target) throws IOException {
        Writer writer = null;
        try {
            File file = new File(context.getRealPathByURI(target));
            file.getParentFile().mkdirs();
            writer = new OutputStreamWriter(new FileOutputStream(file), WikiupConfigure.CHAR_SET);
            writer.write(generateTemplatePage(context, source));
        } finally {
            if(writer != null)
                writer.close();
        }
    }

    public static String generateTemplatePage(ServletProcessorContext context, String uri) {
        ServletProcessorContext c = null;
        try {
            c = new ServletProcessorContext(context, getURI(context, uri));
            c.doInit();
            c.doProcess();
            c.release();
            return c.getResponseBuffer().getResponseText(false);
        } finally {
            if(c != null)
                c.release();
        }
    }

    public static DatabaseEntityMetadata getEntityMetadata(ServletProcessorContext context, Connection conn) throws SQLException {
        String[] locs = parseCatalogSchema(context.getParameter("l"));
        String table = context.getParameter("table", null);
        String domainName = context.getParameter("domain-name", null);
        if(StringUtil.isEmpty(table))
            table = domainName;
        if(StringUtil.isEmpty(domainName))
            domainName = StringUtil.getCamelName(table, '_');
        return new DatabaseEntityMetadata(conn, locs[0], locs[1], table, domainName);
    }

    public static String[] parseCatalogSchema(String location) {
        String[] locs = location.split("@");
        String[] ret = new String[2];
        int i;
        for(i = 0; i < locs.length; i++)
            ret[i] = StringUtil.isEmpty(locs[i]) ? null : locs[i];
        return ret;
    }

    public static Connection getConnection(ServletProcessorContext context) throws SQLException {
        return getDatasource(context).getConnection();
    }

    public static DataSource getDatasource(ServletProcessorContext context) {
        String mgr = context.getParameter("ds", null);
        return (mgr != null ? DataSourceManager.getInstance().get(mgr) : DataSourceManager.getInstance());
    }

    public static void success(ServletProcessorContext context, String... args) {
        success(context, true, args);
    }

    public static void success(ServletProcessorContext context, boolean success, String... args) {
        int i;
        Document resp = context.getResponseXML();
        Documents.setAttributeValue(resp, "success", success);
        for(i = 0; i < args.length; i += 2)
            Documents.setAttributeValue(resp, args[i], args[i + 1]);
    }

    public static File getPluginsConfigureFile(ServletProcessorContext context) {
        return new File(context.getRealPath(StringUtil.connect("WEB-INF", "/wikiup/wmdk/plugins.xml", '/')));
    }

    public static Document getPluginConfigure(ServletProcessorContext context) {
        File configure = getPluginsConfigureFile(context);
        Document plugins = null;
        if(configure.exists())
            plugins = Documents.loadFromFile(configure);
        return plugins;
    }

    public static Document getTemplateConfigure() {
        return SCAFFOLD_TEMPLATES;
    }

    public static String getURI(ServletProcessorContext context, String path) {
        String uri = path;
        if(!uri.startsWith("/")) {
            String fp = FileUtil.getFilePath(context.getRequestURI(), '/');
            uri = StringUtil.connect(fp, path, '/');
        }
        return uri;
    }

    public static Document getScaffoldTemplates(ServletProcessorContext context, String entityName) {
        Document doc = Documents.create("templates");
        String source = Documents.getDocumentValue(WMDKUtil.getTemplateConfigure(), "source");
        String target = Documents.getDocumentValue(WMDKUtil.getTemplateConfigure(), "target");
        File dir = new File(context.getRealPathByURI(source));
        for(File file : dir.listFiles())
            if(file.isDirectory() && !file.getName().startsWith(".") && !file.getName().startsWith("_"))
                WMDKUtil.getScaffoldTemplates(context, file, entityName, source, target, doc);
        return doc;
    }

    private static void getScaffoldTemplates(ServletProcessorContext context, File dir, String entityName, String sourceBase, String targetBase, Document doc) {
        String target = StringUtil.connect(targetBase, StringUtil.connect(entityName, dir.getName(), '/'), '/');
        String source = StringUtil.connect(sourceBase, dir.getName(), '/');
        for(File file : dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".html");
            }
        }))
            if(file.isFile() && !file.getName().startsWith(".") && !file.getName().startsWith("_")) {
                Document item = doc.addChild("item");
                String uri = StringUtil.shrinkRight(StringUtil.connect(target, file.getName(), '/'), ".html");
                if(FileUtil.isExists(context.getRealPath(uri)))
                    Documents.setAttributeValue(item, "exists", true);
                Documents.setAttributeValue(item, "uri", uri);
                Documents.setAttributeValue(item, "href", StringUtil.connect(source, file.getName(), '/'));
                Documents.setAttributeValue(item, "name", dir.getName());
            }
    }

    private static Document loadPluginDependencies() {
        Document dep = WikiupConfigure.getInstance().lookup("wmdk/dependencies");
        Document repo = dep.getChild("repository");
        Document liveRepo = null;
        try {
            liveRepo = Documents.loadFromURL(Documents.getAttributeValue(repo, "url", null));
        } catch(Exception ex) {
            liveRepo = liveRepo != null ? liveRepo : dep.getChild("repository-snapshot");
        }
        for(Document node : liveRepo.getChildren()) {
            String name = Documents.getAttributeValue(node, "name");
            Document merge = findPluginNode(repo, name);
            if(merge != null)
                Documents.mergeAttribute(node, merge);
        }
        return liveRepo;
    }
}
