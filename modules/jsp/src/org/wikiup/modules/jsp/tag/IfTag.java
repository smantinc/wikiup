package org.wikiup.modules.jsp.tag;

import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.modules.jsp.JspServletContext;
import org.wikiup.servlet.ServletProcessorContext;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class IfTag extends BodyTagSupport {
    private String test;

    @Override
    public int doStartTag() throws JspException {
        ServletProcessorContext context = JspServletContext.get().context;
        String t = StringUtil.evaluateEL(test, context);
        return doTest(t) ? EVAL_BODY_INCLUDE : SKIP_BODY;
    }

    public void setTest(String test) {
        this.test = test;
    }

    private boolean doTest(String test) {
        String condition = StringUtil.trim(test);
        int pos = condition != null ? condition.indexOf('=') : -1;
        if(pos != -1) {
            String lv = condition.substring(0, pos - 1);
            String rv = condition.substring(pos + 2);
            boolean eq = StringUtil.compare(StringUtil.trim(lv), StringUtil.trim(rv));
            return condition.charAt(pos - 1) == '!' ? !eq : eq;
        }
        String cond = StringUtil.trimLeft(condition, "!\t ");
        boolean value = ValueUtil.toBoolean(cond, !StringUtil.isEmpty(cond));
        boolean reversed = condition != null ? condition.startsWith("!") : false;
        return reversed ? !value : value;
    }
}
