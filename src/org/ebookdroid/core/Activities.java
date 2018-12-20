package org.ebookdroid.core;

import org.ebookdroid.pdfdroid.PdfViewerActivity;

import android.app.Activity;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum Activities {

    PDF(PdfViewerActivity.class, "pdf");

    private final static Map<String, Class<? extends Activity>> extensionToActivity;

    static {
        extensionToActivity = new HashMap<String, Class<? extends Activity>>();
        for (final Activities a : values()) {
            final Class<? extends Activity> ac = a.getActivityClass();
            for (final String ext : a.getExtensions()) {
                extensionToActivity.put(ext.toLowerCase(), ac);
            }
        }
    }

    private final Class<? extends Activity> activityClass;

    private final String[] extensions;

    private Activities(final Class<? extends Activity> activityClass, final String... extensions) {
        this.activityClass = activityClass;
        this.extensions = extensions;
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }

    public String[] getExtensions() {
        return extensions;
    }

    public static Set<String> getAllExtensions() {
        return extensionToActivity.keySet();
    }

    public static Class<? extends Activity> getByUri(final Uri uri) {
        final String uriString = uri.toString().toLowerCase();
        for (final String ext : extensionToActivity.keySet()) {
            if (uriString.endsWith("." + ext)) {
                return extensionToActivity.get(ext);
            }
        }
        return null;
    }

    public static Class<? extends Activity> getByExtension(final String ext) {
        return extensionToActivity.get(ext.toLowerCase());
    }
}
