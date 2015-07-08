package com.kaleksandrov.smp.ui.activity;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Artist;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.ui.img.CoverManager;

/**
 * Created by kaleksandrov on 5/3/15.
 */
public class ArtistDetailsAdapter extends AbsDetailsHeaderAdapter<Song, ArtistDetailsAdapter.ArtistViewHolder> {

    private Artist mArtist;
    private CoverManager mCoverManager;

    public ArtistDetailsAdapter(Activity activity, Artist artist) {
        super(activity, artist.getSongs(), R.layout.view_artist_details, R.layout.card_song3);

        mArtist = artist;
        FairPlayerApplication app = (FairPlayerApplication) activity.getApplication();
        mCoverManager = app.getCoverManager();
    }

    @Override
    protected void populateSummaryView(ArtistViewHolder viewHolder) {
        viewHolder.mTotalDurationView.setText(formatDuration(mArtist.getDuration()));
        viewHolder.mTrackCountView.setText(Integer.toString(mArtist.getSongs().size()));
    }

    @Override
    protected void populateView(ArtistViewHolder viewHolder, Song song) {
        viewHolder.mId = song.getId();
        viewHolder.mTitleView.setText(song.getName());
        viewHolder.mDurationView.setText(formatDuration(song.getDuration()));
        mCoverManager.loadInto(song.getAlbum().getCoverPath(), viewHolder.mCoverView);
    }

    @Override
    public void onViewRecycled(ArtistViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
        mCoverManager.cancelLoading(viewHolder.mCoverView);
    }

    @Override
    protected ArtistViewHolder createViewHolder(View view) {
        return new ArtistViewHolder(view);
    }

    public class ArtistViewHolder extends AbsDetailsHeaderAdapter.DetailsHeaderViewHolder {

        // Summary views
        private TextView mTotalDurationView;
        private TextView mTrackCountView;

        // Song views
        private ImageView mCoverView;
        private TextView mTitleView;
        private TextView mDurationView;

        protected ArtistViewHolder(View view) {
            super(view);

            // Summary views
            mTotalDurationView = (TextView) view.findViewById(R.id.duration);
            mTrackCountView = (TextView) view.findViewById(R.id.trackCount);

            // Song views
            mCoverView = (ImageView) view.findViewById(R.id.cover);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mDurationView = (TextView) view.findViewById(R.id.duration);
        }
    }
}
