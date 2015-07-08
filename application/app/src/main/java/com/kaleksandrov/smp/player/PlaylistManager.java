package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.model.Song;

import java.util.Collection;
import java.util.List;

public interface PlaylistManager {

    void save(MediaList playlist);

    MediaList create(Collection<Song> songs);

    MediaList get(int id);

    List<MediaList> getAll();

    void update(MediaList playlist);

    void delete(MediaList playlist);
}
