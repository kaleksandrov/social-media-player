package com.kaleksandrov.smp.ui.activity;

import android.app.Activity;
import android.view.View;
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
public class AlbumDetailsAdapter extends AbsDetailsAdapter<Song, AlbumDetailsAdapter.AlbumViewHolder> {

    private Album mAlbum;
    private CoverManager mCoverManager;

    public AlbumDetailsAdapter(Activity activity, Album album) {
        super(activity, album.getSongs(), R.layout.card_song2);

        mAlbum = album;
        mCoverManager = ((FairPlayerApplication) activity.getApplication()).getCoverManager();
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
    protected AlbumViewHolder createViewHolder(View view) {
        return new AlbumViewHolder(view);
    }

    public class AlbumViewHolder extends AbsDetailsAdapter.DetailsViewHolder {

        private TextView mTrackNumberView;
        private TextView mTitleView;
        private TextView mDurationView;

        protected AlbumViewHolder(View view) {
            super(view);

            mTrackNumberView = (TextView) view.findViewById(R.id.trackNumber);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mDurationView = (TextView) view.findViewById(R.id.duration);
        }
    }
}
