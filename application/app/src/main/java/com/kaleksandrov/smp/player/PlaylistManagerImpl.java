package com.kaleksandrov.smp.player;

import android.content.Context;

import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.util.ValidationUtils;

import java.util.Collection;
import java.util.List;

public class PlaylistManagerImpl implements PlaylistManager {

	/* Members */

    private final Context mContext;

	/* Constructors */

    public PlaylistManagerImpl(final Context context) {
        ValidationUtils.notNull(context);

        mContext = context.getApplicationContext();
    }


    @Override
    public void save(MediaList playlist) {

    }

    @Override
    public MediaList create(Collection<Song> songs) {
        return new MediaListImpl(songs);
    }

    @Override
    public MediaList get(int id) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public List<MediaList> getAll() {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void update(MediaList playlist) {
        throw new UnsupportedOperationException("Not implemented!");
    }

    @Override
    public void delete(MediaList playlist) {
        throw new UnsupportedOperationException("Not implemented!");
    }
}
