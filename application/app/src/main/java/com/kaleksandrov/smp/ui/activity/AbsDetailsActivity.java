package com.kaleksandrov.smp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.MediaList;
import com.kaleksandrov.smp.player.PlaylistManager;
import com.kaleksandrov.smp.ui.view.GeneralPaddingItemDecoration;
import com.kaleksandrov.smp.ui.view.NowPlayingBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaleksandrov on 5/1/15.
 */
abstract class AbsDetailsActivity extends AppCompatActivity implements AbsDetailsAdapter.OnItemClickListener<Song>, AbsDetailsHeaderAdapter.OnHeaderClickListener {

    private static final String LOG_TAG = AbsDetailsActivity.class.getSimpleName();

    public static final String EXTRA_KEY_CONTENT_ID = "extra_key_artist_id";

    protected Toolbar mToolbar;
    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected NowPlayingBar mFooter;

    private MediaPlayerClient mPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getTitleString());

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mRecyclerView.addItemDecoration(new GeneralPaddingItemDecoration(this));

        mFooter = (NowPlayingBar) findViewById(R.id.footer);
        mFooter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AbsDetailsActivity.this, PlayerActivity.class);
                startActivity(intent);
            }
        });

        mPlayer = new MediaPlayerClient(this);
        mFooter.setMediaPlayerClient(mPlayer);
    }

    @Override
    protected void onPause() {
        mPlayer.disconnect();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mPlayer.connect();
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    protected abstract String getTitleString();

    protected abstract AbsDetailsHeaderAdapter getAdapter();

    protected void startPlayerActivity(Song song) {
        List<Song> songs = new ArrayList<>(1);
        songs.add(song);
        startPlayerActivity(songs, false);
    }

    protected void startPlayerActivity(List<Song> songs, boolean isShuffle) {
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.EXTRA_RESET_PLAYBACK, true);

        FairPlayerApplication app = (FairPlayerApplication) getApplication();
        PlaylistManager playlistManager = app.getPlaylistManager();
        MediaList playlist = playlistManager.create(songs);
        playlist.setIsRepeat(true);
        playlist.setIsShuffle(isShuffle);
        app.setPlaylist(playlist);

        startActivity(intent);
    }
}
