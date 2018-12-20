package org.ebookdroid.core.settings;

import org.ebookdroid.core.Activities;
import org.ebookdroid.core.DecodeMode;
import org.ebookdroid.core.DocumentViewMode;
import org.ebookdroid.core.PageAlign;
import org.ebookdroid.core.RotationType;
import org.ebookdroid.core.curl.PageAnimationType;
import org.ebookdroid.core.settings.books.BookSettings;
import org.ebookdroid.core.utils.AndroidVersion;
import org.ebookdroid.core.utils.FileExtensionFilter;
import org.ebookdroid.utils.LengthUtils;
import org.ebookdroid.utils.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class AppSettings {

    private final SharedPreferences prefs;

    private Boolean tapScroll;

    private Integer tapSize;

    private DocumentViewMode viewMode;

    private Integer pagesInMemory;

    private Boolean showAnimIcon;

    private Boolean nightMode;

    private PageAlign pageAlign;

    private Boolean fullScreen;

    private RotationType rotation;

    private Boolean showTitle;

    private Integer scrollHeight;

    private PageAnimationType animationType;

    private Boolean splitPages;

    private Boolean pageInTitle;

    private Integer brightness;

    private Boolean brightnessnightmodeonly;

    private Boolean keepscreenon;

    private Set<String> autoScanDirs;

    private Boolean loadRecent;

    private Integer maxImageSize;

    private Boolean zoomByDoubleTap;

    private DecodeMode decodeMode;

    private Boolean useBookcase;

    private Integer djvuRenderingMode;

    private Boolean cropPages;

    private String touchProfiles;

    AppSettings(final Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getTouchProfiles() {
        if (touchProfiles == null) {
            touchProfiles = prefs.getString("touchprofiles", "");
        }
        return touchProfiles;
    }

    public void updateTouchProfiles(String profiles) {
        touchProfiles = profiles;
        final Editor edit = prefs.edit();
        edit.putString("touchprofiles", touchProfiles);
        edit.commit();
    }

    public boolean isLoadRecentBook() {
        if (loadRecent == null) {
            loadRecent = prefs.getBoolean("loadrecent", false);
        }
        return loadRecent;
    }

    public Set<String> getAutoScanDirs() {
        if (autoScanDirs == null) {
            autoScanDirs = StringUtils.split(File.pathSeparator, prefs.getString("brautoscandir", "/sdcard"));
        }
        return autoScanDirs;
    }

    public void setAutoScanDirs(final Set<String> dirs) {
        autoScanDirs = dirs;
        final Editor edit = prefs.edit();
        edit.putString("brautoscandir", StringUtils.merge(File.pathSeparator, autoScanDirs));
        edit.commit();
    }

    public void changeAutoScanDirs(final String dir, final boolean add) {
        final Set<String> dirs = getAutoScanDirs();
        if (add && dirs.add(dir) || dirs.remove(dir)) {
            setAutoScanDirs(dirs);
        }
    }

    public FileExtensionFilter getAllowedFileTypes() {
        return getAllowedFileTypes(Activities.getAllExtensions());
    }

    public FileExtensionFilter getAllowedFileTypes(final Set<String> fileTypes) {
        final Set<String> res = new HashSet<String>();
        for (final String ext : fileTypes) {
            if (isFileTypeAllowed(ext)) {
                res.add(ext);
            }
        }
        return new FileExtensionFilter(res);
    }

    public boolean isFileTypeAllowed(final String ext) {
        return prefs.getBoolean("brfiletype" + ext, true);
    }

    public int getBrightness() {
        if (isBrightnessInNightModeOnly() && !getNightMode()) {
            return 100;
        }
        if (brightness == null) {
            brightness = getIntValue("brightness", 100);
        }
        return brightness;
    }

    public boolean getShowAnimIcon() {
        if (showAnimIcon == null) {
            showAnimIcon = prefs.getBoolean("showanimicon", true);
        }
        return showAnimIcon;
    }

    public boolean getNightMode() {
        if (nightMode == null) {
            nightMode = prefs.getBoolean("nightmode", false);
        }
        return nightMode;
    }

    public boolean isKeepScreenOn() {
        if (keepscreenon == null) {
            keepscreenon = prefs.getBoolean("keepscreenon", true);
        }
        return keepscreenon;
    }

    public boolean isBrightnessInNightModeOnly() {
        if (brightnessnightmodeonly == null) {
            brightnessnightmodeonly = prefs.getBoolean("brightnessnightmodeonly", false);
        }
        return brightnessnightmodeonly;
    }

    public RotationType getRotation() {
        if (rotation == null) {
            final String rotationStr = prefs.getString("rotation", RotationType.AUTOMATIC.getResValue());
            rotation = RotationType.getByResValue(rotationStr);
            if (rotation == null) {
                rotation = RotationType.AUTOMATIC;
            }
        }
        return rotation;
    }

    public boolean getFullScreen() {
        if (fullScreen == null) {
            fullScreen = prefs.getBoolean("fullscreen", false);
        }
        return fullScreen;
    }

    public boolean getShowTitle() {
        if (showTitle == null) {
            showTitle = prefs.getBoolean("title", true);
        }
        return showTitle;
    }

    public boolean getPageInTitle() {
        if (pageInTitle == null) {
            pageInTitle = prefs.getBoolean("pageintitle", true);
        }
        return pageInTitle;
    }

    public boolean getTapScroll() {
        if (tapScroll == null) {
            tapScroll = prefs.getBoolean("tapscroll", false);
        }
        return tapScroll;
    }

    public int getTapSize() {
        if (tapSize == null) {
            tapSize = getIntValue("tapsize", 10);
        }
        return tapSize.intValue();
    }

    public int getScrollHeight() {
        if (scrollHeight == null) {
            scrollHeight = getIntValue("scrollheight", 50);
        }
        return scrollHeight.intValue();
    }

    public int getPagesInMemory() {
        if (pagesInMemory == null) {
            pagesInMemory = getIntValue("pagesinmemory", 2);
        }
        return pagesInMemory.intValue();
    }

    public DecodeMode getDecodeMode() {
        if (decodeMode == null) {
            String val = prefs.getString("decodemode", null);
            if (LengthUtils.isEmpty(val)) {
                if (prefs.getBoolean("nativeresolution", false)) {
                    decodeMode = DecodeMode.NATIVE_RESOLUTION;
                } else if (prefs.getBoolean("slicelimit", false)) {
                    decodeMode = DecodeMode.LOW_MEMORY;
                } else {
                    decodeMode = DecodeMode.NORMAL;
                }
            } else {
                decodeMode = DecodeMode.getByResValue(val);
            }
        }
        return decodeMode;
    }

    public int getMaxImageSize() {
        if (maxImageSize == null) {
            int value = Math.max(64, getIntValue("maximagesize", 256));
            maxImageSize = value * 1024;
        }
        return maxImageSize;
    }

    public boolean getZoomByDoubleTap() {
        if (zoomByDoubleTap == null) {
            zoomByDoubleTap = prefs.getBoolean("zoomdoubletap", false);
        }
        return zoomByDoubleTap;
    }

    public boolean getUseBookcase() {
        if (useBookcase == null) {
            useBookcase = prefs.getBoolean("usebookcase", true);
        }
        return !AndroidVersion.is1x && useBookcase;
    }

    boolean getSplitPages() {
        if (splitPages == null) {
            splitPages = prefs.getBoolean("splitpages", false);
        }
        return splitPages;
    }

    public DocumentViewMode getViewMode() {
        if (viewMode == null) {
            viewMode = DocumentViewMode.getByResValue(prefs.getString("viewmode", null));
            if (viewMode == null) {
                boolean singlePage = prefs.getBoolean("singlepage", false);
                viewMode = singlePage ? DocumentViewMode.SINGLE_PAGE : DocumentViewMode.VERTICALL_SCROLL;
            }
        }
        return viewMode;
    }

    PageAlign getPageAlign() {
        if (pageAlign == null) {
            final String align = prefs.getString("align", PageAlign.AUTO.getResValue());
            pageAlign = PageAlign.getByResValue(align);
            if (pageAlign == null) {
                pageAlign = PageAlign.AUTO;
            }
        }
        return pageAlign;
    }

    PageAnimationType getAnimationType() {
        if (animationType == null) {
            animationType = PageAnimationType.get(prefs.getString("animationType", null));
        }
        return animationType;
    }

    public int getDjvuRenderingMode() {
        if (djvuRenderingMode == null) {
            djvuRenderingMode = getIntValue("djvu_rendering_mode", 0);
        }
        return djvuRenderingMode;
    }

    boolean getCropPages() {
        if (cropPages == null) {
            cropPages = prefs.getBoolean("croppages", false);
        }
        return cropPages;
    }

    void clearPseudoBookSettings() {
        final Editor editor = prefs.edit();
        editor.remove("book");
        editor.remove("book_splitpages");
        editor.remove("book_singlepage");
        editor.remove("book_align");
        editor.remove("book_animationType");
        editor.remove("book_croppages");
        editor.commit();
    }

    void updatePseudoBookSettings(final BookSettings bs) {
        final Editor editor = prefs.edit();
        editor.putString("book", bs.fileName);
        editor.putBoolean("book_splitpages", bs.splitPages);
        editor.putString("book_viewmode", bs.viewMode.getResValue());
        editor.putString("book_align", bs.pageAlign.getResValue());
        editor.putString("book_animationType", bs.animationType.getResValue());
        editor.putBoolean("book_croppages", bs.cropPages);
        editor.commit();
    }

    void fillBookSettings(final BookSettings bs) {
        bs.splitPages = prefs.getBoolean("book_splitpages", getSplitPages());

        bs.viewMode = DocumentViewMode.getByResValue(prefs.getString("book_viewmode", getViewMode().getResValue()));
        if (bs.viewMode == null) {
            bs.viewMode = DocumentViewMode.VERTICALL_SCROLL;
        }

        bs.pageAlign = PageAlign.getByResValue(prefs.getString("book_align", getPageAlign().getResValue()));
        if (bs.pageAlign == null) {
            bs.pageAlign = PageAlign.AUTO;
        }
        bs.animationType = PageAnimationType.get(prefs
                .getString("book_animationType", getAnimationType().getResValue()));
        if (bs.animationType == null) {
            bs.animationType = PageAnimationType.NONE;
        }
        bs.cropPages = prefs.getBoolean("book_croppages", getCropPages());

    }

    private int getIntValue(final String key, final int defaultValue) {
        final String str = prefs.getString(key, "" + defaultValue);
        int value = defaultValue;
        try {
            value = Integer.parseInt(str);
        } catch (final NumberFormatException e) {
        }
        return value;
    }

    public static class Diff {

        private static final int D_NightMode = 0x0001 << 0;
        private static final int D_Rotation = 0x0001 << 1;
        private static final int D_FullScreen = 0x0001 << 2;
        private static final int D_ShowTitle = 0x0001 << 3;
        private static final int D_PageInTitle = 0x0001 << 4;
        private static final int D_TapScroll = 0x0001 << 5;
        private static final int D_TapSize = 0x0001 << 6;
        private static final int D_ScrollHeight = 0x0001 << 7;
        private static final int D_PagesInMemory = 0x0001 << 8;
        private static final int D_DecodeMode = 0x0001 << 9;
        private static final int D_Brightness = 0x0001 << 10;
        private static final int D_BrightnessInNightMode = 0x0001 << 11;
        private static final int D_KeepScreenOn = 0x0001 << 12;
        private static final int D_LoadRecent = 0x0001 << 13;
        private static final int D_MaxImageSize = 0x0001 << 14;
        private static final int D_UseBookcase = 0x0001 << 15;
        private static final int D_DjvuRenderingMode = 0x0001 << 16;
        private static final int D_AutoScanDirs = 0x0001 << 17;
        private static final int D_AllowedFileTypes = 0x0001 << 18;

        private int mask;
        private final boolean firstTime;

        public Diff(final AppSettings olds, final AppSettings news) {
            firstTime = olds == null;
            if (news != null) {
                if (firstTime || olds.getNightMode() != news.getNightMode()) {
                    mask |= D_NightMode;
                }
                if (firstTime || olds.getRotation() != news.getRotation()) {
                    mask |= D_Rotation;
                }
                if (firstTime || olds.getFullScreen() != news.getFullScreen()) {
                    mask |= D_FullScreen;
                }
                if (firstTime || olds.getShowTitle() != news.getShowTitle()) {
                    mask |= D_ShowTitle;
                }
                if (firstTime || olds.getPageInTitle() != news.getPageInTitle()) {
                    mask |= D_PageInTitle;
                }
                if (firstTime || olds.getTapScroll() != news.getTapScroll()) {
                    mask |= D_TapScroll;
                }
                if (firstTime || olds.getTapSize() != news.getTapSize()) {
                    mask |= D_TapSize;
                }
                if (firstTime || olds.getScrollHeight() != news.getScrollHeight()) {
                    mask |= D_ScrollHeight;
                }
                if (firstTime || olds.getPagesInMemory() != news.getPagesInMemory()) {
                    mask |= D_PagesInMemory;
                }
                if (firstTime || olds.getDecodeMode() != news.getDecodeMode()) {
                    mask |= D_DecodeMode;
                }
                if (firstTime || olds.getBrightness() != news.getBrightness()) {
                    mask |= D_Brightness;
                }
                if (firstTime || olds.isBrightnessInNightModeOnly() != news.isBrightnessInNightModeOnly()) {
                    mask |= D_BrightnessInNightMode;
                }
                if (firstTime || olds.isKeepScreenOn() != news.isKeepScreenOn()) {
                    mask |= D_KeepScreenOn;
                }
                if (firstTime || olds.isLoadRecentBook() != news.isLoadRecentBook()) {
                    mask |= D_LoadRecent;
                }
                if (firstTime || olds.getMaxImageSize() != news.getMaxImageSize()) {
                    mask |= D_MaxImageSize;
                }
                if (firstTime || olds.getUseBookcase() != news.getUseBookcase()) {
                    mask |= D_UseBookcase;
                }
                if (firstTime || olds.getDjvuRenderingMode() != news.getDjvuRenderingMode()) {
                    mask |= D_DjvuRenderingMode;
                }
                if (firstTime || olds.getAutoScanDirs().equals(news.getAutoScanDirs())) {
                    mask |= D_AutoScanDirs;
                }
                if (firstTime || olds.getAllowedFileTypes().equals(news.getAllowedFileTypes())) {
                    mask |= D_AllowedFileTypes;
                }
            }
        }

        public boolean isFirstTime() {
            return firstTime;
        }

        public boolean isNightModeChanged() {
            return 0 != (mask & D_NightMode);
        }

        public boolean isRotationChanged() {
            return 0 != (mask & D_Rotation);
        }

        public boolean isFullScreenChanged() {
            return 0 != (mask & D_FullScreen);
        }

        public boolean isShowTitleChanged() {
            return 0 != (mask & D_ShowTitle);
        }

        public boolean isPageInTitleChanged() {
            return 0 != (mask & D_PageInTitle);
        }

        public boolean isTapScrollChanged() {
            return 0 != (mask & D_TapScroll);
        }

        public boolean isTapSizeChanged() {
            return 0 != (mask & D_TapSize);
        }

        public boolean isScrollHeightChanged() {
            return 0 != (mask & D_ScrollHeight);
        }

        public boolean isPagesInMemoryChanged() {
            return 0 != (mask & D_PagesInMemory);
        }

        public boolean isDecodeModeChanged() {
            return 0 != (mask & D_DecodeMode);
        }

        public boolean isBrightnessChanged() {
            return 0 != (mask & D_Brightness);
        }

        public boolean isBrightnessInNightModeChanged() {
            return 0 != (mask & D_BrightnessInNightMode);
        }

        public boolean isKeepScreenOnChanged() {
            return 0 != (mask & D_KeepScreenOn);
        }

        public boolean isLoadRecentChanged() {
            return 0 != (mask & D_LoadRecent);
        }

        public boolean isMaxImageSizeChanged() {
            return 0 != (mask & D_MaxImageSize);
        }

        public boolean isUseBookcaseChanged() {
            return 0 != (mask & D_UseBookcase);
        }

        public boolean isDjvuRenderingModeChanged() {
            return 0 != (mask & D_DjvuRenderingMode);
        }

        public boolean isAutoScanDirsChanged() {
            return 0 != (mask & D_AutoScanDirs);
        }

        public boolean isAllowedFileTypesChanged() {
            return 0 != (mask & D_AllowedFileTypes);
        }
    }

}
