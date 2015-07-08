package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.model.Song;

public interface MediaQueue extends MediaCollection {

    void enqueue(Song song);

    Song dequeue();

    void remove(int index);

    Integer getCount(Song song);

}
