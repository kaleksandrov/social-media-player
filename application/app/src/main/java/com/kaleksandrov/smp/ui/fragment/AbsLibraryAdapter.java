package com.kaleksandrov.smp.ui.fragment;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaleksandrov.smp.R;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by kaleksandrov on 4/29/15.
 */
public abstract class AbsLibraryAdapter<T, V extends AbsLibraryAdapter.ViewHolder> extends RecyclerView.Adapter<V> {

    public interface OnItemClickListener {
        void onItemClick(View view, Object item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ViewHolder(View view) {
            super(view);
            mNameView = (TextView) view.findViewById(R.id.name);
            mCoverView = (ImageView) view.findViewById(R.id.avatar);
            view.setTag(this);
        }

        protected int mId;
        protected TextView mNameView;
        protected ImageView mCoverView;

        public int getId() {
            return mId;
        }
    }

    private List<T> mItems;
    protected WeakReference<Activity> mActivityRef;
    private int mViewLayoutId;
    private OnItemClickListener mOnItemClickListener;

    public AbsLibraryAdapter(Activity activity, List<T> items, int viewLayoutId) {
        mItems = items;
        mActivityRef = new WeakReference<>(activity);
        mViewLayoutId = viewLayoutId;
    }

    @Override
    public V onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(mViewLayoutId, viewGroup, false);

        return createViewHolder(v);
    }

    @Override
    public void onBindViewHolder(V viewHolder, int position) {
        final T item = mItems.get(position);
        populateView(viewHolder, item);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, item);
                }
            }
        });
    }

    protected abstract void populateView(V viewHolder, T item);

    protected abstract V createViewHolder(View view);

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
