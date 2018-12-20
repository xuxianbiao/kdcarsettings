package org.ebookdroid.core.models;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ebookdroid.core.DecodeService;
import org.ebookdroid.core.IDocumentView;
import org.ebookdroid.core.IViewerActivity;
import org.ebookdroid.core.Page;
import org.ebookdroid.core.PageIndex;
import org.ebookdroid.core.PageType;
import org.ebookdroid.core.bitmaps.BitmapManager;
import org.ebookdroid.core.bitmaps.Bitmaps;
import org.ebookdroid.core.cache.CacheManager;
import org.ebookdroid.core.codec.CodecPageInfo;
import org.ebookdroid.core.settings.SettingsManager;
import org.ebookdroid.core.settings.books.BookSettings;
import org.ebookdroid.utils.LengthUtils;

import android.graphics.RectF;

import com.kandi.settings.R;

public class DocumentModel extends CurrentPageModel {

    private static final boolean CACHE_ENABLED = false;//页面缓存标志：true为缓存，false为不缓存

    private static final Page[] EMPTY_PAGES = {};

    private DecodeService decodeService;

    private Page[] pages = EMPTY_PAGES;

    public DocumentModel(final DecodeService decodeService) {
        this.decodeService = decodeService;
    }

    public Page[] getPages() {
        return pages;
    }

    public Iterable<Page> getPages(final int start) {
        return new PageIterator(start, pages.length);
    }

    public Iterable<Page> getPages(final int start, final int end) {
        return new PageIterator(start, Math.min(end, pages.length));
    }

    public int getPageCount() {
        return LengthUtils.length(pages);
    }

    public DecodeService getDecodeService() {
        return decodeService;
    }

    public void recycle() {
        decodeService.recycle();
        decodeService = null;
        recyclePages();
    }

    private void recyclePages() {
        if (LengthUtils.isNotEmpty(pages)) {
            final List<Bitmaps> bitmapsToRecycle = new ArrayList<Bitmaps>();
            for (final Page page : pages) {
                page.recycle(bitmapsToRecycle);
            }
            BitmapManager.release(bitmapsToRecycle);
            BitmapManager.release();
        }
        pages = EMPTY_PAGES;
    }

    public Page getPageObject(final int viewIndex) {
        return pages != null && 0 <= viewIndex && viewIndex < pages.length ? pages[viewIndex] : null;
    }

    /**
     * Gets the current page object.
     * 
     * @return the current page object
     */
    public Page getCurrentPageObject() {
        return getPageObject(this.currentIndex.viewIndex);
    }

    /**
     * Gets the next page object.
     * 
     * @return the next page object
     */
    public Page getNextPageObject() {
        return getPageObject(this.currentIndex.viewIndex + 1);
    }

    /**
     * Gets the prev page object.
     * 
     * @return the prev page object
     */
    public Page getPrevPageObject() {
        return getPageObject(this.currentIndex.viewIndex - 1);
    }

    /**
     * Gets the last page object.
     * 
     * @return the last page object
     */
    public Page getLastPageObject() {
        return getPageObject(pages.length - 1);
    }

    public void setCurrentPageByFirstVisible(final int firstVisiblePage) {
        final Page page = getPageObject(firstVisiblePage);
        if (page != null) {
            setCurrentPageIndex(page.index);
        }
    }

    public void initPages(final IViewerActivity base, final IViewerActivity.IBookLoadTask task) {
        recyclePages();

        final BookSettings bs = SettingsManager.getBookSettings();

        if (base == null || bs == null) {
            return;
        }

        final IDocumentView view = base.getView();

        final CodecPageInfo defCpi = new CodecPageInfo();
        defCpi.width = (view.getWidth());
        defCpi.height = (view.getHeight());

        int viewIndex = 0;

        final long start = System.currentTimeMillis();
        try {
            final ArrayList<Page> list = new ArrayList<Page>();
            final CodecPageInfo[] infos = retrievePagesInfo(base, bs, task);

            for (int docIndex = 0; docIndex < infos.length; docIndex++) {
                if (!bs.splitPages || infos[docIndex] == null || (infos[docIndex].width < infos[docIndex].height)) {
                    final Page page = new Page(base, new PageIndex(docIndex, viewIndex++), PageType.FULL_PAGE,
                            infos[docIndex] != null ? infos[docIndex] : defCpi);
                    list.add(page);
                } else {
                    final Page page1 = new Page(base, new PageIndex(docIndex, viewIndex++), PageType.LEFT_PAGE,
                            infos[docIndex]);
                    list.add(page1);
                    final Page page2 = new Page(base, new PageIndex(docIndex, viewIndex++), PageType.RIGHT_PAGE,
                            infos[docIndex]);
                    list.add(page2);
                }
            }
            pages = list.toArray(new Page[list.size()]);
            if (pages.length > 0) {
                createBookThumbnail(bs, pages[0], false);
            }
        } finally {
            LCTX.d("Loading page info: " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    public void createBookThumbnail(final BookSettings bs, Page page, boolean override) {
        final File thumbnailFile = CacheManager.getThumbnailFile(bs.fileName);
        if (!override && thumbnailFile.exists()) {
            return;
        }

        int width = 200, height = 200;
        RectF bounds = page.getBounds(1.0f);
        float pageWidth = bounds.width();
        float pageHeight = bounds.height();

        if (pageHeight > pageWidth) {
            width = (int) (200 * pageWidth / pageHeight);
        } else {
            height = (int) (200 * pageHeight / pageWidth);
        }

        decodeService.createThumbnail(thumbnailFile, width, height, page.index.docIndex, page.type.getInitialRect());
    }

    private CodecPageInfo[] retrievePagesInfo(final IViewerActivity base, final BookSettings bs,
            IViewerActivity.IBookLoadTask task) {
        final File pagesFile = CacheManager.getPageFile(bs.fileName);

        if (CACHE_ENABLED) {
            if (pagesFile.exists()) {
                final CodecPageInfo[] infos = loadPagesInfo(pagesFile);
                if (infos != null) {
                    boolean nullInfoFound = false;
                    for (CodecPageInfo info : infos) {
                        if (info == null) {
                            nullInfoFound = true;
                            break;
                        }
                    }
                    if (!nullInfoFound) {
                        return infos;
                    }
                }
            }
        }

        final CodecPageInfo[] infos = new CodecPageInfo[getDecodeService().getPageCount()];
        for (int i = 0; i < infos.length; i++) {
            if (task != null) {
                task.setProgressDialogMessage(R.string.msg_getting_page_size, (i + 1), infos.length);
            }
            infos[i] = getDecodeService().getPageInfo(i);
        }

        if (CACHE_ENABLED) {
            storePagesInfo(pagesFile, infos);
        }
        return infos;
    }

    private CodecPageInfo[] loadPagesInfo(final File pagesFile) {
        try {
            final DataInputStream in = new DataInputStream(new FileInputStream(pagesFile));
            try {
                final int pages = in.readInt();
                final CodecPageInfo[] infos = new CodecPageInfo[pages];
                for (int i = 0; i < infos.length; i++) {
                    final CodecPageInfo cpi = new CodecPageInfo();
                    cpi.width = (in.readInt());
                    cpi.height = (in.readInt());
                    if (cpi.width != -1 && cpi.height != -1) {
                        infos[i] = cpi;
                    }
                }
                return infos;
            } catch (final EOFException ex) {
                LCTX.e("Loading pages cache failed: " + ex.getMessage());
            } catch (final IOException ex) {
                LCTX.e("Loading pages cache failed: " + ex.getMessage());
            } finally {
                try {
                    in.close();
                } catch (final IOException ex) {
                }
            }
        } catch (final FileNotFoundException ex) {
            LCTX.e("Loading pages cache failed: " + ex.getMessage());
        }
        return null;
    }

    private void storePagesInfo(final File pagesFile, final CodecPageInfo[] infos) {
        if (!decodeService.isPageSizeCacheable()) {
            return;
        }
        try {
            final DataOutputStream out = new DataOutputStream(new FileOutputStream(pagesFile));
            try {
                out.writeInt(infos.length);
                for (int i = 0; i < infos.length; i++) {
                    if (infos[i] != null) {
                        out.writeInt(infos[i].width);
                        out.writeInt(infos[i].height);
                    } else {
                        out.writeInt(-1);
                        out.writeInt(-1);
                    }
                }
            } catch (final IOException ex) {
                LCTX.e("Saving pages cache failed: " + ex.getMessage());
            } finally {
                try {
                    out.close();
                } catch (final IOException ex) {
                }
            }
        } catch (final IOException ex) {
            LCTX.e("Saving pages cache failed: " + ex.getMessage());
        }
    }

    private final class PageIterator implements Iterable<Page>, Iterator<Page> {

        private final int end;
        private int index;

        private PageIterator(final int start, final int end) {
            this.index = start;
            this.end = end;
        }

        @Override
        public boolean hasNext() {
            return 0 <= index && index < end;
        }

        @Override
        public Page next() {
            return hasNext() ? pages[index++] : null;
        }

        @Override
        public void remove() {
        }

        @Override
        public Iterator<Page> iterator() {
            return this;
        }
    }
}
