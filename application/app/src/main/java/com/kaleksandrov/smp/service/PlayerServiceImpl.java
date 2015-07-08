package com.kaleksandrov.smp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.FairPlayer;
import com.kaleksandrov.smp.player.FairPlayer.PlayerStatus;
import com.kaleksandrov.smp.player.OSMediaPlayerWrapper;
import com.kaleksandrov.smp.ui.activity.PlayerActivity;
import com.kaleksandrov.smp.ui.img.CoverManager;
import com.kaleksandrov.smp.util.StringUtils;
import com.kaleksandrov.smp.util.ValidationUtils;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

/**
 * Android {@link Service} class that plays music in the background. Uses
 * {@link OSMediaPlayerWrapper} for playing the audio.
 *
 * @author kaleksandrov
 */
public class PlayerServiceImpl extends Service implements PlayerService {

	/* Constants */

    private static final String LOG_TAG = PlayerServiceImpl.class.getSimpleName();

    public static final String EXTRA_COMMAND = PlayerServiceImpl.class.getSimpleName() + ":command";
    private static final int NOTIFICATION_ID = 7454;

    private static final int INTENT_PLAYER_ACTIVITY = 7743;
    private static final int INTENT_TOGGLE = 5234;
    private static final int INTENT_NEXT = 98364;
    private static final int INTENT_PREVIOUS = 82734;

    private static final String KEY_COMMAND = "command";
    private static final String KEY_DATA = "data";
    private static final String SERVICE_THREAD_NAME = PlayerServiceImpl.class.getName()
            + ":worker-thread";

	/* Private members */

    private Notification.Builder mNotificationBuilder;

    private FairPlayer mCorePlayer;
    private CoverManager mCoverManager;

    private NotificationUpdater mUpdater;
    private boolean mIsForeground;
    private String mOldAlbumUri;

    private Thread mUiThread;
    private Handler mUiHandler;
    private Handler mServiceHandler;

    private final PlayerServiceProxy mProxy = new PlayerServiceProxy();
    private final IBinder mBinder = new MediaPlayerServiceBinder();

	/* Public methods */

    @Override
    public void onCreate() {
        mUiThread = Thread.currentThread();
        mUiHandler = new Handler(Looper.getMainLooper());

        // Start the service thread.
        HandlerThread thread = new HandlerThread(SERVICE_THREAD_NAME);
        thread.start();
        Looper looper = thread.getLooper();
        mServiceHandler = new ServiceHandler(looper);

        FairPlayerApplication app = (FairPlayerApplication) getApplicationContext();

        mCorePlayer = app.getPlayer();
        Log.d("TTT", "mCorePlayer : " + mCorePlayer);
        mCoverManager = app.getCoverManager();

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        mServiceHandler.getLooper().quit();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        // Check for commands
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Command command = (Command) extras.get(EXTRA_COMMAND);
            if (command != null) {
                executeCommand(command);
            }
        }

        return Service.START_NOT_STICKY;
    }

    /**
     * Executes a command on the player.
     */
    protected void executeCommand(Command command, final Object data) {
        ValidationUtils.notNull(command);
        if (data != null && !(data instanceof Serializable)) {
            throw new IllegalArgumentException("The given data must be Serializable!");
        }

        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_COMMAND, command);
        bundle.putSerializable(KEY_DATA, (Serializable) data);
        message.setData(bundle);
        mServiceHandler.removeCallbacksAndMessages(null);
        mServiceHandler.sendMessage(message);
    }

    /**
     * Executes a command on the player.
     */
    protected void executeCommand(final Command command) {
        executeCommand(command, null);
    }

    /**
     * Create a notification for the service.
     *
     * @return
     */
    private Notification createNotification() {
        Context context = getApplicationContext();

        // Intent for starting the main activity on notification selected
        Intent activityIntent = new Intent(context, PlayerActivity.class);
        PendingIntent pendingActivityIntent = PendingIntent.getActivity(context,
                INTENT_PLAYER_ACTIVITY,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Intent for toggling player state (play/pause) when the notification button is pressed
        Intent toggleIntent = new Intent(context, PlayerServiceImpl.class);
        toggleIntent.putExtra(EXTRA_COMMAND, Command.TOGGLE);
        PendingIntent togglePendingIntent = PendingIntent.getService(context,
                INTENT_TOGGLE,
                toggleIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Intent for playing the next song when the notification button is pressed
        Intent nextIntent = new Intent(context, PlayerServiceImpl.class);
        nextIntent.putExtra(EXTRA_COMMAND, Command.NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(context,
                INTENT_NEXT,
                nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Intent for playing the next song when the notification button is pressed
        Intent previousIntent = new Intent(context, PlayerServiceImpl.class);
        previousIntent.putExtra(EXTRA_COMMAND, Command.PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getService(context,
                INTENT_PREVIOUS,
                previousIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification. Here we don't set an icon because the loading is done
        // synchronous. Create the notification without the icon and start a background task for
        // updating instead.
        mNotificationBuilder = new Notification.Builder(getApplicationContext());
        mNotificationBuilder.setSmallIcon(R.drawable.ic_launcher);
        mNotificationBuilder.setOnlyAlertOnce(true);
        mNotificationBuilder.setContentIntent(pendingActivityIntent);
        mNotificationBuilder.addAction(R.drawable.ic_action_previous, null, previousPendingIntent);
        mNotificationBuilder.addAction(R.drawable.ic_action_play, null, togglePendingIntent);
        mNotificationBuilder.addAction(R.drawable.ic_action_next, null, nextPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNotificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
            mNotificationBuilder.setStyle(new Notification.MediaStyle().setShowActionsInCompactView(0, 1, 2));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mNotificationBuilder.setShowWhen(false);
        }

        executeLater(new Runnable() {
            @Override
            public void run() {
                updateNotification();
            }
        });

        return mNotificationBuilder.build();
    }

    private void executeLater(Runnable runnable) {
        mUiHandler.post(runnable);
    }

    private boolean isUiThread() {
        return Thread.currentThread() == mUiThread;
    }

    /**
     * Update the existing service notification.
     */
    private void updateNotification() {
        if(mNotificationBuilder == null) {
            return;
        }

        mUpdater = new NotificationUpdater();
        mUpdater.update();
    }

    /**
     * Starts the service in foreground mode with a proper notification.
     */
    private void startForegroundService() {
        if (!mIsForeground) {
            final Notification notification = createNotification();
            startForeground(NOTIFICATION_ID, notification);
            mIsForeground = true;
        }
    }

    private void stopForegroundService() {
        if (mIsForeground) {
            stopForeground(false);
            mIsForeground = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void play(int index) {
        mCorePlayer.play(index);
        startForegroundService();
    }

    @Override
    public void play() {
        mCorePlayer.play();
        startForegroundService();
    }

    @Override
    public void pause() {
        mCorePlayer.pause();
        stopForegroundService();
    }

    @Override
    public void unpause() {
        mCorePlayer.unpause();
        startForegroundService();
    }

    @Override
    public void stop() {
        mCorePlayer.stop();
        stopForegroundService();
    }

    @Override
    public void toggle() {
        mCorePlayer.toggle();
        if (mCorePlayer.getStatus() == PlayerStatus.PLAYING) {
            startForegroundService();
        } else {
            stopForegroundService();
        }
    }

    @Override
    public void seek(int milliseconds) {
        mCorePlayer.seek(milliseconds);
    }

    @Override
    public int getCurrentProgress() {
        return mCorePlayer.getCurrentProgress();
    }

    @Override
    public Song getCurrentSong() {
        return mCorePlayer.getCurrentSong();
    }

    @Override
    public PlayerStatus getStatus() {
        return mCorePlayer.getStatus();
    }

    @Override
    public void next() {
        mCorePlayer.next();
    }

    @Override
    public void previous() {
        mCorePlayer.previous();
    }

    @Override
    public void fastForward() {
        mCorePlayer.fastForward();
    }

    @Override
    public void rewind() {
        mCorePlayer.rewind();
    }

    @Override
    public State getCurrentState() {
        return new State(getCurrentSong(), getCurrentProgress(), getStatus());
    }

    /**
     * Interfaces holding constants about the {@link OSMediaPlayerWrapper} commands
     *
     * @author kaleksandrov
     */
    private static enum Command {
        PLAY,
        PAUSE,
        UNPAUSE,
        STOP,
        TOGGLE,
        SEEK,
        NEXT,
        PREVIOUS;
    }

    public class MediaPlayerServiceBinder extends Binder {
        public PlayerService getService() {
            return mProxy;
        }
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(final Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Log.d(LOG_TAG, "Executing command from thread : " + Thread.currentThread().getName());

            Bundle bundle = msg.getData();
            Command command = (Command) bundle.getSerializable(KEY_COMMAND);
            Object data = bundle.getSerializable(KEY_DATA);

            executeCommand(command, data);

            final State state = new State(getCurrentSong(),
                    getCurrentProgress(),
                    getStatus());

            if (isUiThread()) {
                doNotify(state);
            } else {
                executeLater(new Runnable() {
                    @Override
                    public void run() {
                        doNotify(state);
                    }
                });
            }
        }

        private void doNotify(State state) {
            mProxy.setIsChaged();
            mProxy.notifyObservers(state);
            updateNotification();
        }

        private void executeCommand(final Command playerCommand, final Object data) {
            switch (playerCommand) {

                case PLAY: {
                    if (data == null) {
                        play();
                    } else {
                        play((Integer) data);
                    }
                    break;
                }

                case PAUSE: {
                    pause();
                    break;
                }

                case UNPAUSE: {
                    unpause();
                    break;
                }

                case STOP: {
                    stop();
                    break;
                }

                case TOGGLE: {
                    toggle();
                    break;
                }

                case SEEK: {
                    seek((Integer) data);
                    break;
                }

                case PREVIOUS: {
                    previous();
                    break;
                }

                case NEXT: {
                    next();
                    break;
                }

                default: {
                    break;
                }
            }
        }
    }

    private class PlayerServiceProxy extends Observable implements PlayerService, ObservableService {

        @Override
        public void play(int index) {
            executeCommand(Command.PLAY, index);
        }

        @Override
        public void play() {
            executeCommand(Command.PLAY);
        }

        @Override
        public void pause() {
            executeCommand(Command.PAUSE);
        }

        @Override
        public void unpause() {
            executeCommand(Command.UNPAUSE);
        }

        @Override
        public void stop() {
            executeCommand(Command.STOP);
        }

        @Override
        public void toggle() {
            executeCommand(Command.TOGGLE);
        }

        @Override
        public void seek(int milliseconds) {
            executeCommand(Command.SEEK, milliseconds);
        }

        @Override
        public void fastForward() {
            // TODO Auto-generated method stub
        }

        @Override
        public void rewind() {
            // TODO Auto-generated method stub
        }

        @Override
        public int getCurrentProgress() {
            return PlayerServiceImpl.this.getCurrentProgress();
        }

        @Override
        public Song getCurrentSong() {
            return PlayerServiceImpl.this.getCurrentSong();
        }

        @Override
        public PlayerStatus getStatus() {
            return PlayerServiceImpl.this.getStatus();
        }

        @Override
        public void next() {
            executeCommand(Command.NEXT);
        }

        @Override
        public void previous() {
            executeCommand(Command.PREVIOUS);
        }

        @Override
        public void registerObserver(Observer observer) {
            addObserver(observer);
        }

        @Override
        public void unregisterObserver(Observer observer) {
            deleteObserver(observer);
        }

        private void setIsChaged() {
            setChanged();
        }

        @Override
        public State getCurrentState() {
            return PlayerServiceImpl.this.getCurrentState();
        }
    }

    private class NotificationUpdater extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Song currentSong = mCorePlayer.getCurrentSong();

            String coverPath = currentSong.getAlbum().getCoverPath();
            if (mOldAlbumUri != coverPath || StringUtils.isEmpty(coverPath)) {
                mOldAlbumUri = coverPath;
                mCoverManager.loadInto(coverPath, mNotificationBuilder);
            }
            mNotificationBuilder.setContentTitle(currentSong.getName());
            mNotificationBuilder.setContentText(currentSong.getAlbum().getArtist().getName());

			/* Update the notification */
            Notification notification = mNotificationBuilder.build();
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_ID, notification);

            return null;
        }

        private void stopSafely() {
            if (getStatus() == Status.RUNNING) {
                cancel(true);
            }
        }

        public void update() {
            stopSafely();
            execute(null, null);
        }
    }

}