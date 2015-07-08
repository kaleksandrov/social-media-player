package com.kaleksandrov.smp.player;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.kaleksandrov.smp.exception.MediaPlayerException;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.FairPlayer.PlayerStatus;

import java.io.IOException;

/**
 * Implementation of the {@link OSMediaPlayerWrapper} interface. Wraps the Android
 * {@link MediaPlayer} API and provides a simpler interface.
 *
 * @author kaleksandrov
 * @since Aug 23, 2013
 */
class OSMediaPlayerWrapperImpl implements OSMediaPlayerWrapper, OnAudioFocusChangeListener {

	/* Constants */

    private static final String LOG_TAG = OSMediaPlayerWrapperImpl.class.getSimpleName();

    private static final String WIFI_LOCK_NAME = "wifi-lock:"
            + OSMediaPlayerWrapperImpl.class.getSimpleName();

	/* Private fields */

    /**
     * Holds the current position in milliseconds.
     */
    protected int currentPosition;

    /**
     * Holds the last played media.
     */
    protected Song currentSong;

    protected MediaPlayer player;

    protected Context mContext;

    private OnPlayListener onPlayListener;

    private OnStopListener onStopListener;

    private OnPauseListener onPauseListener;

    private OnCompleteListener onCompleteListener;

    private OnErrorListener onErrorListener;

    private boolean isStoppedDueToAudioFocusLost;

    private WifiLock wifiLock;

    private PlayerStatus mStatus;

	/* Constructors */

    public OSMediaPlayerWrapperImpl(Context context) {
        if (context != null) {
            mContext = context;
            mStatus = PlayerStatus.STOPED;

            configureAudioFocus();
        } else {
            throw new IllegalArgumentException("The 'context' must NOT be null!");
        }
    }

    /**
     * If the player is NOT <b><code>null</code></b>, restores the last saved position and plays the
     * media.</br> If the player is <b><code>null</code> </b> but the last played media is
     * available, play it.</br> Otherwise throw exception.
     *
     * @throws MediaPlayerException If there is no previously used resources.
     */
    @Override
    public void play() throws MediaPlayerException {
        if (player != null) {
            Log.d("TTT", "There player is paused in position " + currentPosition + ". Play it!");
            player.start();
            player.seekTo(currentPosition);
            mStatus = PlayerStatus.PLAYING;
            if (onPlayListener != null) {
                onPlayListener.onPlay(currentSong);
            }
        } else if (currentSong != null) {
            Log.d("TTT", "The player is stopped. Restore the previous state.!");
            play(currentSong, currentPosition);
        } else {
            Log.d("TTT", "There is no media loaded!");
            throw new MediaPlayerException("There is no media loaded!");
        }
    }

    @Override
    public void play(Song song) throws MediaPlayerException {
        play(song, 0);

        if (onPlayListener != null) {
            onPlayListener.onPlay(currentSong);
        }
    }

    /**
     * Pauses the player if is running.
     *
     * @param shouldReleaseResources If set to <code>true</code> the player will be destroyed and all resources used
     *                               will be dismissed. This approached is used for good resource management if the
     *                               player is supposed to stay in paused state long time or a configuration change is
     *                               happening. A good example of using this method with
     *                               <code>shouldReleaseResources</code> parameter set to <code>true</code> is in
     *                               {@link Activity}'s lifecycle methods - <code>onPause</code> and
     *                               <code>onResume</code>.</br> If the player will be paused for a short time, set
     *                               this to <code>false</code>.
     */
    @Override
    public void pause(boolean shouldReleaseResources) {
        if (isPlaying()) {
            savePosition();
            if (shouldReleaseResources) {
                disposePlayer();
            } else {
                player.pause();
            }

            if (onPauseListener != null) {
                onPauseListener.onPause(currentSong);
            }

            mStatus = PlayerStatus.PAUSED;
        }
    }

    /**
     * Stops the currently playing media. When stopped the player, all used resources are disposed.
     */
    @Override
    public void stop() {
        if (player != null) {
            currentPosition = 0;
            disposePlayer();

            if (onStopListener != null) {
                onStopListener.onStop(currentSong);
            }

            mStatus = PlayerStatus.STOPED;
        } else {
            Log.i(LOG_TAG, "The player is not running.");
        }
    }

    @Override
    public void seek(int milliseconds) {
        if (player != null) {
            player.seekTo(milliseconds);
        }
    }

    /**
     * Returns the percentage of the played media.
     */
    @Override
    public int getPercentage() {
        int duration = getTotalDuration();
        int position = getCurrentPosition();

        if (duration > 0) {
            return (int) ((position * 100.0) / duration);
        } else {
            return 0;
        }
    }

    /**
     * Returns the milliseconds played from the media.
     */
    @Override
    public int getCurrentPosition() {
        // TODO Sometimes this throws an exception because I'm trying to get the current position
        // while the song is not prepared yet. Try to fix it!!
        try {
            if (player != null
                    && (mStatus == PlayerStatus.PLAYING || mStatus == PlayerStatus.PAUSED)) {
                return player.getCurrentPosition();
            } else {
                return currentPosition;
            }
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Returns the played media total duration.
     */
    @Override
    public int getTotalDuration() {
        if (player != null) {
            return player.getDuration();
        } else {
            return 0;
        }
    }

	/* Private methods */

    /**
     * Checks if the player is initialized and a media is currently played.
     */
    @Override
    public boolean isPlaying() {
        return (player != null && player.isPlaying());
    }

    @Override
    public PlayerStatus getStatus() {
        return mStatus;
    }

    @Override
    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    @Override
    public void setOnStopListener(OnStopListener onStopListener) {
        this.onStopListener = onStopListener;
    }

    @Override
    public void setOnPauseListener(OnPauseListener onPauseListener) {
        this.onPauseListener = onPauseListener;
    }

    @Override
    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        attachOnCompleteListener();
    }

    @Override
    public void setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
        attachOnErrorListener();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN: {
                // Resume the player

                Log.d("TTT", "GAIN");

                if (isStoppedDueToAudioFocusLost) {
                    isStoppedDueToAudioFocusLost = false;

                    try {
                        play();
                    } catch (MediaPlayerException e) {
                        Log.e(LOG_TAG, "Cannot play!", e);
                    }
                }

                break;
            }

            case AudioManager.AUDIOFOCUS_LOSS: {
                // Lost focus for an unbounded amount of time so stop the player and
                // release media resources.

                Log.d("TTT", "LOSS");

                isStoppedDueToAudioFocusLost = false;
                stop();
                break;
            }

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                // Lost focus for a short time, but we have to stop the player.
                // Don't release the media player resource
                // because it is likely to resume

                Log.d("TTT", "LOSS_TRANSIENT");

                if (isPlaying()) {
                    isStoppedDueToAudioFocusLost = true;
                }
                pause(true);
                break;
            }

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                // Lost focus for a short time, but it's OK to keep playing at an
                // attenuated level

                Log.d("TTT", "LOSS_TRANSIENT_CAN_DUCK");

                if (isPlaying()) {
                    isStoppedDueToAudioFocusLost = true;
                }

                pause(false);
                break;
            }

            default: {
                // Unknown focus changes

                Log.d("TTT", "Unknown : " + focusChange);
                break;
            }
        }
    }

	/* Protected methods */

    /**
     * Saves the current position.
     */
    protected void savePosition() {
        if (player != null) {
            currentPosition = player.getCurrentPosition();
        }
    }

    /**
     * Configures the {@link MediaPlayer} object. Attaches listeners, saves currently played media
     * and configures {@link WakeLock}.
     */
    protected void configurePlayer(Song song) {
        if (song.isLockRequired()) {
            // Require WiFi lock only if the player needs a network connection -
            // this is an MediaRemotePlayer
            wifiLock = ((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL,
                    WIFI_LOCK_NAME);
            wifiLock.acquire();
        }

        attachPlayerNativeListeners();
        currentSong = song;
    }

    /**
     * Stops the player and disposes all the resources used.
     */
    protected void disposePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }

        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
            wifiLock = null;
        }
    }

	/* Private methods */

    private void play(Song song, final int offset) throws MediaPlayerException {
        if (song == null) {
            throw new IllegalArgumentException("The provided media object must NOT be null!");
        }

        if (player != null) {
            disposePlayer();
        }

        player = new MediaPlayer();
        player.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.seekTo(offset);
                mp.start();
                if (onPlayListener != null) {
                    onPlayListener.onPlay(currentSong);
                }
            }
        });
        mStatus = PlayerStatus.PLAYING;
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // TODO Probably this does not work for RemoteMedia
            player.setDataSource(mContext, song.getUri());
            player.prepareAsync();
        } catch (IOException e) {
            throw new MediaPlayerException(e);
        }

        configurePlayer(song);
    }

    /**
     * Attach onComplete listener to the {@link MediaPlayer} object.
     */
    private void attachOnCompleteListener() {
        if (player != null && onCompleteListener != null) {
            player.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    onCompleteListener.onComplete(currentSong);
                }
            });
        }
    }

    /**
     * Attach onError listener to the {@link MediaPlayer} object.
     */
    private void attachOnErrorListener() {
        if (player != null && onErrorListener != null) {
            player.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    onErrorListener.onError();
                    return true;
                }
            });
        }
    }

    /**
     * Attach listeneres directly to the {@link MediaPlayer} object
     */
    private void attachPlayerNativeListeners() {
        attachOnCompleteListener();
        attachOnErrorListener();
    }

    /**
     * Requests the audio focus from the Audio manager
     */
    private void configureAudioFocus() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);

    }
}
