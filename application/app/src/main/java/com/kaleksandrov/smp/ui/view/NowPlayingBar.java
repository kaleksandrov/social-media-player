package com.kaleksandrov.smp.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.FairPlayer;
import com.kaleksandrov.smp.service.PlayerService;
import com.kaleksandrov.smp.ui.activity.MediaPlayerClient;
import com.kaleksandrov.smp.ui.img.CoverManager;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by kaleksandrov on 5/9/15.
 */
public class NowPlayingBar extends RelativeLayout implements Observer {

    private static final String TOGGLE_STATE_PAUSE = "pause";
    private static final String TOGGLE_STATE_PLAY = "play";

    private ImageView mCoverView;
    private TextView mSongView;
    private TextView mArtistView;
    private ImageButton mPreviousButton;
    private StateButton mToggleButton;
    private ImageButton mNextButton;

    private CoverManager mCoverManager;

    private MediaPlayerClient mPlayer;

    public NowPlayingBar(Context context) {
        super(context);
        init(context);
    }

    public NowPlayingBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public NowPlayingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NowPlayingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setMediaPlayerClient(MediaPlayerClient client) {
        mPlayer = client;
        mPlayer.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {
        if (!(data instanceof PlayerService.State)) {
            return;
        }

        PlayerService.State state = (PlayerService.State) data;

        Song song = state.getSong();
        if (song != null) {
            setVisibility(View.VISIBLE);
            mCoverManager.loadInto(song.getAlbum().getCoverPath(), mCoverView);
            mSongView.setText(song.getName());
            mArtistView.setText(song.getAlbum().getArtist().getName());
        } else {
            setVisibility(View.GONE);
        }

        FairPlayer.PlayerStatus status = state.getStatus();
        switch (status) {
            case PAUSED: {
                mToggleButton.goTo(TOGGLE_STATE_PLAY);
                break;
            }

            case PLAYING: {
                mToggleButton.goTo(TOGGLE_STATE_PAUSE);
                break;
            }

            case STOPED: {
                mToggleButton.goTo(TOGGLE_STATE_PLAY);
                break;
            }

            default: {
                break;
            }
        }
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(R.layout.view_now_playing_bar, this, true);

        // Needed to be rendered properly in the designer view of the IDE
        if (isInEditMode()) {
            return;
        }

        FairPlayerApplication app = (FairPlayerApplication) context.getApplicationContext();
        mCoverManager = app.getCoverManager();

        mCoverView = (ImageView) root.findViewById(R.id.cover);
        mSongView = (TextView) root.findViewById(R.id.song);
        mArtistView = (TextView) root.findViewById(R.id.artist);
        mToggleButton = (StateButton) root.findViewById(R.id.toggle);
        mToggleButton.addState(TOGGLE_STATE_PAUSE, R.string.state_paused, R.drawable.ic_action_pause);
        mToggleButton.addState(TOGGLE_STATE_PLAY, R.string.state_playing, R.drawable.ic_action_play);
        mPreviousButton = (ImageButton) root.findViewById(R.id.previous);
        mNextButton = (ImageButton) root.findViewById(R.id.next);

        mToggleButton.setOnStateChangeListener(new StateButton.OnStateChangeListener() {

            @Override
            public boolean onStateChange(String oldState, String newState) {
                if (TOGGLE_STATE_PAUSE.equals(oldState) && TOGGLE_STATE_PLAY.equals(newState)) {
                    mPlayer.pause();
                } else if (TOGGLE_STATE_PLAY.equals(oldState)
                        && TOGGLE_STATE_PAUSE.equals(newState)) {
                    if (mPlayer.getStatus() == FairPlayer.PlayerStatus.PAUSED) {
                        mPlayer.unpause();
                    } else if (mPlayer.getStatus() == FairPlayer.PlayerStatus.STOPED) {
                        mPlayer.play();
                    }
                }

                return false;
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.previous();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.next();
            }
        });
    }
}
