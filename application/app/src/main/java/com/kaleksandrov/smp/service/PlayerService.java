package com.kaleksandrov.smp.service;

import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.FairPlayer.PlayerStatus;

public interface PlayerService {

    void play(int index);

    void play();

    void pause();

    void unpause();

    void stop();

    void toggle();

    void seek(int milliseconds);

    void fastForward();

    void rewind();

    int getCurrentProgress();

    Song getCurrentSong();

    PlayerStatus getStatus();

    State getCurrentState();

    void next();

    void previous();

    public class State {
        private final Song mSong;
        private final int mCurrentProgress;
        private final PlayerStatus mStatus;

        public State(Song song,
                     int currentProgress,
                     PlayerStatus status) {
            mSong = song;
            mCurrentProgress = currentProgress;
            mStatus = status;
        }

        public Song getSong() {
            return mSong;
        }

        public int getCurrentProgress() {
            return mCurrentProgress;
        }

        public PlayerStatus getStatus() {
            return mStatus;
        }
    }
}
