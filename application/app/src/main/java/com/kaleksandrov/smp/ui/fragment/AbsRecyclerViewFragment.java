package com.kaleksandrov.smp.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.ui.view.GeneralPaddingItemDecoration;

/**
 * Created by kaleksandrov on 4/29/15.
 */
public abstract class AbsRecyclerViewFragment extends Fragment {

    protected RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    public AbsRecyclerViewFragment() {
        // nothing
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_generic_recycle_list, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        Context context = getActivity();
        mRecyclerView.setLayoutManager(getLayoutManager(context));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new GeneralPaddingItemDecoration(context));
        mRecyclerView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        return rootView;
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        mAdapter = adapter;
    }

    protected abstract RecyclerView.LayoutManager getLayoutManager(Context context);
}
