package com.kaleksandrov.smp.player;

import com.kaleksandrov.smp.model.Song;

public interface MediaCollection extends Iterable<Song> {
	int size();

	Song getByPosition(int index);

	boolean isEmpty();
}
