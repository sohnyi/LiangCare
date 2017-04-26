package com.sohnyi.liangcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.sohnyi.liangcare.database.LiangApp;
import com.sohnyi.liangcare.database.LiangAppLab;
import com.sohnyi.liangcare.utils.GetIconBySystem;
import com.sohnyi.liangcare.utils.GetLabelBySystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by sohnyi on 2017/3/12.
 */

public class AppLockFragment extends Fragment {

    private static final String TAG = "AppLockFragment";
    private static final String IS_FIRST_OPEN = "isFirstLoad";

    private SharedPreferences mPreferences;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private AppLockAdapter mLockAdapter;

    private List<String> mLabels;
    private List<Drawable> mIcons;
    private PackageManager pm;

    private int mPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLabels = new ArrayList<>();
        mIcons = new ArrayList<>();
        pm = getActivity().getPackageManager();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_lock, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_app_lock_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean is_is_first_load = mPreferences.getBoolean(IS_FIRST_OPEN, true);
        if (is_is_first_load) {
            new InitAppInfo().execute();
        }

        updateUI();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        updateUI();
    }

    private void updateUI() {
        LiangAppLab appLab = LiangAppLab.get(getActivity());
        List<LiangApp> apps = appLab.getApps();
        for (LiangApp app : apps) {
            try {

                mLabels.add(pm.getApplicationLabel(pm.getApplicationInfo(
                        app.getPackageName(), PackageManager.GET_META_DATA)).toString());

                mIcons.add(pm.getApplicationIcon(app.getPackageName()));
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "updateUI: " + e.getMessage());
            }
        }

        if (mLockAdapter == null) {
            mLockAdapter = new AppLockAdapter(apps);
            mRecyclerView.setAdapter(mLockAdapter);
        } else {
            mLockAdapter.setApps(apps);
            mLockAdapter.notifyItemChanged(mPosition);
        }
    }


    private class AppLockHolder extends RecyclerView.ViewHolder {
        private String mLabel;
        private TextView mNameTextView;
        private ImageView mIconImageView;
        private Switch mSwitch;

        public AppLockHolder(View itemView) {
            super(itemView);
            mIconImageView = (ImageView) itemView.findViewById(R.id.item_app_icon);
            mNameTextView = (TextView) itemView.findViewById(R.id.item_app_name);
            mSwitch = (Switch) itemView.findViewById(R.id.item_app_switch);

        }

        public void bindApp(LiangApp app, String label, Drawable icon, boolean lock) {
            mLabel = label;

            if (mLabels != null) {
                mNameTextView.setText(mLabel);
            } else {
                mNameTextView.setText(GetLabelBySystem.getLabel(getActivity(), app.getPackageName()));
            }
            if (icon != null) {
                mIconImageView.setImageDrawable(icon);
            } else {
                mIconImageView.setImageDrawable(GetIconBySystem.getIcon(getActivity(), app.getPackageName()));
            }
            mSwitch.setChecked(lock);
        }
    }




private class AppLockAdapter extends RecyclerView.Adapter<AppLockHolder> {
    private List<LiangApp> mApps;

    public AppLockAdapter(List<LiangApp> apps) {
        mApps = apps;
    }

    @Override
    public AppLockHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater
                .inflate(R.layout.app_view_item, parent, false);
        final AppLockHolder holder = new AppLockHolder(view);
        holder.mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int position = holder.getAdapterPosition();
                LiangApp app = mApps.get(position);
                if (app != null) {
                    if (isChecked) {
                        app.setLock(true);
                        Log.d(TAG, "onCheckedChanged: " + app.getPackageName() + " lock");
                    } else {
                        app.setLock(false);
                        Log.d(TAG, "onCheckedChanged: " + app.getPackageName() + " unlock");
                    }
                    app.save();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(AppLockHolder appLockHolder, int position) {
        mPosition = position;
        LiangApp app = mApps.get(position);
        String label = mLabels.get(position);
        Drawable icon = mIcons.get(position);
        boolean lock = app.isLock();
        appLockHolder.bindApp(app, label, icon, lock);
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    public void setApps(List<LiangApp> apps) {
        mApps = apps;
    }

}

private class InitAppInfo extends AsyncTask<Void, Void, Boolean> {
    PackageManager packageManager = getActivity().getPackageManager();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(R.string.app_info_init);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Intent startupIntent = new Intent(Intent.ACTION_MAIN);
            startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(startupIntent, 0);
            Collections.sort(resolveInfos, new Comparator<ResolveInfo>() {
                @Override
                public int compare(ResolveInfo o1, ResolveInfo o2) {
                    return String.CASE_INSENSITIVE_ORDER.compare(
                            o1.loadLabel(packageManager).toString(),
                            o2.loadLabel(packageManager).toString()
                    );
                }
            });
            for (ResolveInfo info : resolveInfos) {
                LiangApp liangApp = new LiangApp();
                String packageName = info.activityInfo.packageName;

                liangApp.setPackageName(packageName);
                liangApp.setLock(false);
                liangApp.setPassword(null);
                liangApp.setInpass(false);
                liangApp.save();
            }
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putBoolean(IS_FIRST_OPEN, false)
                    .apply();
            Log.d(TAG, "doInBackground: data saved");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mProgressDialog.dismiss();
    }


}
}
