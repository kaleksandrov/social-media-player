package com.kaleksandrov.smp.ui.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.kaleksandrov.smp.player.FairPlayer;
import com.kaleksandrov.smp.service.ObservableService;
import com.kaleksandrov.smp.service.PlayerService;
import com.kaleksandrov.smp.service.PlayerServiceImpl;
import com.kaleksandrov.smp.util.ValidationUtils;

import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Observer;

/**
 * PLays the role of a mediator between the activities and the service. Encapsulates the logic over the service binding.
 */
public class MediaPlayerClient extends Observable {

    private WeakReference<Activity> mActivityRef;
    private final ServiceConnection mConnection = new MediaPlayerServiceConnection();
    private final MediaPlayerServiceObserver mPlayerObserver = new MediaPlayerServiceObserver();
    private PlayerService mPlayer;

    private boolean mIsBound;

    public MediaPlayerClient(Activity activity) {
        ValidationUtils.notNull(activity);
        mActivityRef = new WeakReference<>(activity);
    }

    public void connect() {
        Activity activity = mActivityRef.get();
        if (activity == null) {
            return;
        }

        if (!mIsBound) {
            activity.bindService(new Intent(activity, PlayerServiceImpl.class),
                    mConnection,
                    Context.BIND_AUTO_CREATE);
            mIsBound = true;
        }
    }

    public void disconnect() {
        if (mIsBound) {
            if (mPlayer != null) {
                unregister();
            }

            Activity activity = mActivityRef.get();
            if (activity != null) {
                activity.unbindService(mConnection);
            }
            mIsBound = false;
        }
    }

    private boolean mResetPlayback;

    public void start(boolean resetPlayback) {
        mResetPlayback = resetPlayback;

        Activity activity = mActivityRef.get();
        if (activity == null) {
            return;
        }

        Intent serviceIntent = new Intent(activity, PlayerServiceImpl.class);
        activity.startService(serviceIntent);
    }

    public void play() {
        mPlayer.play();
    }

    public void play(int index) {
        mPlayer.play(index);
    }

    public void pause() {
        mPlayer.pause();
    }

    public void unpause() {
        mPlayer.unpause();
    }

    public FairPlayer.PlayerStatus getStatus() {
        return mPlayer.getStatus();
    }

    public void next() {
        mPlayer.next();
    }

    public void previous() {
        mPlayer.previous();
    }

    public void seek(int milliseconds) {
        mPlayer.seek(milliseconds);
    }

    public int getCurrentProgress() {
        return mPlayer.getCurrentProgress();
    }

    /**
     * Sends a message to the {@link PlayerServiceImpl} for registering the current client.
     */
    private void register() {
        ((ObservableService) mPlayer).registerObserver(mPlayerObserver);
    }

    /**
     * Sends a message to the {@link PlayerServiceImpl} for unregistering the current client.
     */
    private void unregister() {
        ((ObservableService) mPlayer).unregisterObserver(mPlayerObserver);
    }

    /**
     * Listens for changes in the media playback from the {@link PlayerService}.
     *
     * @author kaleksandrov
     */
    private class MediaPlayerServiceObserver implements Observer {

        @Override
        public void update(Observable observable, Object data) {
            setChanged();
            notifyObservers(data);
        }
    }

    /**
     * {@link ServiceConnection} instance that manages the connection between the current
     * {@link Activity} and {@link PlayerServiceImpl} instance.
     */
    private class MediaPlayerServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName className, IBinder binder) {

            PlayerServiceImpl.MediaPlayerServiceBinder serviceBinder = (PlayerServiceImpl.MediaPlayerServiceBinder) binder;
            mPlayer = serviceBinder.getService();

            MediaPlayerClient.this.register();
            if(mResetPlayback) {
                mPlayer.play(0);
                mResetPlayback = false;
            }

            setChanged();
            PlayerService.State state = mPlayer.getCurrentState();
            notifyObservers(state);
        }

        public void onServiceDisconnected(ComponentName className) {
            mPlayer = null;
        }
    }
}
