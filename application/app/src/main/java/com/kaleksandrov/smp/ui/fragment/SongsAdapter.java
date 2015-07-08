package com.kaleksandrov.smp.ui.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.ui.img.CoverManager;

import java.util.List;

/**
 * Created by kaleksandrov on 4/29/15.
 */
public class SongsAdapter extends AbsLibraryAdapter<Song, SongsAdapter.SongViewHolder> {

    private static final String LOG_TAG = SongsAdapter.class.getSimpleName();

    private CoverManager mCoverManager;

    public class SongViewHolder extends AbsLibraryAdapter.ViewHolder {

        private TextView mArtist;

        protected SongViewHolder(View view) {
            super(view);

            mArtist = (TextView) view.findViewById(R.id.artist);
        }
    }

    public SongsAdapter(Activity activity, List<Song> songs) {
        super(activity, songs, R.layout.card_song);

        FairPlayerApplication app = (FairPlayerApplication) activity.getApplication();
        mCoverManager = app.getCoverManager();
    }

    @Override
    protected void populateView(SongViewHolder viewHolder, Song song) {
        viewHolder.mId = song.getId();
        viewHolder.mNameView.setText(song.getName());
        viewHolder.mArtist.setText(song.getAlbum().getArtist().getName());
        mCoverManager.loadInto(song.getAlbum().getCoverPath(), viewHolder.mCoverView);
    }

    @Override
    protected SongViewHolder createViewHolder(View view) {
        return new SongViewHolder(view);
    }

    @Override
    public void onViewRecycled(SongViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
        mCoverManager.cancelLoading(viewHolder.mCoverView);
    }
}