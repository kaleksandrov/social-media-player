package com.kaleksandrov.smp.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.model.Song;
import com.kaleksandrov.smp.player.MediaList;
import com.kaleksandrov.smp.player.MediaQueue;
import com.kaleksandrov.smp.ui.activity.AbsDetailsAdapter;
import com.kaleksandrov.smp.ui.activity.PlaylistAdapter;

public class PlaylistFragment extends AbsRecyclerViewFragment implements AbsDetailsAdapter.OnItemClickListener<Song> {

	/* Constants */

    private static final String LOG_TAG = PlaylistFragment.class.getSimpleName();

	/* Callback interfaces */

    public interface OnMediaClickListener {
        void onMediaClicked(int mediaId);
    }

    /* Playlist */
    private MediaList mPlaylist;
    private PlaylistAdapter mPlaylistAdapter;

    private Context mContext;
    private MediaQueue mQueue;

    /* Event Listeners */
    private OnMediaClickListener mListener;

	/* Constructors */

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        Activity activity = getActivity();

        mContext = activity.getApplicationContext();

        FairPlayerApplication app = (FairPlayerApplication) mContext.getApplicationContext();
        // Get the iterator
        mPlaylist = app.getPlaylist();
        mQueue = mPlaylist.getQueue();

        mPlaylistAdapter = new PlaylistAdapter(getActivity(), mPlaylist.getSongs());
        mPlaylistAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mPlaylistAdapter);

        return root;
    }


    @Override
    public void onItemClick(View view, Song song) {
        int index = mPlaylist.getPosition(song);

        if (mListener != null) {
            mListener.onMediaClicked(index);
        }
    }

    @Override
    public void onItemLongClick(View view, Song song) {
        enqueue(song);
        Toast.makeText(getActivity(),
                song.getName() + " added to queue!",
                Toast.LENGTH_LONG).show();

    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManager(Context context) {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof OnMediaClickListener) {
            mListener = (OnMediaClickListener) activity;
        }

        super.onAttach(activity);
    }

    /**
     * Moves the playlist cursor to the currently played song.
     */
    public void moveToCurrent() {
        int currentPosition = mPlaylist.getPosition();
        mRecyclerView.scrollToPosition(currentPosition);
        mPlaylistAdapter.setSelectedItem(currentPosition);
    }

    public void notifyPlaylistChanged() {
        mPlaylistAdapter.notifyDataSetChanged();
    }

    /**
     * Adds the given media to the queue.
     *
     * @param song The media to be added to the queue
     */
    private void enqueue(Song song) {
        mQueue.enqueue(song);
        mPlaylistAdapter.notifyDataSetChanged();
    }
}
