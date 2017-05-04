package com.sohnyi.liangcare;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.sohnyi.liangcare.service.LockService;
import com.sohnyi.liangcare.utils.AppsUsage;
import com.sohnyi.liangcare.utils.PermissionsChecker;
import com.sohnyi.liangcare.utils.ServiceUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.sohnyi.liangcare.utils.PermissionsActivity.PACKAGE_URL_SCHEME;


/**
 * Created by sohnyi on 2017/3/12.
 */

public class AppLockFragment extends Fragment {

    private static final String TAG = "AppLockFragment";
    public static final String HAVE_CHANGED = "haveChanged";
    private static final String LOCK_SERVICE_NAME = "com.sohnyi.liangcare.service.LockService";

    private SharedPreferences mPreferences;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private AppLockAdapter mLockAdapter;
    private AlertDialog mAlertDialog;
    private PermissionsChecker mChecker;

    private List<LiangApp> mLiangApps;
    private List<String> mLabels;
    private List<Drawable> mIcons;
    private PackageManager pm;

    private int mPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = getActivity().getPackageManager();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(R.string.app_info_init);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mChecker = new PermissionsChecker(getActivity());

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

        /*判断应用锁服务是否正在运行*/
        if (!ServiceUtils.isServiceRunning(getActivity(), LOCK_SERVICE_NAME)) {
            Intent intent = new Intent(getActivity(), LockService.class);
            getActivity().startService(intent);
            Log.d(TAG, "onCreateView: start service");
        }

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!AppsUsage.isStatAccessPermissionSet(getActivity())) {
                Log.d(TAG, "onResume: is stat access permission set "
                        + AppsUsage.isStatAccessPermissionSet(getActivity()));
                if (AppsUsage.isNoOption(getActivity())) {
                    if (mAlertDialog == null) {
                        getUsageAccessDialog();
                        mAlertDialog.show();
                    } else if (!mAlertDialog.isShowing()) {
                        mAlertDialog.show();
                    }
                }
            }
        }
        updateUI();
    }

    private void updateUI() {
        boolean have_changed = mPreferences.getBoolean(HAVE_CHANGED, true);
        Log.d(TAG, "updateUI: have changed=" + have_changed);
        if (have_changed) {
            new InitAppInfo().execute();
        }

        mLabels = new ArrayList<>();
        mIcons = new ArrayList<>();
        new InitData().execute();
    }


    private class AppLockHolder extends RecyclerView.ViewHolder {
        private String mLabel;
        private TextView mNameTextView;
        private ImageView mIconImageView;
        private Switch mSwitch;

        private AppLockHolder(View itemView) {
            super(itemView);
            mIconImageView = (ImageView) itemView.findViewById(R.id.item_app_icon);
            mNameTextView = (TextView) itemView.findViewById(R.id.item_app_name);
            mSwitch = (Switch) itemView.findViewById(R.id.item_app_switch);

        }

        private void bindApp(LiangApp app, String label, Drawable icon, boolean lock) {
            mLabel = label;

            mNameTextView.setText(mLabel);
            mIconImageView.setImageDrawable(icon);
            mSwitch.setChecked(lock);
        }
    }


    private class AppLockAdapter extends RecyclerView.Adapter<AppLockHolder> {
        private List<LiangApp> mApps;

        private AppLockAdapter(List<LiangApp> apps) {
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
                            if (!mChecker.canDrawOverlay() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getActivity().getPackageName()));
                                startActivity(intent);
                            }
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

        private void setApps(List<LiangApp> apps) {
            mApps = apps;
        }

    }

    private class InitData extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            LiangAppLab appLab = LiangAppLab.get();
            mLiangApps = appLab.getApps();
            Log.d(TAG, "init data doInBackground: liang app size=" + mLiangApps.size());
            for (LiangApp app : mLiangApps) {
                try {
                    mLabels.add(pm.getApplicationLabel(pm.getApplicationInfo(
                            app.getPackageName(), PackageManager.GET_META_DATA)).toString());
                    mIcons.add(pm.getApplicationIcon(app.getPackageName()));
                } catch (PackageManager.NameNotFoundException e) {
                    Log.e(TAG, "updateUI: " + e.getMessage());
                    return false;
                }
            }
            if ((mLiangApps.size() == mLabels.size()) && (mLiangApps.size() == mIcons.size())) {
                Log.d(TAG, "doInBackground: liang app size =" + mLiangApps.size());
            } else {
                SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(getActivity());
                boolean h_c = p.getBoolean(HAVE_CHANGED, true);
                Log.e(TAG, "doInBackground: init data error");
                Log.e(TAG, "doInBackground: have changed " + h_c);
                Log.e(TAG, "doInBackground:" + ", label size=" + mLabels.size()
                        + ", icon size = " + mIcons.size());
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
            mProgressDialog.dismiss();
            if (mLockAdapter == null) {
                mLockAdapter = new AppLockAdapter(mLiangApps);
                mRecyclerView.setAdapter(mLockAdapter);
            } else {
                mLockAdapter.setApps(mLiangApps);
                mLockAdapter.notifyItemChanged(mPosition);
            }
        }
    }

    private class InitAppInfo extends AsyncTask<Void, Void, Boolean> {
        PackageManager packageManager = getActivity().getPackageManager();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            List<LiangApp> apps = DataSupport.findAll(LiangApp.class);
            if (apps.size() > 0) {
                DataSupport.deleteAll(LiangApp.class);
            }
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
                editor.putBoolean(HAVE_CHANGED, false)
                        .apply();
                Log.d(TAG, "doInBackground: init app info data saved");
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

    private void getUsageAccessDialog() {
        final int MY_DIALOG_THEME = R.style.myAlertDialog;
        final Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context, MY_DIALOG_THEME);
        builder.setTitle(getText(R.string.permission_request))
                .setMessage(getText(R.string.fun_need_app_usage))
                .setCancelable(false)
                .setPositiveButton(getText(R.string.settings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppsUsage.startActionUsageAccessSettings(context);
                    }
                })
                .setNegativeButton(getText(R.string.cancel), null);
        mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mAlertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context,
                        R.color.accent));
                mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context,
                        R.color.colorAccent));
            }
        });
    }
}
