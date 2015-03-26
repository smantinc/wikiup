package org.wikiup.framework.impl.cl;

import org.wikiup.Wikiup;
import org.wikiup.framework.bean.WikiupNamingDirectory;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.DocumentAware;
import org.wikiup.core.inf.Releasable;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractJarArchiveLoader implements Releasable, DocumentAware {
    protected ClassLoader classLoader;
    private final Pattern VERSION_PATTERN = Pattern.compile("([\\d\\w_\\-]+)-([\\d]+)\\.([\\d]+)\\.([\\d]+)\\.jar");

    public void setUrls(URL[] urls) {
        classLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }

    public void release() {
        classLoader = null;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void aware(Document desc) {
        List<URL> list = new ArrayList<URL>();
        WikiupNamingDirectory wnd = Wikiup.getModel(WikiupNamingDirectory.class);
        for(Document doc : desc.getChildren()) {
            String value = Documents.getDocumentValue(doc);
            File file = new File(StringUtil.evaluateEL(value, wnd));
            if(file.exists())
                if(file.isFile())
                    appendFileURL(list, file);
                else if(file.isDirectory())
                    for(File f : getJarsFromDirectory(file))
                        appendFileURL(list, f);
        }
        setUrls(list.toArray(new URL[list.size()]));
    }

    private void appendFileURL(Collection<URL> colls, File file) {
        try {
            colls.add(file.toURI().toURL());
        } catch(MalformedURLException e) {
        }
    }

    private Collection<File> getJarsFromDirectory(File directory) {
        Map<String, File> latestJar = new HashMap<String, File>();
        File[] files = directory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".jar");
            }
        });
        for(File file : files) {
            String name = file.getName().toLowerCase();
            Matcher matcher = VERSION_PATTERN.matcher(name);
            String moduleName = FileUtil.getFileName(name);
            if(matcher.matches())
                moduleName = matcher.group(1);
            File jar = latestJar.get(moduleName);
            if(jar == null || (matcher.matches() && getJarVersion(matcher, moduleName) > getJarVersion(null, jar.getName())))
                latestJar.put(moduleName, file);
        }
        return latestJar.values();
    }

    private int getJarVersion(Matcher matcher, String name) {
        if(matcher == null)
            matcher = VERSION_PATTERN.matcher(name);
        if(matcher.matches()) {
            int major = ValueUtil.toInteger(matcher.group(2), 0) * 100000;
            int minor = ValueUtil.toInteger(matcher.group(3), 0) * 1000;
            int build = ValueUtil.toInteger(matcher.group(4), 0);
            return major + minor + build;
        }
        return 0;
    }
}
