package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.model.Song;

import java.util.List;

/**
 * Inteface describing the basic Playlist functionality.
 *
 * @author kaleksandrov
 *         <p/>
 *         TODO Make it Generic
 */
public interface MediaList extends MediaCollection {

    Song getByPosition(int index);

    int getPosition();

    int getPosition(Song media);

    List<Song> getSongs();

    MediaQueue getQueue();

    int size();

    boolean isRepeat();

    void setIsRepeat(boolean isRepeat);

    boolean isShuffle();

    void setIsShuffle(boolean isShuffle);
}
