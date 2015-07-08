package com.kaleksandrov.smp.ui.activity;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaleksandrov.smp.model.Model;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by kaleksandrov on 4/29/15.
 */
public abstract class AbsDetailsAdapter<T extends Model, V extends AbsDetailsAdapter.DetailsViewHolder> extends RecyclerView.Adapter<V> {

    public interface OnItemClickListener<T extends Model> {
        void onItemClick(View view, T item);

        void onItemLongClick(View view, T item);
    }

    private interface SelectionHolder {
        void select(int index);

        boolean isSelected(int index);
    }

    private class SingleSelectionHolder implements SelectionHolder {

        public SingleSelectionHolder() {
            mSelectedIndex = -1;
        }

        private int mSelectedIndex;

        @Override
        public void select(int index) {
            int oldSelectedIndex = mSelectedIndex;
            mSelectedIndex = index;
            notifyItemChanged(oldSelectedIndex);
            notifyItemChanged(mSelectedIndex);
        }

        @Override
        public boolean isSelected(int index) {
            return index == mSelectedIndex;
        }
    }

    public class DetailsViewHolder extends RecyclerView.ViewHolder {

        protected int mId;

        protected DetailsViewHolder(View view) {
            super(view);
            view.setTag(this);
        }

        public int getId() {
            return mId;
        }
    }

    private List<T> mItems;
    private int mViewLayoutId;
    private OnItemClickListener mOnItemClickListener;
    protected WeakReference<Activity> mActivityRef;
    private SelectionHolder mSelectionHolder;

    private static final String DURATION_FORMAT = "mm:ss";
    private static final SimpleDateFormat sFormatter = new SimpleDateFormat(DURATION_FORMAT);

    public AbsDetailsAdapter(Activity activity, List<T> items, int viewLayoutId) {
        mItems = items;
        mViewLayoutId = viewLayoutId;
        mActivityRef = new WeakReference<Activity>(activity);
        mSelectionHolder = new SingleSelectionHolder();
    }

    @Override
    public V onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(mViewLayoutId, viewGroup, false);

        V viewHolder = createViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(V viewHolder, final int position) {
        final T item = mItems.get(position);
        populateView(viewHolder, item);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectionHolder.select(position);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, item);
                }
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemLongClick(view, item);
                }
                return true;
            }
        });

        if (mSelectionHolder.isSelected(position)) {
            onItemSelected(viewHolder, position);
        } else {
            onItemUnselected(viewHolder, position);
        }
    }

    protected void onItemSelected(V viewHolder, int position) {
        // nothing
    }

    protected void onItemUnselected(V viewHolder, int position) {
        // nothing
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

    protected static String formatDuration(int duration) {
        return sFormatter.format(new Date(duration));
    }

    protected int getColor(int colorId) {
        return mActivityRef.get().getResources().getColor(colorId);
    }

    public void setSelectedItem(int position) {
        mSelectionHolder.select(position);
    }
}
