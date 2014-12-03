package org.wikiup.modules.updater;

import org.wikiup.core.inf.Document;
import org.wikiup.servlet.ServletProcessorContext;
import org.wikiup.servlet.inf.ServletAction;

public class UpdaterServletAction implements ServletAction {
    public void doAction(ServletProcessorContext sender, Document param) {
        UpdaterUtil.doUpdate(param);
    }
}
