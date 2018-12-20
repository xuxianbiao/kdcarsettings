package org.ebookdroid.core.models;

import org.ebookdroid.core.PageIndex;
import org.ebookdroid.core.events.CurrentPageListener;
import org.ebookdroid.core.events.ListenerProxy;
import org.ebookdroid.core.log.LogContext;
import org.ebookdroid.utils.CompareUtils;

public class CurrentPageModel extends ListenerProxy {

    protected static final LogContext LCTX = LogContext.ROOT.lctx("DocModel");

    protected PageIndex currentIndex = PageIndex.FIRST;

    public CurrentPageModel() {
        super(CurrentPageListener.class);
    }

    public void setCurrentPageIndex(final PageIndex newIndex) {
        if (!CompareUtils.equals(currentIndex, newIndex)) {
            if (LCTX.isDebugEnabled()) {
                LCTX.d("Current page changed: " + "currentIndex" + " -> " + newIndex);
            }

            final PageIndex oldIndex = this.currentIndex;
            this.currentIndex = newIndex;

            this.<CurrentPageListener>getListener().currentPageChanged(oldIndex, newIndex);
        }
    }

    public PageIndex getCurrentIndex() {
        return this.currentIndex;
    }

    public int getCurrentViewPageIndex() {
        return this.currentIndex.viewIndex;
    }

    public int getCurrentDocPageIndex() {
        return this.currentIndex.docIndex;
    }
}
