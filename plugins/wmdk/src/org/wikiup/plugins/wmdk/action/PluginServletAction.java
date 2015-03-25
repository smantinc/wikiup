package org.wikiup.plugins.wmdk.action;

import org.wikiup.core.Wikiup;
import org.wikiup.framework.bean.I18nResourceManager;
import org.wikiup.framework.bean.WikiupConfigure;
import org.wikiup.framework.bean.WikiupPluginManager;
import org.wikiup.core.impl.context.MapContext;
import org.wikiup.core.impl.context.XPathContext;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.Interfaces;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.plugins.wmdk.util.WMDKUtil;
import org.wikiup.servlet.ServletProcessorContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PluginServletAction {
    private static final String[] MODULE_DESCRIPTION_NAMES = {"title", "brief"};
    private static Map<String, ArtifactDownloader> downloaders = new HashMap<String, ArtifactDownloader>();


    public void list(ServletProcessorContext context) {
        Document plugins = WMDKUtil.getPluginConfigure(context);
        Document resp = context.getResponseXML();
        WMDKUtil.copyPluginList(resp, plugins);
        mergeModuleDescriptions(context.getServletRequest().getLocale(), resp);
    }

    public void activate(ServletProcessorContext context) throws IOException {
        Document doc = WMDKUtil.copyPluginList(Documents.create("root"));
        WikiupPluginManager pm = Wikiup.getModel(WikiupPluginManager.class);
        for(Document node : doc.getChildren()) {
            String moduleName = Documents.getId(node);
            String status = moduleName != null ? context.getParameter(moduleName, null) : null;
            boolean required = Documents.getAttributeBooleanValue(node, "required", false);
            boolean removed = "removed".equals(status);
            Documents.setAttributeValue(node, "disabled", "off".equals(status) && !required);
            Documents.setAttributeValue(node, "removed", removed && !required);
            if(removed) {
                WikiupPluginManager.Plugin plugin = pm.get(moduleName);
                if(plugin != null)
                    FileUtil.delete(new File(plugin.getJarFile().getName()));
            }
        }
        File file = WMDKUtil.getPluginsConfigureFile(context);
        file.getParentFile().mkdirs();
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), WikiupConfigure.CHAR_SET);
            Documents.writeXMLHeader(writer, WikiupConfigure.CHAR_SET);
            Documents.printToWriter(doc, writer);
        } finally {
            if(writer != null)
                writer.close();
        }
        reload(context);
    }

    public void reload(ServletProcessorContext context) {
        WMDKUtil.reload();
        WMDKUtil.success(context);
    }

    public Document revision(ServletProcessorContext context) {
        Document doc = WMDKUtil.copyPluginList(Documents.create("root"));
        Document modules = Documents.create("modules");
        String moduleName = context.getParameter("module", null);
        for(Document node : doc.getChildren())
            if(moduleName == null || moduleName.equals(Documents.getId(node, null)))
                Documents.mergeAttribute(modules.addChild(node.getName()), node);
        return modules;
    }

    public void mount(final ServletProcessorContext context, Document desc) throws IOException {
        List<Dictionary<?>> artifacts = new LinkedList<Dictionary<?>>();
        MapContext<String> mapContext = new MapContext<String>();

        for(Object key : context.getServletRequest().getParameterMap().keySet()) {
            String name = ValueUtil.toString(key, "");
            mapContext.set(name, context.getParameter(name, null));
        }

        Document dependencies = Documents.loadFromURL(StringUtil.evaluateEL(Documents.getDocumentValue(desc, "dependencies"), mapContext));
        for(Document dependency : dependencies.getChildren("dependency"))
            artifacts.add(new XPathContext(dependency));
        ArtifactDownloader downloader = new ArtifactDownloader(context, desc, artifacts, mapContext);
        String mid = String.valueOf(downloader.hashCode());
        downloaders.put(mid, downloader);
        new Thread(downloader).start();
        WMDKUtil.success(context, "mid", mid);
    }

    public Document mping(ServletProcessorContext context) {
        Document resp = Documents.create("root");
        ArtifactDownloader downloader = downloaders.get(context.getParameter("mid"));
        if(downloader != null) {
            Documents.setAttributeValue(resp, "contentLength", downloader.contentLength);
            Documents.setAttributeValue(resp, "artifactName", downloader.artifactName);
            Documents.setAttributeValue(resp, "bytesReceived", downloader.bytesReceived);
        } else
            Documents.setAttributeValue(resp, "finished", true);
        return resp;
    }

    private void mergeModuleDescriptions(Locale locale, Document desc) {
        WikiupPluginManager mm = Wikiup.getModel(WikiupPluginManager.class);
        Dictionary<String> language = Wikiup.getModel(I18nResourceManager.class).getLanguageBundle(locale);
        for(Document doc : desc.getChildren()) {
            WikiupPluginManager.Plugin plugin = mm.get(Documents.getId(doc));
            Document info = plugin != null ? plugin.getDocument() : null;
            Documents.setAttributeValue(doc, "disabled", plugin != null ? plugin.isDisabled() : false);
            for(String name : MODULE_DESCRIPTION_NAMES)
                Documents.setAttributeValue(doc, name, info != null ? StringUtil.evaluateEL(Documents.getDocumentValue(info, name), language) : "");
        }
    }

    private static class ArtifactDownloader implements Runnable {
        private ServletProcessorContext context;
        private Document desc;
        private List<Dictionary<?>> artifacts;
        private Dictionary<?> artifact;

        private String artifactName;
        private long contentLength;
        private long bytesReceived;

        public ArtifactDownloader(ServletProcessorContext context, Document desc, List<Dictionary<?>> artifacts, Dictionary<?> artifact) {
            this.context = context;
            this.desc = desc;
            this.artifacts = artifacts;
            this.artifact = artifact;
        }

        public void run() {
            try {
                for(Dictionary<?> artifact : artifacts)
                    downloadArtifact(artifact);

                downloadArtifact(artifact);
            } catch(Exception ex) {
            } finally {
                downloaders.remove(String.valueOf(this.hashCode()));
            }
        }

        private void downloadArtifact(Dictionary<?> dictionary) throws IOException {
            String fileName = StringUtil.evaluateEL(Documents.getDocumentValue(desc, "file-name"), dictionary);
            File file = null;
            for(Document node : desc.getChildren("lib")) {
                String foo = Documents.getAttributeValue(node, "for", null);
                if(foo == null || fileName.matches(foo)) {
                    String lib = context.getRealPath(Documents.getDocumentValue((node)));
                    file = new File(lib, fileName);
                    break;
                }
            }
            if(file != null && !file.exists()) {
                URL url = new URL(StringUtil.evaluateEL(Documents.getDocumentValue(desc, "repository"), dictionary));
                HttpURLConnection conn = Interfaces.cast(HttpURLConnection.class, url.openConnection());
                contentLength = ValueUtil.toLong(conn.getHeaderField("Content-Length"), 0);
                bytesReceived = 0;
                artifactName = file.getName();
                InputStream is = conn.getInputStream();
                OutputStream os = null;
                try {
                    file.getParentFile().mkdirs();
                    os = new FileOutputStream(file, false);
                    int len;
                    byte[] buf = new byte[8192];

                    while((len = is.read(buf, 0, buf.length)) > 0) {
                        os.write(buf, 0, len);
                        bytesReceived += len;
                    }
                    os.flush();
                } finally {
                    StreamUtil.close(os);
                    StreamUtil.close(is);
                }
            }
        }
    }
}
