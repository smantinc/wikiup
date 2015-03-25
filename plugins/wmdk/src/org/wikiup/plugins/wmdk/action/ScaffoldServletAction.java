package org.wikiup.plugins.wmdk.action;

import org.wikiup.core.Wikiup;
import org.wikiup.framework.bean.WikiupConfigure;
import org.wikiup.framework.bean.WikiupPluginManager;
import org.wikiup.core.impl.dictionary.BeanPropertyDictionary;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.database.orm.WikiupEntityManager;
import org.wikiup.database.orm.inf.EntityMetadata;
import org.wikiup.plugins.wmdk.metadata.DatabaseEntityMetadata;
import org.wikiup.plugins.wmdk.util.WMDKUtil;
import org.wikiup.servlet.ServletProcessorContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ScaffoldServletAction {
    public Document templates(ServletProcessorContext context) {
        Document doc = Documents.create("root");
        Document templates = WMDKUtil.getTemplateConfigure();
        String target = Documents.getDocumentValue(templates, "target");
        File base = new File(context.getRealPathByURI(Documents.getDocumentValue(templates, "source")));
        String entityName = context.getParameter("domain", "");
        boolean all = ValueUtil.toBoolean(context.getParameter("all", null), true);
        if(base.isDirectory()) {
            for(File file : base.listFiles())
                if(file.isDirectory() && !file.getName().startsWith("_") && !file.getName().startsWith(".")) {
                    String name = file.getName();
                    boolean disabled = isTemplateDisabled(templates, name);
                    String index = getIndexFileUri(context, StringUtil.connect(StringUtil.connect(target, entityName, '/'), name, '/'));
                    if(all || (!disabled && index != null)) {
                        Document node = doc.addChild("item");
                        Documents.setAttributeValue(node, "name", name);
                        Documents.setAttributeValue(node, "index", index);
                        Documents.setAttributeValue(node, "disabled", disabled);
                    }
                }
        }
        return doc;
    }

    private String getIndexFileUri(ServletProcessorContext context, String uri) {
        File dir = new File(context.getRealPathByURI(uri));
        if(dir.isDirectory()) {
            for(File file : dir.listFiles())
                if(file.getName().startsWith("index."))
                    return context.getContextURI(StringUtil.connect(uri, file.getName(), '/'));
        }
        return null;
    }

    public void gen(ServletProcessorContext context, WikiupEntityManager entityManager) throws SQLException, IOException {
        Connection conn = null;
        Writer writer = null;
        try {
            Document resp = context.getResponseXML();
            conn = WMDKUtil.getConnection(context);
            DatabaseEntityMetadata metadata = WMDKUtil.getEntityMetadata(context, conn);
            Documents.setAttributeValue(resp, "domain-name", metadata.getName());
            String manager = context.getParameter("manager");
            Document managerConfigure = WMDKUtil.findEntityManagerConfigureNode(manager);

            File file = new File(context.getRealPath(Documents.getDocumentValue(managerConfigure, "target")), metadata.getName() + ".xml");
            file.getParentFile().mkdirs();
            writer = new OutputStreamWriter(new FileOutputStream(file), WikiupConfigure.CHAR_SET);

            generateEntityConfigure(entityManager, metadata, manager, managerConfigure, writer);
            doScaffold(context, metadata.getName(), false);
        } finally {
            if(conn != null)
                conn.close();
            if(writer != null)
                writer.close();
        }
        WMDKUtil.reboot();
        WMDKUtil.success(context);
    }

    public void domain(ServletProcessorContext context, WikiupEntityManager entityManager, Writer writer) throws SQLException, IOException {
        String manager = context.getParameter("manager");
        Document managerConfigure = WMDKUtil.findEntityManagerConfigureNode(manager);
        Connection conn = WMDKUtil.getConnection(context);
        DatabaseEntityMetadata metadata = WMDKUtil.getEntityMetadata(context, conn);
        generateEntityConfigure(entityManager, metadata, manager, managerConfigure, writer);
    }

    public Map<String, Object> page(ServletProcessorContext context, WikiupEntityManager entityManager) throws SQLException, IOException {
        EntityMetadata metadata = entityManager.getEntityMetadata(context.getParameter("domain-name", null));
        context.setAttribute("var:scaffold-entityMetadata", metadata);
        doScaffold(context, metadata.getName(), true);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", true);
        map.put("name", metadata.getName());
        return map;
    }

    private void doScaffold(ServletProcessorContext context, String entityName, boolean force) throws IOException {
        Document templates = WMDKUtil.getScaffoldTemplates(context, entityName);
        for(Document node : templates.getChildren()) {
            String name = Documents.getDocumentValue(node, "name");
            if(context.getParameter("wk-scaffold-" + name, null) != null || force)
                if(!isTemplateDisabled(templates, name))
                    WMDKUtil.generateTemplatePage(context, Documents.getDocumentValue(node, "href"), Documents.getDocumentValue(node, "uri"));
        }
    }

    private void generateEntityConfigure(WikiupEntityManager entityManager, DatabaseEntityMetadata metadata, String manager, Document managerConfigure, Writer writer) throws IOException {
        Document doc = entityManager.getEntityManager(manager).describe(metadata);
        WMDKUtil.decorateEntityDescription(doc, manager, new BeanPropertyDictionary(metadata));
        Documents.writeXMLHeader(writer, WikiupConfigure.CHAR_SET);
        String docType = StringUtil.trim(Documents.getChildValue(managerConfigure, "doc-type"));
        writer.write(docType);
        writer.write(docType.length() == 0 ? "" : "\n");
        Documents.printToWriter(doc, writer);
    }

    private boolean isTemplateDisabled(Document templates, String name) {
        Document module = templates.getChild(name);
        WikiupPluginManager pm = Wikiup.getModel(WikiupPluginManager.class);
        if(module != null) {
            WikiupPluginManager.Plugin plugin = pm.get(Documents.getDocumentValue(module, "module"));
            return plugin == null || plugin.isDisabled();
        }
        return false;
    }
}
