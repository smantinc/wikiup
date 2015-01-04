package org.wikiup.extensions.wmdk.leprechaun;

import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SampleCategoryServletAction {
    public Document list(ServletProcessorContext context) {
        String path = context.getParameter("path");
        File dir = new File(context.getRealPath(path));
        Map<String, List<File>> files = new LinkedHashMap<String, List<File>>();

        if(dir.isDirectory())
            for(File file : dir.listFiles()) {
                String name = FileUtil.getFileName(file.getName());
                List<File> list = files.get(name);
                if(file.isFile()) {
                    if(list == null)
                        files.put(name, (list = new LinkedList<File>()));
                    list.add(file);
                }
            }

        Document doc = Documents.create("root");
        for(String key : files.keySet()) {
            List<File> list = files.get(key);
            Document category = doc.addChild("category");
            for(File file : list) {
                String ext = FileUtil.getFileExt(file.getName());
                if(!"py,rb.ssjs".contains(ext) || file.getName().contains("exec"))
                    addFileItem(file, category, path, false);
                if("html,jsp,php,vm,py,rb".contains(ext))
                    addFileItem(file, category, path, true);
            }
        }
        return doc;
    }

    private void addFileItem(File file, Document category, String path, boolean sourceView) {
        Document item = category.addChild("item");
        Documents.setAttributeValue(item, "title", file.getName());
        Documents.setAttributeValue(item, "src", StringUtil.connect(path, file.getName(), '/'));
        if(sourceView)
            Documents.setAttributeValue(item, "sourceView", true);
    }
}
