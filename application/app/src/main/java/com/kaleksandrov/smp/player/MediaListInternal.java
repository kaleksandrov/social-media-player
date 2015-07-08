package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.model.Song;

/**
 * Inteface describing the basic Playlist functionality.
 *
 * @author kaleksandrov
 */
interface MediaListInternal extends MediaList {

    Song moveTo(int index);

    Song getCurrent();

    Song previous();

    Song next();
}
