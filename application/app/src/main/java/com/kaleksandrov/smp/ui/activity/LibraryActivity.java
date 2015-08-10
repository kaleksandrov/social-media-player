package com.kaleksandrov.smp.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.view.View;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.content.Content;
import com.kaleksandrov.smp.model.Album;
import com.kaleksandrov.smp.model.Artist;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.MediaList;
import com.kaleksandrov.smp.player.PlaylistManager;
import com.kaleksandrov.smp.ui.fragment.AbsLibraryAdapter;
import com.kaleksandrov.smp.ui.fragment.AbsRecyclerViewFragment;
import com.kaleksandrov.smp.ui.fragment.AlbumsAdapter;
import com.kaleksandrov.smp.ui.fragment.AlbumsFragment;
import com.kaleksandrov.smp.ui.fragment.ArtistsAdapter;
import com.kaleksandrov.smp.ui.fragment.ArtistsFragment;
import com.kaleksandrov.smp.ui.fragment.SongsAdapter;
import com.kaleksandrov.smp.ui.fragment.SongsFragment;
import com.kaleksandrov.smp.ui.view.NowPlayingBar;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity implements AbsLibraryAdapter.OnItemClickListener {

    private static final String LOG_TAG = LibraryActivity.class.getSimpleName();
    private Toolbar mToolbar;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private NowPlayingBar mNowPlayingBar;
    private MediaPlayerClient mPlayer;
    private PlaylistManager mPlaylistManager;
    private Content mContent;
    private FloatingActionButton mPlayAllButton;
    private TabLayout mTabsNew;

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
        mTabsNew = (TabLayout) findViewById(R.id.tabsNew);
        mTabsNew.setupWithViewPager(mViewPager);

        mNowPlayingBar = (NowPlayingBar) findViewById(R.id.footer);
        mPlayer = new MediaPlayerClient(this);
        mNowPlayingBar.setMediaPlayerClient(mPlayer);
        mNowPlayingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LibraryActivity.this, PlayerActivity.class);
                startActivity(intent);

            }
        });

        mPlayAllButton = (FloatingActionButton) findViewById(R.id.play);
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

        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(this,
                        Pair.create(view.findViewById(R.id.avatar), "avatar"));
        ActivityCompat.startActivity(this, intent, options.toBundle());
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
