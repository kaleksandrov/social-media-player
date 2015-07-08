package com.kaleksandrov.smp.ui.fragment;

import android.app.Activity;
import android.view.View;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.model.Artist;

import java.util.List;

/**
 * Created by kaleksandrov on 4/29/15.
 */
public class ArtistsAdapter extends AbsLibraryAdapter<Artist, AbsLibraryAdapter.ViewHolder> {

    public ArtistsAdapter(Activity activity, List<Artist> artists) {
        super(activity, artists, R.layout.card_artist);
    }

    @Override
    protected void populateView(AbsLibraryAdapter.ViewHolder viewHolder, Artist artist) {
        viewHolder.mId = artist.getId();
        viewHolder.mNameView.setText(artist.getName());
    }

    @Override
    protected AbsLibraryAdapter.ViewHolder createViewHolder(View view) {
        return new AbsLibraryAdapter.ViewHolder(view);
    }
}
