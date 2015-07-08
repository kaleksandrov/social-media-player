package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.util.ValidationUtils;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

class MediaQueueImpl extends AbsMediaCollection implements MediaQueue {

    private SortedMap<Song, Integer> mQueue = new TreeMap<Song, Integer>();

    @Override
    public int size() {
        return mQueue.size();
    }

    @Override
    public boolean isEmpty() {
        return mQueue.isEmpty();
    }

    @Override
    public Song getByPosition(int index) {
        // TODO [kaleksandrov] Redesign this
        throw new UnsupportedOperationException("This is not supported!");
    }

    @Override
    public Integer getCount(Song song) {
        ValidationUtils.notNull(song);

        return mQueue.get(song);
    }

    @Override
    public void enqueue(Song song) {
        ValidationUtils.notNull(song);
        Integer count = mQueue.get(song);
        if (count == null) {
            count = 0;
        }
        mQueue.put(song, ++count);
    }

    @Override
    public Song dequeue() {
        Song song = mQueue.firstKey();
        Integer count = mQueue.get(song);
        if (count == 1) {
            mQueue.remove(song);
        } else {
            mQueue.put(song, --count);
        }

        return song;
    }

    @Override
    public void remove(int index) {
        mQueue.remove(index);
    }

    @Override
    public Iterator<Song> iterator() {
        // TODO[kaleksandrov] Redesign this
        throw new UnsupportedOperationException("This is not supported!");
    }
}
