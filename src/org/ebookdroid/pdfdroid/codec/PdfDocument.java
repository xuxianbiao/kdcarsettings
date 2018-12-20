package org.ebookdroid.pdfdroid.codec;

import org.ebookdroid.core.OutlineLink;
import org.ebookdroid.core.PageLink;
import org.ebookdroid.core.codec.AbstractCodecDocument;
import org.ebookdroid.core.codec.CodecPage;
import org.ebookdroid.core.codec.CodecPageInfo;


import java.util.ArrayList;
import java.util.List;

public class PdfDocument extends AbstractCodecDocument {

    //TODO: Must be configurable
    private static final int STOREMEMORY = 64<<20;

    PdfDocument(final PdfContext context, final String fname, final String pwd) {
        super(context, open(STOREMEMORY, fname, pwd));
    }

    @Override
    public List<OutlineLink> getOutline() {
        final PdfOutline ou = new PdfOutline();
        return ou.getOutline(documentHandle);
    }

    @Override
    public ArrayList<PageLink> getPageLinks(final int pageNumber) {
        return getPageLinks(documentHandle, pageNumber);
    }

    @Override
    public CodecPage getPage(final int pageNumber) {
        return PdfPage.createPage(documentHandle, pageNumber + 1);
    }

    @Override
    public int getPageCount() {
        return getPageCount(documentHandle);
    }

    @Override
    public CodecPageInfo getPageInfo(final int pageNumber) {
        final CodecPageInfo info = new CodecPageInfo();
        final int res = getPageInfo(documentHandle, pageNumber + 1, info);
        if (res == -1) {
            return null;
        } else {
            // Check rotation
            info.rotation = (360 + info.rotation) %360;
            info.width = (PdfContext.getWidthInPixels(info.width));
            info.height = (PdfContext.getHeightInPixels(info.height));
            return info;
        }
    }

    @Override
    protected void freeDocument() {
        free(documentHandle);
    }

    private native static int getPageInfo(long docHandle, int pageNumber, CodecPageInfo cpi);

    private native static ArrayList<PageLink> getPageLinks(long docHandle, int pageNumber);

    private static native long open(int storememory, String fname, String pwd);

    private static native void free(long handle);

    private static native int getPageCount(long handle);

}
