package com.kaleksandrov.smp.ui.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by kaleksandrov on 5/14/15.
 */
public class DetailsPaddingItemDecoration extends GeneralPaddingItemDecoration {

    public DetailsPaddingItemDecoration(Context context) {
        super(context);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        Context context = mContextRef.get();
        if (context != null) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top += 32 * context.getResources().getDisplayMetrics().density;
            }
        }
    }

}
