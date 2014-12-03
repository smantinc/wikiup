package org.wikiup.modules.fileupload.impl;

import org.apache.commons.fileupload.FileItem;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StreamUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.fileupload.inf.UploadAction;
import org.wikiup.servlet.ServletProcessorContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


public class FileUploadAction implements UploadAction {

    public boolean doUpload(ServletProcessorContext context, Document desc, FileItem item, Document upload) {
        String repository = context.getContextAttribute(desc, "repository");
        String filename = context.getContextAttribute(desc, "file-name", null);
        File file = new File(repository, ValueUtil.toString(filename, item.getName()));
        OutputStream os = null;
        InputStream is = null;
        try {
            os = new FileOutputStream(file);
            is = item.getInputStream();
            StreamUtil.copy(new FileOutputStream(file), item.getInputStream());
        } catch(Exception ex) {
            Assert.fail(ex);
        } finally {
            StreamUtil.close(os);
            StreamUtil.close(is);
        }
        return true;
    }
}
