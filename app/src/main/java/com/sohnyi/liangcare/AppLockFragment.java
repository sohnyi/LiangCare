package com.sohnyi.liangcare;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by sohnyi on 2017/3/12.
 */

public class AppLockFragment extends Fragment {
    private RecyclerView mRecyclerView;

    private boolean isLock = false;
    private int count = 0;


    public static AppLockFragment newInstance() {
        return new AppLockFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_lock, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_app_lock_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.HORIZONTAL));


        setupAdapter();
        return view;

    }

    private void setupAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(startupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo resolveInfo, ResolveInfo t1) {
                return String.CASE_INSENSITIVE_ORDER.compare(
                        resolveInfo.loadLabel(packageManager).toString(),
                        t1.loadLabel(packageManager).toString()
                );
            }
        });
        mRecyclerView.setAdapter(new ActivityAdapter(activities));
    }


    private class ActivityHolder extends RecyclerView.ViewHolder {
        private ResolveInfo mResolveInfo;
        private TextView mNameTextView;
        private ImageView mIconImageView;
        private ImageView mImageView;
        public ActivityHolder(View itemView) {
            super(itemView);
            mIconImageView = (ImageView) itemView.findViewById(R.id.item_app_icon);
            mNameTextView = (TextView) itemView.findViewById(R.id.item_app_name);
            mImageView = (ImageView) itemView.findViewById(R.id.item_app_lock);

            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isLock = !isLock;
                    bindActivity(mResolveInfo);
                }
            });
            //itemView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();
            String appName = mResolveInfo.loadLabel(pm).toString();
            mIconImageView.setImageDrawable(mResolveInfo.loadIcon(pm));
            mNameTextView.setText(appName);

            if (isLock == false) {
                mImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_lock_open_blue_grey_500_24dp));
            } else {
                mImageView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_lock_light_green_a700_24dp));
            }
        }

    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private final List<ResolveInfo> mActivities;

        public ActivityAdapter(List<ResolveInfo> activities) {
            mActivities = activities;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.app_view_item, parent, false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder activityHolder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            activityHolder.bindActivity(resolveInfo);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "getItemCount:" + mActivities.size());
            return mActivities.size();
        }
    }
}
