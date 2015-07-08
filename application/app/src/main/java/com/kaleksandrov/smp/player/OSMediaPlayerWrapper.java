package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.exception.MediaPlayerException;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.FairPlayer.PlayerStatus;

/**
 * Interface that describes a generic media player functionality
 *
 * @author kaleksandrov
 * @since Aug 23, 2013
 */
public interface OSMediaPlayerWrapper {

    /**
     * Interface that provides a callback functionality when a media is played.
     *
     * @author kaleksandrov
     * @since Aug 23, 2013
     */
    interface OnPlayListener {
        void onPlay(Song song);
    }

    /**
     * Interface that provides a callback functionality when a media is stopped.
     *
     * @author kaleksandrov
     * @since Aug 23, 2013
     */
    interface OnStopListener {
        void onStop(Song song);
    }

    /**
     * Interface that provides a callback functionality when a media is paused.
     *
     * @author kaleksandrov
     * @since Aug 23, 2013
     */
    interface OnPauseListener {
        void onPause(Song song);
    }

    /**
     * Interface that provides a callback functionality when a media progress is changed.
     *
     * @author kaleksandrov
     * @since Aug 23, 2013
     */
    interface OnProgressChangeListener {
        void onProgressChange(Song song, int percent);
    }

    /**
     * Interface that provides a callback functionality when a media playing is finished.
     *
     * @author kaleksandrov
     * @since Aug 23, 2013
     */
    interface OnCompleteListener {
        void onComplete(Song song);
    }

    /**
     * Interface that provides a callback functionality when something goes wrong.
     *
     * @author kaleksandrov
     * @since Aug 23, 2013
     */
    interface OnErrorListener {
        void onError();
    }

    /**
     * Resumes a paused media resource.
     *
     * @throws MediaPlayerException If something goes wrong.
     */
    void play() throws MediaPlayerException;

    void play(Song song) throws MediaPlayerException;

    /**
     * Pauses the media player if is running.
     */
    void pause(final boolean shouldReleaseResources);

    /**
     * Stops the media player if is running.
     */
    void stop();

    void seek(int milliseconds);

    /**
     * Checks if the media player is running.
     *
     * @return
     */
    boolean isPlaying();

    PlayerStatus getStatus();

    /**
     * Returns the current media cursor position in percents.
     *
     * @return The current media cursor position.
     */
    int getPercentage();

    /**
     * Returns the current cursor position in milliseconds
     *
     * @return The current media cursor position
     */
    int getCurrentPosition();

    int getTotalDuration();

    /**
     * Sets a listener for on play event
     *
     * @param onPlayListener The listener object.
     */
    void setOnPlayListener(OnPlayListener onPlayListener);


    void setOnStopListener(OnStopListener onStopListener);


    void setOnPauseListener(OnPauseListener onPauseListener);


    void setOnCompleteListener(OnCompleteListener onCompleteListener);


    void setOnErrorListener(OnErrorListener onErrorListener);
}
