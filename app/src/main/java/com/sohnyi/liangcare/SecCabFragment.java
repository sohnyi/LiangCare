package com.sohnyi.liangcare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sohnyi on 2017/4/10.
 */

public class SecCabFragment extends Fragment implements View.OnClickListener{

    private static final int FILE_TYPE_DOC = 1;
    private static final int FILE_TYPE_IMG = 2;
    private static final int FILE_TYPE_AUD = 3;
    private static final int FILE_TYPE_VID = 4;

    private CardView mCardView_doc;
    private CardView mCardView_img;
    private CardView mCardView_aud;
    private CardView mCardView_vid;

    public static SecCabFragment newInstance() {
        return new SecCabFragment();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sec_cab, container, false);

        initViews(view);

        return view;
    }

    @Override
    public void onClick(View v) {
        int file_type = 0;
        switch (v.getId()) {
            case R.id.doc_cardView:
                file_type = FILE_TYPE_DOC;
                break;
            case R.id.image_cardView:
                file_type = FILE_TYPE_IMG;
                break;
            case R.id.audio_cardView:
                file_type = FILE_TYPE_AUD;
                break;
            case R.id.video_cardView:
                file_type = FILE_TYPE_VID;
                break;
            default:
                break;
            }
        if (file_type != 0) {
            Intent intent = SecCabFileListActivity.newIntent(getActivity(), file_type);
            startActivity(intent);
        }
    }

    public void initViews(View v) {
        mCardView_doc = (CardView) v.findViewById(R.id.doc_cardView);
        mCardView_img = (CardView) v.findViewById(R.id.image_cardView);
        mCardView_aud = (CardView) v.findViewById(R.id.audio_cardView);
        mCardView_vid = (CardView) v.findViewById(R.id.video_cardView);
        mCardView_doc.setOnClickListener(this);
        mCardView_img.setOnClickListener(this);
        mCardView_aud.setOnClickListener(this);
        mCardView_vid.setOnClickListener(this);
    }

}
