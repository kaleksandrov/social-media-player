package com.kaleksandrov.smp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.content.Content;
import com.kaleksandrov.smp.model.Artist;
import com.kaleksandrov.smp.model.Song;

/**
 * Created by kaleksandrov on 5/2/15.
 */
public class ArtistDetailsActivity extends AbsDetailsActivity {

    private Artist mArtist;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null && !extras.isEmpty()) {
            int artistId = extras.getInt(EXTRA_KEY_CONTENT_ID);
            if (artistId != 0) {
                FairPlayerApplication app = (FairPlayerApplication) getApplication();
                Content content = app.getContent();
                mArtist = content.getArtist(artistId);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }

        super.onCreate(savedInstanceState);
    }

    @Override
    protected String getTitleString() {
        return mArtist.getName();
    }

    @Override
    protected AbsDetailsAdapter getAdapter() {
        ArtistDetailsAdapter adapter = new ArtistDetailsAdapter(this, mArtist);
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    public void onItemClick(View view, Song song) {
        startPlayerActivity(song);
    }

    @Override
    public void onItemLongClick(View view, Song item) {
        // nothing
    }

    @Override
    protected void onPlayClick() {
        startPlayerActivity(mArtist.getSongs(), false);
    }
}
