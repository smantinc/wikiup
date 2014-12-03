package org.wikiup.modules.fileupload.impl;

import org.apache.commons.fileupload.FileItem;
import org.wikiup.core.Wikiup;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.database.orm.Entity;
import org.wikiup.modules.fileupload.inf.UploadAction;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.beans.MimeTypes;

@Deprecated
public class EntityUploadAction implements UploadAction {
    public boolean doUpload(ServletProcessorContext context, Document desc, FileItem item, Document upload) {
        String blobName = Documents.getAttributeValue(desc, "blob-name");
        Entity blob = context.getEntity(context.get("const:mapper:blob:name," + blobName + ",entity-name").toString());
        String fileExt = FileUtil.getFileExt(item.getName());
        String id;
        MimeTypes mimeTypes = Wikiup.getModel(MimeTypes.class);
        blob.set("contentType", mimeTypes.get(fileExt));
        blob.get("content").setObject(new UploadFileItemBlob(item));
        blob.insert();
        id = Documents.getAttributeValue(blob.getRelatives("last"), "id");
        Documents.setAttributeValue(upload, "id", id);
        return true;
    }
}
