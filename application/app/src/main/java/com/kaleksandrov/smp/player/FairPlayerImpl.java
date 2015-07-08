package com.kaleksandrov.smp.player;

import android.content.Context;
import android.util.Log;

import com.kaleksandrov.smp.exception.MediaPlayerException;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.OSMediaPlayerWrapper.OnCompleteListener;

public class FairPlayerImpl implements FairPlayer {

	/* Constants */

    private static final String WAKE_LOCK_KEY = FairPlayerImpl.class.getName();

	/* Members */

    private MediaListInternal mPlaylist;
    private OSMediaPlayerWrapper mPlayer;
    private WakeLockManager mWakeLockManager;
    private Context mContext;

	/* Constructors */

    public FairPlayerImpl(final Context context, final MediaList playlist) {
        if (!(playlist instanceof MediaListInternal)) {
            throw new IllegalArgumentException("The given list implementation is not supported! Use PlaylistManager class to manage playlist!");
        }
        mContext = context.getApplicationContext();
        mPlaylist = (MediaListInternal) playlist;
        mPlayer = new OSMediaPlayerWrapperImpl(mContext);
        mPlayer.setOnCompleteListener(new OnCompleteListener() {

            @Override
            public void onComplete(Song song) {
                next();
            }
        });
        mWakeLockManager = new WakeLockManager(mContext);
    }

	/* Public methods */

    @Override
    public void play(final int index) {
        Song song = mPlaylist.moveTo(index);
        play(song);
    }

    @Override
    public void play() {
        Song song = mPlaylist.getCurrent();
        play(song);
    }

    @Override
    public void pause() {
        mPlayer.pause(true);
        mWakeLockManager.releaseLock(WAKE_LOCK_KEY);
    }

    @Override
    public void unpause() {
        try {
            mWakeLockManager.acquireLock(WAKE_LOCK_KEY);
            mPlayer.play();
        } catch (MediaPlayerException e) {
            Log.e("TTT", "Cannot play the given media!", e);
        }
    }

    @Override
    public void stop() {
        mPlayer.stop();
        mWakeLockManager.releaseLock(WAKE_LOCK_KEY);
    }

    @Override
    public void toggle() {
        switch (mPlayer.getStatus()) {
            case PLAYING: {
                pause();
                break;
            }

            case PAUSED: {
                unpause();
                break;
            }

            case STOPED: {
                play();
                break;
            }

            default:
                // TODO Throw an exception
                break;
        }
    }

    @Override
    public void seek(int milliseconds) {
        mPlayer.seek(milliseconds);
    }

    @Override
    public int getCurrentProgress() {
        return mPlayer.getCurrentPosition();
    }

    @Override
    public Song next() {
        Song song = mPlaylist.next();

        switch (mPlayer.getStatus()) {
            case PLAYING: {
                play(song);
                break;
            }

            case PAUSED: {
                play(song);
                break;
            }

            case STOPED: {
                // do nothing
                break;
            }

            default:
                // TODO Throw an exception
                break;
        }

        return song;
    }

    @Override
    public Song previous() {
        Song song = mPlaylist.previous();

        switch (mPlayer.getStatus()) {
            case PLAYING: {
                play(song);
                break;
            }

            case PAUSED: {
                play(song);
                break;
            }

            case STOPED: {
                // do nothing
                break;
            }

            default:
                // TODO Throw an exception
                break;
        }

        return song;
    }

    @Override
    public Song getCurrentSong() {
        return mPlaylist.getCurrent();
    }

    @Override
    public PlayerStatus getStatus() {
        return mPlayer.getStatus();
    }

    @Override
    public void setPlaylist(MediaList playlist) {
        mPlaylist = (MediaListInternal) playlist;
    }

	/* Private methods */

    private void play(Song song) {
        if (song == null) {
            return;
        }

        try {
            mWakeLockManager.acquireLock(WAKE_LOCK_KEY);
            mPlayer.play(song);
        } catch (MediaPlayerException e) {
            Log.e("TTT", "Cannot play the given media!", e);
        }
    }

    @Override
    public void fastForward() {
        // TODO Implement
    }

    @Override
    public void rewind() {
        // TODO Implement
    }
}
