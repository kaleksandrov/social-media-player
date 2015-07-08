package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.util.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of the {@link MediaList} interface.
 *
 * @author kaleksandrov
 */
class MediaListImpl extends AbsMediaCollection implements MediaListInternal {

    private int mPosition;
    private MediaQueue mQueue;

    private List<Song> mMediaList;

    private boolean mIsShuffle;
    private boolean mIsRepeat;

    private static final Comparator<Song> sSongComparator = new Comparator<Song>() {
        private static final int LOWER = -1;
        private static final int GREATER = 1;

        @Override
        public int compare(Song first, Song second) {
            if (first == null) {
                return LOWER;
            } else if (second == null) {
                return GREATER;
            }
            return first.getUri().compareTo(second.getUri());
        }
    };

    public MediaListImpl() {
        mMediaList = new ArrayList<>();
        mQueue = new MediaQueueImpl();
    }

    public MediaListImpl(Collection<Song> songs) {
        this();

        mMediaList.addAll(songs);

        Collections.sort(mMediaList, sSongComparator);
    }

	/* Public API */

    @Override
    public boolean isRepeat() {
        return mIsRepeat;
    }

    @Override
    public void setIsRepeat(boolean isRepeat) {
        mIsRepeat = isRepeat;
    }

    // TODO [kaleksandrov] change the API to changeOrder(Order.RANDOM/Order.NATURAL) instead of setting flags
    @Override
    public boolean isShuffle() {
        return mIsShuffle;
    }

    @Override
    public void setIsShuffle(boolean isShuffle) {
        if (mIsShuffle != isShuffle) {
            mIsShuffle = isShuffle;

            if (mIsShuffle) {
                Collections.shuffle(mMediaList);
            } else {
                Collections.sort(mMediaList, sSongComparator);
            }
            recalculateCurrentIndex();
        }
    }

    @Override
    public int size() {
        return mMediaList.size();
    }

    @Override
    public boolean isEmpty() {
        return mMediaList.isEmpty();
    }

    public MediaQueue getQueue() {
        return mQueue;
    }

    @Override
    public Iterator<Song> iterator() {
        return new MediaCollectionIterator(mMediaList.iterator());
    }

    @Override
    public Song getByPosition(int index) {
        ValidationUtils.inRange(index, 0, size() - 1);
        if (isEmpty()) {
            return null;
        }

        return mMediaList.get(index);
    }

    @Override
    public int getPosition(Song song) {
        return mMediaList.indexOf(song);
    }

    @Override
    public List<Song> getSongs() {
        return Collections.unmodifiableList(mMediaList);
    }

    @Override
    public int getPosition() {
        return mPosition;
    }

	/* Internal API */

    @Override
    public Song moveTo(int originalIndex) {
        ValidationUtils.inRange(originalIndex, 0, size() - 1);

        mPosition = originalIndex;
        recalculateCurrentIndex();

        Song next = getCurrent();
        return next;
    }

    @Override
    public Song getCurrent() {
        return getByPosition(mPosition);
    }

    @Override
    public Song previous() {
        Song media = null;

        if (0 < mPosition) {
            media = mMediaList.get(--mPosition);
        } else if (mIsRepeat) {
            mPosition = size() - 1;
            media = mMediaList.get(mPosition);
        }

        return media;
    }

    @Override
    public Song next() {
        Song media = null;

        if (mQueue.isEmpty()) {
            if (mPosition < size() - 1) {
                media = mMediaList.get(++mPosition);
            } else if (mIsRepeat) {
                mPosition = 0;
                media = mMediaList.get(mPosition);
            }
        } else {
            media = mQueue.dequeue();
            mPosition = mMediaList.indexOf(media);
        }

        return media;
    }

	/* Private methods */

    private void recalculateCurrentIndex() {
        Song song = mMediaList.get(mPosition);
        mPosition = mMediaList.indexOf(song);
    }
}
