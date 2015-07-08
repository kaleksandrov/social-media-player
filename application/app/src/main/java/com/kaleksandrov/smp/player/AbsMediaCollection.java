package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.model.Song;

import java.util.Iterator;

abstract class AbsMediaCollection implements MediaCollection {

    protected class MediaCollectionIterator implements Iterator<Song> {
        private Iterator<Song> mIterator;

        public MediaCollectionIterator(Iterator<Song> iterator) {
            mIterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return mIterator.hasNext();
        }

        @Override
        public Song next() {
            return mIterator.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
