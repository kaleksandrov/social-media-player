package com.kaleksandrov.smp.application;

import android.app.Application;
import android.util.Log;

import com.kaleksandrov.smp.content.Content;
import com.kaleksandrov.smp.content.ContentLoader;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.FairPlayer;
import com.kaleksandrov.smp.player.FairPlayerImpl;
import com.kaleksandrov.smp.player.MediaList;
import com.kaleksandrov.smp.player.PlaylistManager;
import com.kaleksandrov.smp.player.PlaylistManagerImpl;
import com.kaleksandrov.smp.ui.img.CoverManager;

import java.util.ArrayList;

/**
 * The {@link Application} class. Imitates singleton design pattern - the Android OS guarantees that
 * only one instance of this class can exist in a proper moment of time.
 *
 * @author kaleksandrov
 */
public class FairPlayerApplication extends Application {

	/* Members */

    private FairPlayer mPlayer;

    private MediaList mPlaylist;

    private PlaylistManager mPlaylistManager;

    private CoverManager mCoverManager;

    private Content mContent;

    private boolean mIsLoaded;

	/* Public methods */

	/* Getters && Setters */

    public FairPlayer getPlayer() {
        Log.d("TTT", "Get mPlayer : " + mPlayer);
        return mPlayer;
    }

    /**
     * Retrieves the name of the application. TODO Remove this from the Application class
     *
     * @return The application name as a {@link String}.
     */
    public String getName() {
        return getString(getApplicationInfo().labelRes);
    }

    public MediaList getPlaylist() {
        return mPlaylist;
    }

    public void setPlaylist(MediaList playlist) {
        mPlaylist = playlist;
        mPlayer.setPlaylist(mPlaylist);
    }

    public PlaylistManager getPlaylistManager() {
        return mPlaylistManager;
    }

    public CoverManager getCoverManager() {
        return mCoverManager;
    }

    public Content getContent() {
        return mContent;
    }

    public void initResource() {
        if(mIsLoaded) {
            return;
        }

        ContentLoader loader = new ContentLoader(this);
        mContent = loader.loadContent();

        mPlaylistManager = new PlaylistManagerImpl(this);
        mPlaylist = mPlaylistManager.create(new ArrayList<Song>());
        mPlayer = new FairPlayerImpl(this, mPlaylist);

        Log.d("TTT", "Created mPlayer : " + mPlayer);
        mCoverManager = new CoverManager(this);
        
        mIsLoaded =true;
    }
}
