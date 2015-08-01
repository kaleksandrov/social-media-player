package com.kaleksandrov.smp.ui.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.ui.img.CoverManager;

import java.util.List;

/**
 * Created by kaleksandrov on 5/3/15.
 */
public class PlaylistAdapter extends AbsDetailsAdapter<Song, PlaylistAdapter.SongViewHolder> {

    private CoverManager mCoverManager;

    public PlaylistAdapter(Activity activity, List<Song> songs) {
        super(activity, songs, R.layout.card_song4);

        FairPlayerApplication app = (FairPlayerApplication) activity.getApplication();
        mCoverManager = app.getCoverManager();
    }

    @Override
    protected void populateView(SongViewHolder viewHolder, Song song) {
        viewHolder.mId = song.getId();
        viewHolder.mTitleView.setText(song.getName());
        viewHolder.mDurationView.setText(formatDuration(song.getDuration()));
        mCoverManager.loadInto(song.getAlbum().getCoverPath(), viewHolder.mCoverView);
    }

    @Override
    public void onViewRecycled(SongViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
        mCoverManager.cancelLoading(viewHolder.mCoverView);
    }

    @Override
    protected SongViewHolder createViewHolder(View view) {
        return new SongViewHolder(view);
    }

    @Override
    protected void onItemSelected(SongViewHolder viewHolder, int position) {
        viewHolder.mTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        viewHolder.mDurationView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    @Override
    protected void onItemUnselected(SongViewHolder viewHolder, int position) {
        viewHolder.mTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        viewHolder.mDurationView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
    }

    public class SongViewHolder extends AbsDetailsAdapter.DetailsViewHolder {

        private CardView mRoot;
        private ImageView mCoverView;
        private TextView mTitleView;
        private TextView mDurationView;

        protected SongViewHolder(View view) {
            super(view);

            mRoot = (CardView) view;
            mCoverView = (ImageView) view.findViewById(R.id.cover);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mDurationView = (TextView) view.findViewById(R.id.duration);
        }
    }
}
