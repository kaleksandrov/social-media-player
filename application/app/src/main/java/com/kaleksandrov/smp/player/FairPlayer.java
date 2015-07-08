package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.model.Song;

public interface FairPlayer {

    public enum PlayerStatus {
        PLAYING,
        PAUSED,
        STOPED;
    }

    void play();

    void play(int index);

    void pause();

    void unpause();

    void stop();

    void toggle();

    void seek(int milliseconds);

    void fastForward();

    void rewind();

    int getCurrentProgress();

    Song next();

    Song previous();

    Song getCurrentSong();

    PlayerStatus getStatus();

    void setPlaylist(MediaList playlist);
}
