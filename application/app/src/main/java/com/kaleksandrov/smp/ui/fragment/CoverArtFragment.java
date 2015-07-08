package com.kaleksandrov.smp.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kaleksandrov.smp.R;
import com.kaleksandrov.smp.application.FairPlayerApplication;
import com.kaleksandrov.smp.ui.img.CoverManager;

public class CoverArtFragment extends Fragment {

    private static final String KEY_COVER_URI = "cover_id";

    private CoverManager mCoverManager;

    // Views
    private View mRoot;
    private ImageView mCoverArt;

    public static CoverArtFragment newInstance(String coverPath) {
        CoverArtFragment f = new CoverArtFragment();

        Bundle args = new Bundle();
        args.putString(KEY_COVER_URI, coverPath);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_cover_art, container, false);
        mCoverArt = (ImageView) mRoot.findViewById(R.id.cover_art);

        FairPlayerApplication app = (FairPlayerApplication) getActivity().getApplicationContext();
        mCoverManager = app.getCoverManager();

        updateCoverArt();

        return mRoot;
    }

    @Override
    public void onDestroyView() {
        mCoverManager.cancelLoading(mCoverArt);
        super.onDestroyView();
    }

    /**
     * Updates the album cover.
     */
    private void updateCoverArt() {
        String coverPath = getArguments().getString(KEY_COVER_URI);
        mCoverManager.loadInto(coverPath, mCoverArt);
    }
}
