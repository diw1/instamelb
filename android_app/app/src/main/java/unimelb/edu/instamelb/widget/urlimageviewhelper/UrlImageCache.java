package unimelb.edu.instamelb.widget.urlimageviewhelper;

import android.graphics.drawable.Drawable;

public final class UrlImageCache extends SoftReferenceHashTable<String, Drawable> {
    private static final UrlImageCache mInstance = new UrlImageCache();

    public static UrlImageCache getInstance() {
        return mInstance;
    }

    private UrlImageCache() {
    }
}
