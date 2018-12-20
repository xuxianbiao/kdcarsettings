package org.ebookdroid.core;

import org.ebookdroid.core.bitmaps.BitmapRef;
import org.ebookdroid.core.codec.CodecPage;
import org.ebookdroid.core.codec.CodecPageInfo;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.File;
import java.util.List;

public interface DecodeService {

    void open(String fileName, String password);

    void decodePage(ViewState viewState, PageTreeNode node, RectF nodeBounds);

    void stopDecoding(PageTreeNode node, String reason);

    int getPageCount();

    List<OutlineLink> getOutline();

    CodecPageInfo getPageInfo(int pageIndex);

    void recycle();

    Rect getNativeSize(final float pageWidth, final float pageHeight, final RectF nodeBounds, float pageTypeWidthScale);

    Rect getScaledSize(final ViewState viewState, float pageWidth, float pageHeight, RectF nodeBounds, float pageTypeWidthScale, int sliceGeneration);

    void updateViewState(ViewState viewState);

    interface DecodeCallback {
        void decodeComplete(CodecPage codecPage, BitmapRef bitmap, Rect bitmapBounds);
    }

    void createThumbnail(File thumbnailFile, int width, int height, int pageNo, RectF region);

    boolean isPageSizeCacheable();

    int getPixelFormat();

    Bitmap.Config getBitmapConfig();

    long getMemoryLimit();

    void decreaseMemortLimit();

}
