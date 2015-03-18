package org.wikiup.modules.jsp.tag;

import java.io.IOException;
import java.util.Iterator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.Dictionary;
import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.jsp.JspServletContainer;
import org.wikiup.modules.jsp.JspServletContext;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.util.ProcessorContexts;

public class ForeachTag extends BodyTagSupport {
    private String in;
    private ServletProcessorContext context;
    private Iterator<?> iterator;
    private String body;
    private ExpressionLanguage<Dictionary<?>, String> el = Wikiup.getModel(JspServletContainer.class).getEl();

    public void setIn(String in) {
        this.in = in;
    }

    @Override
    public int doEndTag() throws JspException {
        context.getBeanStack().pop();
        return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
        int result = SKIP_BODY;
        Object ctx;
        context = JspServletContext.get().context;
        ServletProcessorContext.BeanStack beanStack = context.getBeanStack();
        if(in != null) {
            ctx = ProcessorContexts.get(context, StringUtil.evaluateEL(in, context), Null.getInstance());
            beanStack.push(ctx);
        }
        Iterable<?> iterable = beanStack.peek(Iterable.class); 
        iterator = iterable != null ? iterable.iterator() : Null.getInstance();
        if(iterator.hasNext() && (ctx = iterator.next()) != null) {
            beanStack.push(ctx);
            body = null;
            result = EVAL_BODY_BUFFERED;
        }
        if(in != null)
            beanStack.pop();
        return result;
    }

    @Override
    public int doAfterBody() throws JspException {
        ServletProcessorContext.BeanStack beanStack = context.getBeanStack();
        beanStack.pop();
        Object ctx;
        if(body == null)
            body = bodyContent.getString();
        try {
            bodyContent.getEnclosingWriter().print(el.evaluate(context, body));
        } catch(IOException e) {
        }
        if(iterator.hasNext() && (ctx = iterator.next()) != null) {
            beanStack.push(ctx);
            return EVAL_BODY_AGAIN;
        }
        return 0;
    }
}
