package com.kaleksandrov.smp.ui.activity;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Album;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.ui.img.CoverManager;
import com.kaleksandrov.smp.util.StringUtils;

/**
 * Created by kaleksandrov on 5/3/15.
 */
public class AlbumDetailsAdapter extends AbsDetailsHeaderAdapter<Song, AlbumDetailsAdapter.AlbumViewHolder> {

    private Album mAlbum;
    private CoverManager mCoverManager;

    public AlbumDetailsAdapter(Activity activity, Album album) {
        super(activity, album.getSongs(), R.layout.view_album_details, R.layout.card_song2);

        mAlbum = album;
        mCoverManager = ((FairPlayerApplication) activity.getApplication()).getCoverManager();
    }

    @Override
    protected void populateSummaryView(AlbumViewHolder viewHolder) {
        viewHolder.mArtistView.setText(mAlbum.getArtist().getName());
        if (mAlbum.getYear() == 0) {
            viewHolder.mYearView.setText(StringUtils.EMPTY);
        } else {
            viewHolder.mYearView.setText(Integer.toString(mAlbum.getYear()));
        }
        viewHolder.mTotalDurationView.setText(formatDuration(mAlbum.getDuration()));
        viewHolder.mTrackCountView.setText(Integer.toString(mAlbum.getSongs().size()));

        mCoverManager.loadInto(mAlbum.getCoverPath(), viewHolder.mCoverView);
    }

    @Override
    protected void populateView(AlbumViewHolder viewHolder, Song song) {
        viewHolder.mId = song.getId();
        viewHolder.mTitleView.setText(song.getName());
        if (song.getTrackNumber() == 0) {
            viewHolder.mTrackNumberView.setText(StringUtils.EMPTY);
        } else {
            viewHolder.mTrackNumberView.setText(Integer.toString(song.getTrackNumber()));
        }
        viewHolder.mDurationView.setText(formatDuration(song.getDuration()));
    }

    @Override
    public void onViewRecycled(AlbumViewHolder holder) {
        mCoverManager.cancelLoading(holder.mCoverView);
        super.onViewRecycled(holder);
    }

    @Override
    protected AlbumViewHolder createViewHolder(View view) {
        return new AlbumViewHolder(view);
    }

    public class AlbumViewHolder extends AbsDetailsHeaderAdapter.DetailsHeaderViewHolder {

        // Summary views
        private ImageView mCoverView;
        private TextView mArtistView;
        private TextView mYearView;
        private TextView mTotalDurationView;
        private TextView mTrackCountView;

        // Song views
        private TextView mTrackNumberView;
        private TextView mTitleView;
        private TextView mDurationView;

        protected AlbumViewHolder(View view) {
            super(view);

            // Summary views
            mCoverView = (ImageView) view.findViewById(R.id.cover);
            mArtistView = (TextView) view.findViewById(R.id.artist);
            mYearView = (TextView) view.findViewById(R.id.year);
            mTotalDurationView = (TextView) view.findViewById(R.id.duration);
            mTrackCountView = (TextView) view.findViewById(R.id.trackCount);

            // Song views
            mTrackNumberView = (TextView) view.findViewById(R.id.trackNumber);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mDurationView = (TextView) view.findViewById(R.id.duration);
        }
    }
}
