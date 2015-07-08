package com.kaleksandrov.smp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.content.Content;
import com.kaleksandrov.smp.model.Album;
import com.kaleksandrov.smp.model.Artist;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.MediaList;
import com.kaleksandrov.smp.player.PlaylistManager;
import com.kaleksandrov.smp.service.PlayerService;
import com.kaleksandrov.smp.ui.fragment.AbsLibraryAdapter;
import com.kaleksandrov.smp.ui.fragment.AbsRecyclerViewFragment;
import com.kaleksandrov.smp.ui.fragment.AlbumsAdapter;
import com.kaleksandrov.smp.ui.fragment.AlbumsFragment;
import com.kaleksandrov.smp.ui.fragment.ArtistsAdapter;
import com.kaleksandrov.smp.ui.fragment.ArtistsFragment;
import com.kaleksandrov.smp.ui.fragment.SongsAdapter;
import com.kaleksandrov.smp.ui.fragment.SongsFragment;
import com.kaleksandrov.smp.ui.view.NowPlayingBar;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class LibraryActivity extends AppCompatActivity implements AbsLibraryAdapter.OnItemClickListener, Observer {

    private static final String LOG_TAG = LibraryActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private NowPlayingBar mNowPlayingBar;
    private MediaPlayerClient mPlayer;
    private PlaylistManager mPlaylistManager;
    private Content mContent;
    private PagerSlidingTabStrip mTabs;
    private FloatingActionButton mPlayAllButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.library_title);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Bind the tabs to the ViewPager
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mTabs.setViewPager(mViewPager);

        mNowPlayingBar = (NowPlayingBar) findViewById(R.id.footer);
        mPlayer = new MediaPlayerClient(this);
        mPlayer.addObserver(this);
        mNowPlayingBar.setMediaPlayerClient(mPlayer);
        mNowPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, PlayerActivity.class);
                startActivity(intent);

            }
        });

        mPlayAllButton = (FloatingActionButton) findViewById(R.id.play_all);
        mPlayAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, PlayerActivity.class);
                intent.putExtra(PlayerActivity.EXTRA_RESET_PLAYBACK, true);

                FairPlayerApplication app = (FairPlayerApplication) getApplication();
                PlaylistManager playlistManager = app.getPlaylistManager();
                MediaList playlist = playlistManager.create(mContent.getSongs());
                playlist.setIsRepeat(true);
                playlist.setIsShuffle(false);
                app.setPlaylist(playlist);

                startActivity(intent);
            }
        });

        FairPlayerApplication app = (FairPlayerApplication) getApplication();
        mPlaylistManager = app.getPlaylistManager();
        mContent = app.getContent();
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
    public void onItemClick(View view, Object item) {
        int id = mViewPager.getCurrentItem();

        Intent intent = null;
        switch (id) {
            case 0:
                intent = new Intent(this, ArtistDetailsActivity.class);
                intent.putExtra(AbsDetailsActivity.EXTRA_KEY_CONTENT_ID, ((Artist) item).getId());
                break;
            case 1:
                intent = new Intent(this, AlbumDetailsActivity.class);
                intent.putExtra(AbsDetailsActivity.EXTRA_KEY_CONTENT_ID, ((Album) item).getId());
                break;
            case 2:
                intent = new Intent(this, PlayerActivity.class);
                intent.putExtra(PlayerActivity.EXTRA_RESET_PLAYBACK, true);

                List<Song> songs = new ArrayList<>(1);
                songs.add(mContent.getSong(((Song) item).getId()));

                MediaList playlist = mPlaylistManager.create(songs);
                FairPlayerApplication app = (FairPlayerApplication) getApplication();
                app.setPlaylist(playlist);
                break;
            default:
        }

        startActivity(intent);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (!(data instanceof PlayerService.State)) {
            return;
        }

        PlayerService.State state = (PlayerService.State) data;

        Song song = state.getSong();
        if (song == null) {
            mPlayAllButton.setVisibility(View.VISIBLE);
        } else {
            mPlayAllButton.setVisibility(View.GONE);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            AbsRecyclerViewFragment fragment = null;
            switch (position) {
                case 0:
                    ArtistsAdapter artistsAdapter = new ArtistsAdapter(LibraryActivity.this, mContent.getArtists());
                    artistsAdapter.setOnItemClickListener(LibraryActivity.this);
                    fragment = ArtistsFragment.newInstance();
                    fragment.setAdapter(artistsAdapter);
                    break;
                case 1:
                    AlbumsAdapter albumsAdapter = new AlbumsAdapter(LibraryActivity.this, mContent.getAlbums());
                    albumsAdapter.setOnItemClickListener(LibraryActivity.this);
                    fragment = AlbumsFragment.newInstance();
                    fragment.setAdapter(albumsAdapter);
                    break;
                case 2:
                    SongsAdapter songsAdapter = new SongsAdapter(LibraryActivity.this, mContent.getSongs());
                    songsAdapter.setOnItemClickListener(LibraryActivity.this);
                    fragment = SongsFragment.newInstance();
                    fragment.setAdapter(songsAdapter);
                    break;
                default:
                    throw new IllegalArgumentException();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_title_artists);
                case 1:
                    return getString(R.string.tab_title_albums);
                case 2:
                    return getString(R.string.tab_title_songs);
            }
            return null;
        }
    }
}
