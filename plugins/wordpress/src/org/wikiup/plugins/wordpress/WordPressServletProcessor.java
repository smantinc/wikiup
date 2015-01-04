package org.wikiup.plugins.wordpress;

import org.wikiup.core.util.FileUtil;
import org.wikiup.core.util.StringUtil;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.beans.ServletContextContainer;
import org.wikiup.servlet.inf.ServletProcessor;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import java.io.IOException;

public class WordPressServletProcessor implements ServletProcessor {
    public void process(ServletProcessorContext context) {
        String uri = StringUtil.connect("", context.getRequestURI(), '/');
        String index = StringUtil.connect(uri, "index.php", '/');
        if(FileUtil.isExists(ServletContextContainer.getInstance().getRealPath(index))) {
            RequestDispatcher dispatcher = context.getServletRequest().getRequestDispatcher(index);
            try {
                dispatcher.forward(context.getServletRequest(), context.getServletResponse());
            } catch(ServletException e) {
            } catch(IOException e) {
            }
        }
    }
}
