package org.wikiup.modules.jetty;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Main {
    static Bootstrap bootstrap;

    public static void main(String[] args) throws Exception {
        if(args.length > 0) {
            Properties properties = new Properties();
            File file = new File(args[0]);
            int offset = 0;
            if(file.exists()) {
                BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
                properties.load(is);
                is.close();
                offset = 1;
            }

            loadPropertiesFromArgs(args, offset, properties);

            Configure conf = new Configure(properties);

            for(Object key : properties.keySet()) {
                String name = key != null ? key.toString() : "";
                System.setProperty(name, properties.getProperty(name));
            }

            bootstrap = new Bootstrap();
            bootstrap.start(conf);
        }
    }

    public static void stop() throws Exception {
        bootstrap.stop();
    }

    private static void loadPropertiesFromArgs(String[] args, int offset, Properties properties) {
        if(args.length > offset) {
            int i;
            for(i = offset; i < args.length; i++)
                if(args[i].startsWith("-D")) {
                    int idx = args[i].indexOf('=');
                    if(idx != -1)
                        properties.put(args[i].substring(2, idx), args[i].substring(idx + 1));
                }
        }
    }

}
