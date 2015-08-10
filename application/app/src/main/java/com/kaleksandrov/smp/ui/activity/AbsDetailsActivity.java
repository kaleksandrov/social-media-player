package com.kaleksandrov.smp.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.MediaList;
import com.kaleksandrov.smp.player.PlaylistManager;
import com.kaleksandrov.smp.ui.img.CoverManager;
import com.kaleksandrov.smp.ui.view.DetailsPaddingItemDecoration;
import com.kaleksandrov.smp.ui.view.NowPlayingBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaleksandrov on 5/1/15.
 */
abstract class AbsDetailsActivity extends AppCompatActivity implements AbsDetailsAdapter.OnItemClickListener<Song> {

    private static final String LOG_TAG = AbsDetailsActivity.class.getSimpleName();

    public static final String EXTRA_KEY_CONTENT_ID = "extra_key_artist_id";

    protected Toolbar mToolbar;
    protected CollapsingToolbarLayout mToolbarLayout;
    protected RecyclerView mRecyclerView;
    protected LinearLayoutManager mLinearLayoutManager;
    protected NowPlayingBar mFooter;
    protected ImageView mCoverArt;
    protected FloatingActionButton mPlayButton;

    protected CoverManager mCoverManager;
    private MediaPlayerClient mPlayer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        }

        setContentView(R.layout.activity_details);

        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbarLayout);
        mToolbarLayout.setTitle(getTitleString());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.list);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(getAdapter());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mRecyclerView.addItemDecoration(new DetailsPaddingItemDecoration(this));
        mCoverArt = (ImageView) findViewById(R.id.coverArtHeader);

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

        mPlayButton = (FloatingActionButton) findViewById(R.id.play);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayClick();
            }
        });

        mCoverManager = ((FairPlayerApplication) getApplication()).getCoverManager();
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
        supportFinishAfterTransition();
        return false;
    }

    protected abstract String getTitleString();

    protected abstract AbsDetailsAdapter getAdapter();

    protected abstract void onPlayClick();

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
