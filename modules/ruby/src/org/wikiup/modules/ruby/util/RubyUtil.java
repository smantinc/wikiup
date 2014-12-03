package org.wikiup.modules.ruby.util;

import org.jruby.embed.ScriptingContainer;
import org.wikiup.core.util.Assert;
import org.wikiup.core.util.StreamUtil;

import java.io.FileReader;
import java.io.IOException;

public class RubyUtil {
    public static Object toJava(Object obj) {
        return obj;
    }

    public static void runScriptlet(ScriptingContainer scriptingContainer, String src) {
        if(src != null) {
            FileReader reader = null;
            try {
                reader = new FileReader(src);
                scriptingContainer.runScriptlet(reader, src);
            } catch(IOException e) {
                Assert.fail(e);
            } finally {
                StreamUtil.close(reader);
            }
        }
    }
}
