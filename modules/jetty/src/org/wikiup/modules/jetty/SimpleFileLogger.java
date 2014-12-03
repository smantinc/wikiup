package org.wikiup.modules.jetty;

import org.mortbay.log.Logger;
import org.mortbay.util.DateCache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class SimpleFileLogger implements Logger {
    private static DateCache _dateCache;
    private static boolean debug = System.getProperty("DEBUG", null) != null;
    private String name;

    private PrintStream ps;

    static {
        try {
            _dateCache = new DateCache("yyyy-MM-dd HH:mm:ss");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public SimpleFileLogger() {
        File logfile = new File(System.getProperty("wikiup.module.jetty.logfile"));
        if(!logfile.exists())
            logfile.getParentFile().mkdirs();
        init(logfile);
    }

    public SimpleFileLogger(String name) {
        this.name = name;
        init(new File(name));
    }

    public boolean isDebugEnabled() {
        return debug;
    }

    public void setDebugEnabled(boolean enabled) {
        debug = enabled;
    }

    public void info(String msg, Object arg0, Object arg1) {
        String d = _dateCache.now();
        int ms = _dateCache.lastMs();
        ps.println(d + (ms <= 99 ? ms <= 0 ? ".00" : ".0" : ".") + ms + ":INFO:  " + format(msg, arg0, arg1));
    }

    public void debug(String msg, Throwable th) {
        if(debug) {
            String d = _dateCache.now();
            int ms = _dateCache.lastMs();
            ps.println(d + (ms <= 99 ? ms <= 0 ? ".00" : ".0" : ".") + ms + ":DEBUG: " + msg);
            if(th != null)
                th.printStackTrace();
        }
    }

    public void debug(String msg, Object arg0, Object arg1) {
        if(debug) {
            String d = _dateCache.now();
            int ms = _dateCache.lastMs();
            ps.println(d + (ms <= 99 ? ms <= 0 ? ".00" : ".0" : ".") + ms + ":DEBUG: " + format(msg, arg0, arg1));
        }
    }

    public void warn(String msg, Object arg0, Object arg1) {
        String d = _dateCache.now();
        int ms = _dateCache.lastMs();
        ps.println(d + (ms <= 99 ? ms <= 0 ? ".00" : ".0" : ".") + ms + ":WARN:  " + format(msg, arg0, arg1));
    }

    public void warn(String msg, Throwable th) {
        String d = _dateCache.now();
        int ms = _dateCache.lastMs();
        ps.println(d + (ms <= 99 ? ms <= 0 ? ".00" : ".0" : ".") + ms + ":WARN:  " + msg);
        if(th != null)
            th.printStackTrace();
    }

    private String format(String msg, Object arg0, Object arg1) {
        int i0 = msg.indexOf("{}");
        int i1 = i0 >= 0 ? msg.indexOf("{}", i0 + 2) : -1;
        if(arg1 != null && i1 >= 0)
            msg = msg.substring(0, i1) + arg1 + msg.substring(i1 + 2);
        if(arg0 != null && i0 >= 0)
            msg = msg.substring(0, i0) + arg0 + msg.substring(i0 + 2);
        return msg;
    }

    public Logger getLogger(String name) {
        if(name == null && this.name == null || name != null && name.equals(this.name))
            return this;
        else
            return new SimpleFileLogger(name);
    }

    public String toString() {
        return name;
    }

    private void init(File file) {
        try {
            ps = new PrintStream(new FileOutputStream(file, false));
            System.setOut(ps);
            System.setErr(ps);
        } catch(FileNotFoundException e) {
            ps = System.err;
        }
    }
}