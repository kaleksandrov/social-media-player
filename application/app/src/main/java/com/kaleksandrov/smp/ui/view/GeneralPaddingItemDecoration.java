package com.kaleksandrov.smp.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by kaleksandrov on 5/14/15.
 */
public class GeneralPaddingItemDecoration extends RecyclerView.ItemDecoration {

    private static final int PADDING_DP = 8;
    protected final WeakReference<Context> mContextRef;

    public GeneralPaddingItemDecoration(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        Context context = mContextRef.get();
        if (context != null) {
            //TODO [kaleksandrov] Move this to utility method
            float px = PADDING_DP * context.getResources().getDisplayMetrics().density;
            outRect.left += px;
            outRect.right += px;
        }
    }

}
