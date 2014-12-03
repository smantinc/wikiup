package org.wikiup.modules.updater;

import org.wikiup.core.bootstrap.Bootstrap;
import org.wikiup.core.bootstrap.inf.ext.BootstrapAction;
import org.wikiup.core.inf.Document;
import org.wikiup.core.util.Documents;

public class UpdaterBootstrapAction implements BootstrapAction {
    public void doAction(Bootstrap bootstrap, Document desc) {
        if(!Documents.getAttributeBooleanValue(desc, "disabled", false))
            UpdaterUtil.doUpdate(desc);
    }
}
