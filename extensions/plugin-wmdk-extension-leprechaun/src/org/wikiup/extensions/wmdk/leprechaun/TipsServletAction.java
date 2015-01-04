package org.wikiup.extensions.wmdk.leprechaun;

import org.wikiup.core.bean.I18nResourceManager;
import org.wikiup.core.bean.WikiupConfigure;
import org.wikiup.core.inf.Document;
import org.wikiup.core.inf.Getter;
import org.wikiup.core.util.Documents;
import org.wikiup.core.util.StringUtil;
import org.wikiup.core.util.ValueUtil;
import org.wikiup.servlet.ServletProcessorContext;

public class TipsServletAction {
    static private final Document TIPS_CONFIGURE = WikiupConfigure.getInstance().lookup("wmdk/leprechaun/tips");
    static private final Document HELP_CONFIGURE = WikiupConfigure.getInstance().lookup("wmdk/leprechaun/help");

    public void tips(ServletProcessorContext context, I18nResourceManager i18n) {
        getTip(context, context.getParameter("seq", "1"), i18n);
    }

    private void getTip(ServletProcessorContext context, String seq, I18nResourceManager i18n) {
        String s = Documents.getDocumentValue(TIPS_CONFIGURE, "s" + seq);
        Getter<String> language = i18n.getLanguageBundle(context.getServletRequest().getLocale());
        String tip = language.get("wmdk.extension.leprechaun.tips.t" + seq);
        Document resp = context.getResponseXML();
        Documents.setChildValue(resp, "tip", tip);
        if(s != null)
            Documents.setChildValue(resp, "spot-light", s);
    }

    public void help(ServletProcessorContext context, I18nResourceManager i18n) {
        String[] path = StringUtil.splitNamespaces(context.getParameter("path"));
        Document doc = Documents.getDocumentByPath(HELP_CONFIGURE, path, path.length);
        if(doc == null)
            doc = Documents.getDocumentByPath(HELP_CONFIGURE, path, path.length - 1);
        String seq = doc != null ? ValueUtil.toString(doc.getObject()) : null;
        if(seq != null) {
            Documents.setChildValue(context.getResponseXML(), "seq", seq);
            getTip(context, seq, i18n);
        }
    }
}
