package com.kaleksandrov.smp.ui.fragment;

import android.app.Activity;
import android.view.View;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Album;
import com.kaleksandrov.smp.ui.img.CoverManager;

import java.util.List;

/**
 * Created by kaleksandrov on 4/29/15.
 */
public class AlbumsAdapter extends AbsLibraryAdapter<Album, AbsLibraryAdapter.ViewHolder> {

    private CoverManager mCoverManager;

    public AlbumsAdapter(Activity activity, List<Album> albums) {
        super(activity, albums, R.layout.card_album);

        FairPlayerApplication app = (FairPlayerApplication) activity.getApplication();
        mCoverManager = app.getCoverManager();
    }

    @Override
    protected void populateView(AbsLibraryAdapter.ViewHolder viewHolder, Album album) {
        viewHolder.mId = album.getId();
        viewHolder.mNameView.setText(album.getName());
        mCoverManager.loadInto(album.getCoverPath(), viewHolder.mCoverView);
    }

    @Override
    public void onViewRecycled(AbsLibraryAdapter.ViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
        mCoverManager.cancelLoading(viewHolder.mCoverView);
    }

    @Override
    protected AbsLibraryAdapter.ViewHolder createViewHolder(View view) {
        return new AbsLibraryAdapter.ViewHolder(view);
    }
}
