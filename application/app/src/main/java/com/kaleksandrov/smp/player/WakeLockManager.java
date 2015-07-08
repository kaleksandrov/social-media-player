package com.kaleksandrov.smp.player;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * Singleton class that manages application wake locks.
 * 
 * @author kaleksandrov
 * 
 */
class WakeLockManager {

	/**
	 * Different types of locks.
	 * 
	 * @author kaleksandrov
	 * 
	 */
	public static enum LockType {
		FULL,
		PARTIAL,
		SCREEN_BRIGHT,
		SCREEN_DIM;
	}

	private static final String WAKE_LOCK_TAG = WakeLockManager.class.getName();

	private Context mContext;
	private Map<String, WakeLock> mLocks;

	public WakeLockManager(final Context context) {
		// TODO Validate
		mContext = context.getApplicationContext();
		mLocks = new HashMap<String, PowerManager.WakeLock>();
	}

	/**
	 * Synchronized method that acquires a new partial lock. The lock must be identified by a unique
	 * key.
	 * 
	 * @param key
	 *            A unique lock key.
	 * @return <b><code>true</b></code> if the lock is gained, otherwise -<b> <code>false</b></code>
	 *         .
	 */
	public synchronized boolean acquireLock(final String key) {
		return acquireLock(key, LockType.PARTIAL);
	}

	/**
	 * Synchronized method that acquires a new lock. The lock must be identified by a unique key.
	 * 
	 * @param key
	 *            A unique lock key.
	 * @param type
	 *            The type of the lock to be acquired.
	 * @return <b><code>true</b></code> if the lock is gained, otherwise -<b> <code>false</b></code>
	 *         .
	 */
	public boolean acquireLock(final String key, final LockType type) {
		synchronized (mLocks) {
			if (mLocks.containsKey(key)) {
				return false;
			} else {
				final PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
				final WakeLock lock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
				lock.acquire();
				mLocks.put(key, lock);

				return true;
			}
		}
	}

	/**
	 * Synchronized method that releases a lock.
	 * 
	 * @param key
	 *            A unique lock key.
	 * @return <b><code>true</b></code> if the lock is released, otherwise -<b>
	 *         <code>false</b></code>.
	 */
	public boolean releaseLock(final String key) {
		synchronized (mLocks) {
			if (mLocks.containsKey(key)) {
				mLocks.remove(key).release();

				return true;
			} else {
				return false;
			}
		}
	}
}
