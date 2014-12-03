package org.wikiup.modules.jsp.tag;

import org.wikiup.core.Wikiup;
import org.wikiup.core.impl.Null;
import org.wikiup.core.inf.ExpressionLanguage;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.inf.ModelProvider;
import org.wikiup.core.util.StringUtil;
import org.wikiup.modules.jsp.JspServletContainer;
import org.wikiup.modules.jsp.JspServletContext;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.ms.ProcessorContextModelContainer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import java.io.IOException;
import java.util.Iterator;

public class ForeachTag extends BodyTagSupport {
    private String in;
    private ServletProcessorContext context;
    private Iterator<ModelProvider> iterator;
    private String body;
    private ExpressionLanguage<Getter<?>, String> el = Wikiup.getModel(JspServletContainer.class).getEl();

    public void setIn(String in) {
        this.in = in;
    }

    @Override
    public int doEndTag() throws JspException {
        context.popModelContainer();
        return EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
        ModelProvider modelProvider;
        context = JspServletContext.get().context;
        ProcessorContextModelContainer container = context.pushModelContainer();
        if(in != null) {
            modelProvider = context.getModelContainer(StringUtil.evaluateEL(in, context), Null.getInstance());
            container.setModelContainer(modelProvider);
        }
        iterator = context.getModelContainerStack().getIteratorFromContextStack(null);
        if(iterator.hasNext() && (modelProvider = iterator.next()) != null) {
            context.pushModelContainer(modelProvider);
            body = null;
            return EVAL_BODY_BUFFERED;
        }
        return SKIP_BODY;
    }

    @Override
    public int doAfterBody() throws JspException {
        context.popModelContainer();
        ModelProvider modelProvider;
        if(body == null)
            body = bodyContent.getString();
        try {
            bodyContent.getEnclosingWriter().print(el.evaluate(context, body));
        } catch(IOException e) {
        }
        if(iterator.hasNext() && (modelProvider = iterator.next()) != null) {
            context.pushModelContainer(modelProvider);
            return EVAL_BODY_AGAIN;
        }
        return 0;
    }
}
