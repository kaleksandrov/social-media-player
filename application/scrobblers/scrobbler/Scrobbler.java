package org.freesoft.socialmediaplayer.scrobbler;

import org.freesoft.socialmediaplayer.player.Media;

/**
 * Interface exposing functionality for scrobbling songs.
 * 
 * @author kaleksandrov
 * 
 */
public interface Scrobbler {

	/**
	 * Tracks that the given media is played.
	 * 
	 * @param media
	 *            The media to be tracked.
	 */
	void played(final Media media);

	/**
	 * Tracks that the given media is stopped.
	 * 
	 * @param media
	 *            The media to be tracked.
	 */
	void stopped(final Media media);

	/**
	 * Tracks that the given media is paused.
	 * 
	 * @param media
	 *            The media to be tracked.
	 */
	void paused(final Media media);

	/**
	 * Tracks that the given media is resumed.
	 * 
	 * @param media
	 *            The media to be tracked.
	 */
	void resumed(final Media media);

	/**
	 * Enables/Disables media scrobbling.
	 * 
	 * @param isEnabled
	 *            Is the scrobbling enabled.
	 */
	void enable(final boolean isEnabled);

	/**
	 * Gets if the scrobbling is enabled.
	 * 
	 * @return <b><code>true</code></b> if the scrobbling is enabled, otherwise - <b>
	 *         <code>false</code></b>
	 */
	boolean isEnabled();
}
