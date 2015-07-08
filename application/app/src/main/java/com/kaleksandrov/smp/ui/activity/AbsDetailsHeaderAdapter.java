package com.kaleksandrov.smp.ui.activity;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.model.Model;

import java.util.List;

/**
 * Created by kaleksandrov on 4/29/15.
 * TODO [kaleksandrov] consider rename this class as its name should end with "Adapter" by convention.
 */
public abstract class AbsDetailsHeaderAdapter<T extends Model, V extends AbsDetailsHeaderAdapter.DetailsHeaderViewHolder> extends AbsDetailsAdapter<T, V> {

    private static final int SUMMARY_TYPE_ID = 22222;

    public interface OnHeaderClickListener {

        void onPlayAllClick(View view);

        void onShuffleAllClick(View view);
    }

    public class DetailsHeaderViewHolder extends DetailsViewHolder {
        Button mShuffleAllButton;
        Button mPlayAllButton;

        protected DetailsHeaderViewHolder(View view) {
            super(view);

            mShuffleAllButton = (Button) view.findViewById(R.id.shuffle_all);
            mPlayAllButton = (Button) view.findViewById(R.id.play_all);
        }
    }

    private int mSummaryViewLayoutId;
    private OnHeaderClickListener mOnHeaderClickListener;

    public AbsDetailsHeaderAdapter(Activity activity, List<T> items, int summaryViewId, int viewLayoutId) {
        super(activity, items, viewLayoutId);
        mSummaryViewLayoutId = summaryViewId;
    }

    @Override
    public int getItemViewType(int position) {
        switch (position) {
            case 0:
                return SUMMARY_TYPE_ID;
            default:
                return super.getItemViewType(position);
        }
    }

    @Override
    public V onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == SUMMARY_TYPE_ID) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_details_header, viewGroup, false);
            LinearLayout container = (LinearLayout) view.findViewById(R.id.container);

            LayoutInflater inflater = mActivityRef.get().getLayoutInflater();
            View newView = inflater.inflate(mSummaryViewLayoutId, container, false);

            CardView.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            newView.setLayoutParams(params);

            container.addView(newView, 0);

            return createViewHolder(view);
        } else {
            return super.onCreateViewHolder(viewGroup, viewType);
        }
    }

    @Override
    public void onBindViewHolder(V viewHolder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == SUMMARY_TYPE_ID) {
            populateSummaryView(viewHolder);

            viewHolder.mShuffleAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnHeaderClickListener != null) {
                        mOnHeaderClickListener.onShuffleAllClick(view);
                    }
                }
            });

            viewHolder.mPlayAllButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnHeaderClickListener != null) {
                        mOnHeaderClickListener.onPlayAllClick(view);
                    }
                }
            });
        } else {
            super.onBindViewHolder(viewHolder, position - 1);
        }

    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    protected abstract void populateSummaryView(V viewHolder);

    public void setOnHeaderClickListener(OnHeaderClickListener onHeaderClickListener) {
        mOnHeaderClickListener = onHeaderClickListener;
    }
}
