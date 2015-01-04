package org.wikiup.plugins.wmdk.action;

import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.impl.translator.lf.NotLogicalFilter;
import org.wikiup.core.impl.translator.lf.RegexpMatchLogicalFilter;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Resource;
import org.wikiup.core.inf.ext.LogicalFilter;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.plugins.wmdk.util.WMDKUtil;
import org.wikiup.servlet.ServletProcessorContext;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class WebResourceServletAction {
    public void types(ServletProcessorContext context, Document desc) {
        Document resp = context.getResponseXML();
        for(Document node : desc.getChildren("template")) {
            Document child = resp.addChild("item");
            Documents.setAttributeValue(child, "name", Documents.getAttributeValue(node, "name", Documents.getAttributeValue(node, "suffix")));
            Documents.setAttributeValue(child, "uri", Documents.getAttributeValue(node, "uri"));
        }
    }

    public void create(ServletProcessorContext context, Document desc) throws IOException {
        String name = context.getParameter("name");
        String type = context.getParameter("type");
        String ext = FileUtil.getFileExt(name);
        Document doc = Documents.findMatchesChild(desc, "template", "suffix", StringUtil.isEmpty(ext) ? type : ext);
        WMDKUtil.generateTemplatePage(context, Documents.getDocumentValue(doc, "uri"), StringUtil.isEmpty(ext) ? StringUtil.connect(name, type, '.') : name);
        WMDKUtil.success(context);
    }

    public void get(ServletProcessorContext context) throws IOException {
        String uri = context.getParameter("uri");
        Resource url = context.getResource(uri);
        context.setContentType("text/plain;" + WikiupConfigure.CHAR_SET);
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(url.open(), WikiupConfigure.CHAR_SET);
            StreamUtil.copy(context.getResponseWriter(), reader);
        } finally {
            StreamUtil.close(reader);
        }
    }

    public void update(ServletProcessorContext context) throws IOException {
        String uri = context.getParameter("uri");
        String content = context.getParameter("content");
        File file = new File(context.getRealPathByURI(uri));
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), WikiupConfigure.CHAR_SET);
            writer.write(content);
        } finally {
            if(writer != null)
                writer.close();
        }
        WMDKUtil.success(context);
    }

    public void rename(ServletProcessorContext context) {
        String uri = context.getParameter("uri");
        String name = context.getParameter("name");
        File from = new File(context.getRealPathByURI(uri));
        boolean success = from.renameTo(new File(FileUtil.joinPath(from.getParent(), name)));
        WMDKUtil.success(context, success);
    }

    public void delete(ServletProcessorContext context) {
        String uri = context.getParameter("uri");
        boolean success = FileUtil.delete(new File(context.getRealPathByURI(uri)));
        WMDKUtil.success(context, success);
    }

    public void list(ServletProcessorContext context) {
        String root = context.getContextPath(context.getParameter("node", ""));
        final String type = context.getParameter("t", "df");
        File path = new File(context.getRealPath(root));
        Document response = context.getResponseXML();
        File[] files = path.listFiles(new FileFilter() {
            public boolean accept(File file) {
                if(file.getName().startsWith("."))
                    return false;
                if(file.isDirectory())
                    return type.indexOf('d') != -1;
                return type.indexOf('f') != -1;
            }
        });
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                if(f1.isDirectory())
                    return f2.isDirectory() ? f1.compareTo(f2) : -1;
                return f2.isDirectory() ? 1 : f1.compareTo(f2);
            }
        });
        for(File file : files) {
            Document item = response.addChild("entry");
            Documents.setAttributeValue(item, "text", file.getName());
            Documents.setAttributeValue(item, "id", context.getContextURI(FileUtil.joinPath(root, file.getName(), '/')));
            Documents.setAttributeValue(item, "leaf", !file.isDirectory());
            Documents.setAttributeValue(item, "expandable", file.isDirectory());
        }
    }

    public void export(ServletProcessorContext context, Document desc) throws IOException {
        context.setContentType("application/x-download");
        context.setHeader("Content-Disposition", "attachment; filename=archive.zip");
        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(context.getResponseOutputStream());
            for(Document node : desc.getChildren()) {
                String path = context.getContextAttribute(node, "path", null);
                String fileName = context.getContextAttribute(node, "file", context.getContextAttribute(node, "dir", null));
                String uri = context.getContextAttribute(node, "uri", null);
                if(uri == null) {
                    Document exclude = node.getChild("exclude");
                    LogicalFilter<String> filter = exclude != null ? new NotLogicalFilter<String>(new RegexpMatchLogicalFilter(Documents.getDocumentValue(exclude, "regexp", null))) : null;
                    if(path == null)
                        out.putNextEntry(new ZipEntry(fileName + "/"));
                    else
                        zip(out, new File(path), fileName, filter);
                } else {
                    String template = WMDKUtil.generateTemplatePage(context, uri);
                    zip(out, template.getBytes(WikiupConfigure.CHAR_SET), fileName);
                }
            }
            out.flush();
        } finally {
            if(out != null)
                out.close();
        }
    }

    private void zip(ZipOutputStream out, File f, String base, LogicalFilter<String> filter) throws IOException {
        if(filter == null || filter.translate(f.getName()))
            if(f.isDirectory()) {
                out.putNextEntry(new ZipEntry(base + "/"));
                for(File file : f.listFiles())
                    zip(out, file, StringUtil.isEmpty(base) ? file.getName() : StringUtil.connect(base, file.getName(), '/'), filter);
            } else {
                FileInputStream in = null;
                try {
                    out.putNextEntry(new ZipEntry(base));
                    StreamUtil.copy(out, in = new FileInputStream(f));
                } finally {
                    if(in != null)
                        in.close();
                }
            }
    }

    private void zip(ZipOutputStream out, byte[] bytes, String fileName) throws IOException {
        out.putNextEntry(new ZipEntry(fileName));
        out.write(bytes);
    }
}
