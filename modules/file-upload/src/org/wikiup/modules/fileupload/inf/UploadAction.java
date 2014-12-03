package org.wikiup.modules.fileupload.inf;

import org.apache.commons.fileupload.FileItem;
import org.wikiup.core.inf.Document;
import org.wikiup.servlet.ServletProcessorContext;

public interface UploadAction {
    public boolean doUpload(ServletProcessorContext context, Document desc, FileItem item, Document upload);
}
