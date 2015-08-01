package com.kaleksandrov.smp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.content.Content;
import com.kaleksandrov.smp.model.Album;
import com.kaleksandrov.smp.model.Song;

/**
 * Created by kaleksandrov on 5/2/15.
 */
public class AlbumDetailsActivity extends AbsDetailsActivity {

    private Album mAlbum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null && !extras.isEmpty()) {
            int albumId = extras.getInt(EXTRA_KEY_CONTENT_ID);
            if (albumId != 0) {
                FairPlayerApplication app = (FairPlayerApplication) getApplication();
                Content content = app.getContent();
                mAlbum = content.getAlbum(albumId);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }

        super.onCreate(savedInstanceState);


        mCoverManager.loadInto(mAlbum.getCoverPath(), mCoverArt);
    }

    @Override
    protected String getTitleString() {
        return mAlbum.getName();
    }

    @Override
    protected AbsDetailsAdapter getAdapter() {
        AlbumDetailsAdapter adapter = new AlbumDetailsAdapter(this, mAlbum);
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
        startPlayerActivity(mAlbum.getSongs(), false);
    }
}
