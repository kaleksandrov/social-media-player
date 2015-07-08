package com.kaleksandrov.smp.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.FairPlayer.PlayerStatus;
import com.kaleksandrov.smp.player.MediaList;
import com.kaleksandrov.smp.service.PlayerService.State;
import com.kaleksandrov.smp.ui.fragment.CoverArtFragment;
import com.kaleksandrov.smp.ui.fragment.PlaylistFragment;
import com.kaleksandrov.smp.ui.fragment.ZoomOutPageTransformer;
import com.kaleksandrov.smp.ui.view.StateButton;
import com.kaleksandrov.smp.ui.view.StateButton.OnStateChangeListener;
import com.kaleksandrov.smp.util.FormatUtils;
import com.kaleksandrov.smp.util.StringUtils;

import java.util.Observable;
import java.util.Observer;

/**
 * Tha main screen of the application. Show information about the song currently played and give the
 * user the ability go control the playback.
 *
 * @author kaleksandrov
 */
public class PlayerActivity extends AppCompatActivity implements Observer, PlaylistFragment.OnMediaClickListener {

	/* Constants */

    public static final String EXTRA_RESET_PLAYBACK = "resetPlayback";

    private static final String LOG_TAG = PlayerActivity.class.getSimpleName();

    private static final String TOGGLE_STATE_PLAY = "play";
    private static final String TOGGLE_STATE_PAUSE = "pause";

    private static final String REPEAT_STATE_ON = "on";
    private static final String REPEAT_STATE_OFF = "ff";

    private static final String SHUFFLE_STATE_ON = "on";
    private static final String SHUFFLE_STATE_OFF = "ff";

    private static final int PROGRESS_UPDATE_INTERVAL = 1000; // One second

    private static final String SEND_INTENT_MIME_TYPE = "text/plain";
    private static final String SEND_INTENT_TEXT = "Listening to %s - %s";

	/* Simple Views */

    private StateButton mToggleButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private StateButton mRepeatButton;
    private StateButton mShuffleButton;
    private SeekBar mProgressBar;
    private TextView mElapsedTimeText;
    private TextView mTotalTimeText;
    private Toolbar mToolbar;

    private PlaylistFragment mPlaylistFragment;

	/* Cover pager */

    private ViewPager mCoverArtPager;
    private CoverArtPagerAdapter mCoverArtAdapter;

	/* Progress Updater */

    private Handler mProgressUpdater = new Handler();
    private Runnable mUpdateTimeTask = new ProgressUpdateRunnable();

	/* Player Service */

    private MediaPlayerClient mMediaPlayerClient;
    private MediaList mPlaylist;
    private Song mCurrentSong;

	/* Public methods */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                String shareTitle = getString(R.string.share_title);
                String shareSubject = getString(R.string.share_subject);

                String shareMessage = null;
                if (mCurrentSong == null) {
                    shareMessage = getString(R.string.share_text_default);
                } else {
                    shareMessage = getString(R.string.share_text,
                            mCurrentSong.getName(),
                            mCurrentSong.getAlbum().getArtist().getName());
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType(SEND_INTENT_MIME_TYPE);
                intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);

                startActivity(Intent.createChooser(intent, shareTitle));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

	/* Protected methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Bundle extras = getIntent().getExtras();
        boolean resetPlayback = false;
        if(extras != null && extras.containsKey(EXTRA_RESET_PLAYBACK)) {
            resetPlayback = extras.getBoolean(EXTRA_RESET_PLAYBACK, false);
        }

		/* Init & Configure */
        initMembers();
        attachListeners();

		/* Start the media player service and connect to it */
        startMediaPlayerService(resetPlayback);
    }

    @Override
    protected void onResume() {
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mMediaPlayerClient.connect();
        startProgressBarUpdater();
        super.onResume();
    }

    @Override
    protected void onPause() {
        stopProgressBarUpdater();
        mMediaPlayerClient.disconnect();
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

	/* Private methods */

    /**
     * Initializes the view members.
     */
    private void initMembers() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPlaylist = ((FairPlayerApplication) getApplication()).getPlaylist();
        mNextButton = (ImageButton) findViewById(R.id.next);
        mPreviousButton = (ImageButton) findViewById(R.id.prev);
        mProgressBar = (SeekBar) findViewById(R.id.seekBar);
        mProgressBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        mProgressBar.getThumb().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        mElapsedTimeText = (TextView) findViewById(R.id.currentTime);
        mTotalTimeText = (TextView) findViewById(R.id.totalTime);

        mPlaylistFragment = (PlaylistFragment) getSupportFragmentManager().findFragmentById(R.id.playlist);

        mToggleButton = (StateButton) findViewById(R.id.toggle);
        mToggleButton.addState(TOGGLE_STATE_PAUSE,
                R.string.state_paused,
                R.drawable.ic_action_pause);
        mToggleButton.addState(TOGGLE_STATE_PLAY, R.string.state_playing, R.drawable.ic_action_play);

        mRepeatButton = (StateButton) findViewById(R.id.repeat);
        mRepeatButton.addState(REPEAT_STATE_ON, R.string.state_on, R.drawable.ic_action_repeat);
        mRepeatButton.addState(REPEAT_STATE_OFF, R.string.state_off, R.drawable.ic_action_repeat_off);
        if (mPlaylist.isRepeat()) {
            mRepeatButton.goTo(REPEAT_STATE_ON);
        } else {
            mRepeatButton.goTo(REPEAT_STATE_OFF);
        }

        mShuffleButton = (StateButton) findViewById(R.id.shuffle);
        mShuffleButton.addState(SHUFFLE_STATE_ON, R.string.state_on, R.drawable.ic_action_shuffle);
        mShuffleButton.addState(SHUFFLE_STATE_OFF, R.string.state_off, R.drawable.ic_action_shuffle_off);
        if (mPlaylist.isShuffle()) {
            mShuffleButton.goTo(SHUFFLE_STATE_ON);
        } else {
            mShuffleButton.goTo(SHUFFLE_STATE_OFF);
        }

        mCoverArtPager = (ViewPager) findViewById(R.id.pager);
        mCoverArtAdapter = new CoverArtPagerAdapter(getSupportFragmentManager());
        mCoverArtPager.setAdapter(mCoverArtAdapter);
        mCoverArtPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    /**
     * Attaches the listener objects to the views.
     */
    private void attachListeners() {
        mToggleButton.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public boolean onStateChange(String oldState, String newState) {
                if (TOGGLE_STATE_PAUSE.equals(oldState) && TOGGLE_STATE_PLAY.equals(newState)) {
                    mMediaPlayerClient.pause();
                } else if (TOGGLE_STATE_PLAY.equals(oldState)
                        && TOGGLE_STATE_PAUSE.equals(newState)) {
                    if (mMediaPlayerClient.getStatus() == PlayerStatus.PAUSED) {
                        mMediaPlayerClient.unpause();
                    } else if (mMediaPlayerClient.getStatus() == PlayerStatus.STOPED) {
                        mMediaPlayerClient.play();
                    }
                }

                return false;
            }
        });

        mRepeatButton.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public boolean onStateChange(String oldState, String newState) {
                if (REPEAT_STATE_ON.equals(oldState) && REPEAT_STATE_OFF.equals(newState)) {
                    mPlaylist.setIsRepeat(false);
                } else if (REPEAT_STATE_OFF.equals(oldState) && REPEAT_STATE_ON.equals(newState)) {
                    mPlaylist.setIsRepeat(true);
                }

                return false;
            }
        });

        mShuffleButton.setOnStateChangeListener(new OnStateChangeListener() {

            @Override
            public boolean onStateChange(String oldState, String newState) {
                if (SHUFFLE_STATE_ON.equals(oldState) && SHUFFLE_STATE_OFF.equals(newState)) {
                    mPlaylist.setIsShuffle(false);
                } else if (SHUFFLE_STATE_OFF.equals(oldState) && SHUFFLE_STATE_ON.equals(newState)) {
                    mPlaylist.setIsShuffle(true);
                }

                mPlaylistFragment.notifyPlaylistChanged();

                return false;
            }
        });

        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayerClient.next();
            }
        });

        mPreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayerClient.previous();
            }
        });

        mProgressBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mProgressUpdater.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mProgressUpdater.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayerClient.seek(progress);
                }
            }
        });

        mCoverArtPager.setOnPageChangeListener(new OnPageChangeListener() {

            private int mCurrentPageIndex;
            private boolean mIsPageScrolling;
            private int mLastState;

            @Override
            public void onPageSelected(int pageIndex) {
                if (mIsPageScrolling) {
                    mIsPageScrolling = false;

                    if (pageIndex > mCurrentPageIndex) {
                        mMediaPlayerClient.next();
                    } else {
                        mMediaPlayerClient.previous();
                    }
                }

                mCurrentPageIndex = pageIndex;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // nothing
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (ViewPager.SCROLL_STATE_DRAGGING == mLastState
                        && ViewPager.SCROLL_STATE_SETTLING == state) {
                    mIsPageScrolling = true;
                } else {
                    mIsPageScrolling = false;
                }

                mLastState = state;
            }
        });
    }

    private void startProgressBarUpdater() {
        mProgressUpdater.removeCallbacks(mUpdateTimeTask);
        mProgressUpdater.postDelayed(mUpdateTimeTask, PROGRESS_UPDATE_INTERVAL);
    }

    private void stopProgressBarUpdater() {
        mProgressUpdater.removeCallbacks(mUpdateTimeTask);
    }

    private void startMediaPlayerService(boolean resetPlayback) {
        mMediaPlayerClient = new MediaPlayerClient(this);
        mMediaPlayerClient.addObserver(this);
        mMediaPlayerClient.start(resetPlayback);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (!(data instanceof State)) {
            return;
        }

        State state = (State) data;
        mCurrentSong = state.getSong();

        if (mCurrentSong == null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(StringUtils.EMPTY);
            actionBar.setSubtitle(StringUtils.EMPTY);

            mProgressBar.setProgress(0);
            mProgressBar.setMax(0);

            mElapsedTimeText.setText(StringUtils.EMPTY);
            mTotalTimeText.setText(StringUtils.EMPTY);

            mToggleButton.goTo(TOGGLE_STATE_PLAY);
            stopProgressBarUpdater();
        } else {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(mCurrentSong.getName());
            actionBar.setSubtitle(mCurrentSong.getAlbum().getArtist().getName());

            mProgressBar.setProgress(state.getCurrentProgress());
            mProgressBar.setMax(mCurrentSong.getDuration());

            mElapsedTimeText.setText(FormatUtils.formatMinutesAndSeconds(state.getCurrentProgress()));
            mTotalTimeText.setText(FormatUtils.formatMinutesAndSeconds(mCurrentSong.getDuration()));

            mCoverArtPager.setCurrentItem(mPlaylist.getPosition(), true);

            mPlaylistFragment.moveToCurrent();

            PlayerStatus status = state.getStatus();
            switch (status) {
                case PAUSED: {
                    mToggleButton.goTo(TOGGLE_STATE_PLAY);
                    stopProgressBarUpdater();
                    break;
                }

                case PLAYING: {
                    mToggleButton.goTo(TOGGLE_STATE_PAUSE);
                    startProgressBarUpdater();
                    break;
                }

                case STOPED: {
                    mToggleButton.goTo(TOGGLE_STATE_PLAY);
                    stopProgressBarUpdater();
                    break;
                }

                default: {
                    Log.w(LOG_TAG, "Unknown status type : " + status);
                    break;
                }
            }
        }
    }

    @Override
    public void onMediaClicked(int mediaId) {
        mMediaPlayerClient.play(mediaId);

    }

	/* Nested classes */

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in sequence.
     */
    private class CoverArtPagerAdapter extends FragmentStatePagerAdapter {

        public CoverArtPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Song song = mPlaylist.getByPosition(position);
            CoverArtFragment fragment = CoverArtFragment.newInstance(song.getAlbum().getCoverPath());
            return fragment;
        }

        @Override
        public int getCount() {
            return mPlaylist.size();
        }
    }

    private class ProgressUpdateRunnable implements Runnable {
        public void run() {
            int currentDuration = mMediaPlayerClient.getCurrentProgress();

            Log.d(LOG_TAG, "Current duration : " + currentDuration);

            mProgressBar.setProgress(currentDuration);
            mElapsedTimeText.setText(FormatUtils.formatMinutesAndSeconds(currentDuration));

            mProgressUpdater.postDelayed(mUpdateTimeTask, PROGRESS_UPDATE_INTERVAL);
        }
    }
}