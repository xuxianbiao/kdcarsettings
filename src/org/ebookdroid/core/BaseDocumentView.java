package org.ebookdroid.core;

import org.ebookdroid.core.DrawThread.DrawTask;
import org.ebookdroid.core.models.DocumentModel;
import org.ebookdroid.core.settings.SettingsManager;
import org.ebookdroid.utils.Flag;
import org.ebookdroid.utils.MathUtils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class BaseDocumentView extends View implements IDocumentView {

    protected final IViewerActivity base;

    protected final Scroller scroller;

    protected PageAlign align;

    protected DrawThread drawThread;

    protected boolean layoutLocked;

    protected final AtomicReference<Rect> layout = new AtomicReference<Rect>();

    protected final Flag layoutFlag = new Flag();

    public BaseDocumentView(final IViewerActivity baseActivity) {
        super(baseActivity.getContext());
        this.base = baseActivity;
        this.scroller = new Scroller(getContext());

        setKeepScreenOn(SettingsManager.getAppSettings().isKeepScreenOn());
        setFocusable(true);
        setFocusableInTouchMode(true);
        // getHolder().addCallback(this);
        drawThread = new DrawThread(null);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#getView()
     */
    @Override
    public final View getView() {
        return this;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#getBase()
     */
    @Override
    public final IViewerActivity getBase() {
        return base;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#getScroller()
     */
    @Override
    public final Scroller getScroller() {
        return scroller;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#invalidateScroll()
     */
    @Override
    public final void invalidateScroll() {
        stopScroller();

        final float scrollScaleRatio = getScrollScaleRatio();
        scrollTo((int) (getScrollX() * scrollScaleRatio), (int) (getScrollY() * scrollScaleRatio));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#invalidateScroll(float, float)
     */
    @Override
    public final void invalidateScroll(final float newZoom, final float oldZoom) {
        stopScroller();

        final float ratio = newZoom / oldZoom;
        scrollTo((int) ((getScrollX() + getWidth() / 2) * ratio - getWidth() / 2),
                (int) ((getScrollY() + getHeight() / 2) * ratio - getHeight() / 2));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#startPageScroll(int, int)
     */
    @Override
    public void startPageScroll(final int dx, final int dy) {
        scroller.startScroll(getScrollX(), getScrollY(), dx, dy);
        redrawView();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#startFling(float, float, android.graphics.Rect)
     */
    @Override
    public void startFling(final float vX, final float vY, final Rect limits) {
        scroller.fling(getScrollX(), getScrollY(), -(int) vX, -(int) vY, limits.left, limits.right, limits.top,
                limits.bottom);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#continueScroll()
     */
    @Override
    public void continueScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#forceFinishScroll()
     */
    @Override
    public void forceFinishScroll() {
        if (!scroller.isFinished()) { // is flinging
            scroller.forceFinished(true); // to stop flinging on touch
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.view.View#onScrollChanged(int, int, int, int)
     */
    @Override
    protected final void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        base.getDocumentController().onScrollChanged(-1, t - oldt);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.view.View#onTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        super.onTouchEvent(ev);
        return base.getDocumentController().onTouchEvent(ev);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#scrollTo(int, int)
     */
    @Override
    public final void scrollTo(final int x, final int y) {
        final Runnable r = new Runnable() {

            @Override
            public void run() {
                final IDocumentViewController dc = base.getDocumentController();
                final DocumentModel dm = base.getDocumentModel();
                if (dc != null && dm != null) {
                    final Rect l = dc.getScrollLimits();
                    BaseDocumentView.super.scrollTo(MathUtils.adjust(x, l.left, l.right),
                            MathUtils.adjust(y, l.top, l.bottom));
                }
            }
        };

        base.getActivity().runOnUiThread(r);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#getViewRect()
     */
    @Override
    public final RectF getViewRect() {
        return new RectF(getScrollX(), getScrollY(), getScrollX() + getWidth(), getScrollY() + getHeight());
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#changeLayoutLock(boolean)
     */
    @Override
    public void changeLayoutLock(final boolean lock) {
        post(new Runnable() {

            @Override
            public void run() {
                layoutLocked = lock;
            }
        });
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#isLayoutLocked()
     */
    @Override
    public boolean isLayoutLocked() {
        return layoutLocked;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.view.View#onLayout(boolean, int, int, int, int)
     */
    @Override
    protected final void onLayout(final boolean layoutChanged, final int left, final int top, final int right,
            final int bottom) {
        super.onLayout(layoutChanged, left, top, right, bottom);

        final Rect oldLayout = layout.getAndSet(new Rect(left, top, right, bottom));
        base.getDocumentController().onLayoutChanged(layoutChanged, layoutLocked, oldLayout, layout.get());

        if (oldLayout == null) {
            layoutFlag.set();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#waitForInitialization()
     */
    @Override
    public final void waitForInitialization() {
        while (!layoutFlag.get()) {
            layoutFlag.waitFor(TimeUnit.SECONDS, 1);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#getScrollScaleRatio()
     */
    @Override
    public float getScrollScaleRatio() {
        final Page page = getBase().getDocumentModel().getCurrentPageObject();
        final float zoom = getBase().getZoomModel().getZoom();

        if (page == null || page.getBounds(zoom) == null) {
            return 0;
        }
        return getWidth() * zoom / page.getBounds(zoom).width();
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#stopScroller()
     */
    @Override
    public void stopScroller() {
        if (!scroller.isFinished()) {
            scroller.abortAnimation();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#redrawView()
     */
    @Override
    public final void redrawView() {
        redrawView(new ViewState(base.getDocumentController()));
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.ebookdroid.core.IDocumentView#redrawView(org.ebookdroid.core.ViewState)
     */
    @Override
    public final void redrawView(final ViewState viewState) {
        if (viewState != null) {
            if (drawThread != null) {
                drawThread.draw(viewState);
            }
            final DecodeService ds = base.getDecodeService();
            if (ds != null) {
                ds.updateViewState(viewState);
            }
            postInvalidate();
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(final Canvas canvas) {
        DrawTask task = drawThread.takeTask(1, TimeUnit.MILLISECONDS);
        if (task == null) {
            task = new DrawTask(new ViewState(base.getDocumentController()));
        }
        drawThread.performDrawing(canvas, task);
    }
}
