package org.ebookdroid.core.views;

import org.ebookdroid.core.models.ZoomModel;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class PageViewZoomControls extends LinearLayout {

    public PageViewZoomControls(final Context context, final ZoomModel zoomModel) {
        super(context);
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.BOTTOM);
        addView(new ZoomRoll(context, zoomModel));

        zoomModel.addListener(this);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return false;
    }
}
