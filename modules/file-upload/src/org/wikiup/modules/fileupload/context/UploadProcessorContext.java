package org.wikiup.modules.fileupload.context;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.mp.InstanceModelProvider;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.BeanContainer;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.fileupload.UploadUtil;
import org.wikiup.modules.fileupload.inf.UploadAction;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ProcessorContext;
import org.wikiup.servlet.inf.ServletProcessorContextAware;
import org.wikiup.servlet.util.ActionUtil;

import java.util.List;

public class UploadProcessorContext implements ProcessorContext, ServletProcessorContextAware, DocumentAware {

    private Document configure = Documents.create("upload");
    private ServletFileUpload upload = new ServletFileUpload();
    private String denyExt, allowExt, entry;
    private Document template;
    private ServletProcessorContext context;

    public void setServletProcessorContext(ServletProcessorContext context) {
        this.context = context;
    }

    public BeanContainer getModelContainer(String name, Dictionary<?> params) {
        return new InstanceModelProvider(name != null ? configure.getChild(name) : configure);
    }

    public Object get(String name) {
        return Documents.getAttributeValue(configure, name, null);
    }

    private void doUpload(ServletProcessorContext context) {
        List items = UploadUtil.parseUploadParameters(context, upload);
        String entry = StringUtil.evaluateEL(this.entry, context);
        Document template = this.entry != null ? this.template.getChild(entry) : this.template;
        int i, count = 0;
        for(i = 0; i < items.size(); i++) {
            FileItem item = (FileItem) items.get(i);
            if(!item.isFormField() && item.getSize() > 0) {
                String fileExt = FileUtil.getFileExt(item.getName());
                if(!fileExt.matches(denyExt) || fileExt.matches(allowExt)) {
                    Document field = getUpdatedFieldAction(template, item.getFieldName());
                    String contextName = Documents.getAttributeValue(field, "context-name", null);
//					ClassFactoryBuildingDocument doc = ClassFactoryBuildingDocument.getDocument(field);
//					doc.appendInstaceFilter(new Wikiup.DocumentInitialization());
                    UploadAction uf = Wikiup.getInstance().getBean(UploadAction.class, field);
                    Document upload = contextName != null ? configure.addChild(contextName) : configure;
                    Documents.setAttributeValue(upload, "field-name", item.getFieldName());
                    Documents.setAttributeValue(upload, "file-name", FileUtil.getFileName(item.getName()));
                    Documents.setAttributeValue(upload, "file-ext", fileExt);
                    if(uf.doUpload(context, field, item, upload)) {
                        count++;
                        if(field.getChild("action") != null)
                            ActionUtil.doAction(context, field.getChild("action"));
                    }
                }
            }
        }
        Documents.setAttributeValue(configure, "wk:size", String.valueOf(count));
    }

    private void loadUploadConfig(Document node) {
        UploadUtil.loadUploadConfig(node, upload);
        denyExt = Documents.getDocumentValue(node, "deny-file-ext", ".*");
        allowExt = Documents.getDocumentValue(node, "allow-file-ext", "gif|jpg|jpeg|png");
    }

    private Document getUpdatedFieldAction(Document doc, String name) {
        Document def = null;
        for(Document node : doc.getChildren("field")) {
            String fieldName = Documents.getAttributeValue(node, "field-name", null);
            if(StringUtil.compare(fieldName, name))
                return node;
            def = fieldName == null ? node : def;
        }
        return def;
    }

    public void aware(Document desc) {
        loadUploadConfig(desc.getChild("configure"));
        entry = Documents.getAttributeValue(desc, "entry", null);
        template = desc.getChild("templates");
        doUpload(context);
    }
}
