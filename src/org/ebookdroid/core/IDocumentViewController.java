package org.ebookdroid.core;

import org.ebookdroid.core.IViewerActivity.IBookLoadTask;
import org.ebookdroid.core.events.ZoomListener;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.MotionEvent;

public interface IDocumentViewController extends ZoomListener {

    void init(IBookLoadTask bookLoadTask);

    void show();

    /* Page related methods */
    void goToPage(int page);

    void invalidatePageSizes(InvalidateSizeReason reason, Page changedPage);

    int getFirstVisiblePage();

    int calculateCurrentPage(ViewState viewState);

    int getLastVisiblePage();

    void verticalConfigScroll(int i);

    void redrawView();

    void redrawView(ViewState viewState);

    void setAlign(PageAlign byResValue);

    /* Infrastructure methods */

    IViewerActivity getBase();

    IDocumentView getView();

    void updateAnimationType();

    void updateMemorySettings();

    public static enum InvalidateSizeReason {
        INIT, LAYOUT, PAGE_ALIGN, ZOOM, PAGE_LOADED;
    }

    void drawView(Canvas canvas, ViewState viewState);

    boolean onLayoutChanged(boolean layoutChanged, boolean layoutLocked, Rect oldLaout, Rect newLayout);

    Rect getScrollLimits();

    boolean onTouchEvent(MotionEvent ev);

    void onScrollChanged(final int newPage, final int direction);

    boolean dispatchKeyEvent(final KeyEvent event);

    ViewState updatePageVisibility(final int newPage, final int direction, final float zoom);

    void pageUpdated(int viewIndex);
}
