package com.kaleksandrov.smp.ui.img;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.util.StringUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

/**
 * Created by kaleksandrov on 5/15/15.
 */
public class CoverManager {

    private static final int DEFAULT_COVER_ID = R.drawable.no_cover;
    private static final int ERROR_COVER_ID = R.drawable.no_cover;
    private static final int NOTIFICATION_IMG_SIZE = 48; // dp

    private final Picasso mPicasso;
    private final float mDensity;


    /**
     * Holds the application context so there is no need to use WeakReference or null checking before use.
     */

    public CoverManager(Context context) {
        mPicasso = Picasso.with(context);
        mDensity = context.getResources().getDisplayMetrics().density;
    }

    private static final String LOG_TAG = CoverManager.class.getSimpleName();

    public void loadInto(String coverUri, ImageView view) {
        if (view == null) {
            Log.d(LOG_TAG, "View is null");
            return;
        }

        if (StringUtils.isEmpty(coverUri)) {
            loadDefaultInto(view);
        } else {
            Log.d(LOG_TAG, "Loading: " + coverUri);
            mPicasso.load(new File(coverUri))
                    .config(Bitmap.Config.ARGB_8888)
                    .resize(400, 400)
                    .centerCrop()
                    .placeholder(DEFAULT_COVER_ID)
                    .error(ERROR_COVER_ID)
                    .into(view);
        }
    }

    public void loadInto(String coverUri, Notification.Builder builder) {
        Log.d(LOG_TAG, "Loading: " + coverUri);

        int size = dpToPx(NOTIFICATION_IMG_SIZE);

        Bitmap img = null;
        if (StringUtils.isEmpty(coverUri)) {
            img = load(DEFAULT_COVER_ID, size);
        } else {
            img = load(coverUri, size);
        }

        builder.setLargeIcon(img);

    }

    public void loadDefaultInto(ImageView view) {
        if (view == null) {
            Log.d(LOG_TAG, "View is null");
            return;
        }

        mPicasso.load(DEFAULT_COVER_ID)
                .resize(400, 400)
                .config(Bitmap.Config.ARGB_8888)
                .centerCrop()
                .into(view);
    }

    public void cancelLoading(ImageView view) {
        if (view == null) {
            Log.d(LOG_TAG, "View is null");
            return;
        }

        mPicasso.cancelRequest(view);
        loadDefaultInto(view);
    }

    /* Private methods */

    private int dpToPx(int dp) {
        return (int) (dp * mDensity + 0.5);
    }

    private Bitmap load(int resourceId, int size) {
        Log.d(LOG_TAG, "Loading: " + resourceId);

        Bitmap result = null;
        try {
            result = mPicasso.load(resourceId)
                    .resize(size, size)
                    .config(Bitmap.Config.ARGB_8888)
                    .get();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Cannot load image", e);
        }

        return result;
    }

    private Bitmap load(String coverUri, int size) {
        if (StringUtils.isEmpty(coverUri)) {
            return null;
        }

        Log.d(LOG_TAG, "Loading: " + coverUri);

        Bitmap result = null;
        try {
            result = mPicasso.load(new File(coverUri))
                    .config(Bitmap.Config.ARGB_8888)
                    .resize(size, size)
                    .get();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Cannot load image", e);
        }

        return result;
    }
}
