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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import com.sohnyi.liangcare.utils.LogUtil;
import com.sohnyi.liangcare.utils.PermissionsChecker;
import com.sohnyi.liangcare.utils.ServiceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.sohnyi.liangcare.utils.PermissionsActivity.PACKAGE_URL_SCHEME;

/**
 * Created by sohnyi on 2017/3/12.
 */

public class AppLockActivity extends AppCompatActivity {
    private static final String TAG = "AppLockActivity ";
    public static final String FIRST_LOAD = "firstLoad";
    private static final String LOCK_SERVICE_NAME = "com.sohnyi.liangcare.service.LockService";

    private SharedPreferences mPreferences;
    private RecyclerView mRecyclerView;
    private ProgressDialog mProgressDialog;
    private AppLockAdapter mLockAdapter;
    private AlertDialog mAlertDialog;
    private PermissionsChecker mChecker;
    private Toolbar mToolbar;

    private List<LiangApp> mLiangApps;
    private List<String> mLabels;
    private List<Drawable> mIcons;
    private PackageManager pm;
    private LiangAppLab mAppLab;

    private int mPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        pm = getPackageManager();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mChecker = new PermissionsChecker(this);
        mAppLab = LiangAppLab.get();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.app_lock_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(divider);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        /*判断应用锁服务是否正在运行*/
        if (!ServiceUtils.isServiceRunning(this, LOCK_SERVICE_NAME)) {
            Intent intent = new Intent(this, LockService.class);
            startService(intent);
            LogUtil.d(TAG, "onCreateView: start service");
        }

        initProgressDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fragmen_lock, menu);
        MenuItem searchItem = menu.findItem(R.id.search_item_fragment_lock);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set_item_fragment_lock:
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!AppsUsage.isStatAccessPermissionSet(this)) {
                LogUtil.d(TAG, "onResume: is stat access permission set "
                        + AppsUsage.isStatAccessPermissionSet(this));
                if (AppsUsage.isNoOption(this)) {
                    if (mAlertDialog == null) {
                        getUsageAccessDialog();
                        mAlertDialog.show();
                    } else if (!mAlertDialog.isShowing()) {
                        mAlertDialog.show();
                    }
                } else {
                    LogUtil.d(TAG, "no option");
                }
            }
        }
        updateUI();
    }

    private void updateUI() {
        boolean first_load = mPreferences.getBoolean(FIRST_LOAD, true);
        LogUtil.d(TAG, "updateUI: first load=" + first_load);
        if (first_load) {
            new InitAppInfo().execute();
        }

        mLabels = new ArrayList<>();
        mIcons = new ArrayList<>();
        new InitData().execute();
    }

    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(R.string.app_info_init);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.setCancelable(false);
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
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
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
                                intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getApplicationContext().getPackageName()));
                                startActivity(intent);
                            }
                            LogUtil.d(TAG, "onCheckedChanged: " + app.getPackageName() + " lock");
                        } else {
                            app.setLock(false);
                            LogUtil.d(TAG, "onCheckedChanged: " + app.getPackageName() + " unlock");
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

    /*获取应用列表信息*/
    private class InitData extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            mLiangApps = mAppLab.getApps();

            LogUtil.d(TAG, "init data doInBackground: liang app size=" + mLiangApps.size());
            for (LiangApp app : mLiangApps) {
                try {
                    mLabels.add(pm.getApplicationLabel(pm.getApplicationInfo(
                            app.getPackageName(), PackageManager.GET_META_DATA)).toString());
                } catch (PackageManager.NameNotFoundException e) {
                    mLabels.add(app.getPackageName());
                    LogUtil.e(TAG, "updateUI: label" + e.getMessage());
                }
                try {
                    mIcons.add(pm.getApplicationIcon(app.getPackageName()));
                } catch (PackageManager.NameNotFoundException e) {
                    mIcons.add(getApplicationContext().getDrawable(R.mipmap.default_icon));
                    LogUtil.e(TAG, "updateUI: icon" + e.getMessage());
                }
            }
            if ((mLiangApps.size() == mLabels.size()) && (mLiangApps.size() == mIcons.size())) {
                LogUtil.d(TAG, "doInBackground: liang app size =" + mLiangApps.size());
            } else {
                LogUtil.e(TAG, "doInBackground: init data error");
                LogUtil.e(TAG, "doInBackground:" + ", label size=" + mLabels.size()
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

    /*首次打开，初始化应用信息并保存到数据库*/
    private class InitAppInfo extends AsyncTask<Void, Void, Boolean> {
        PackageManager packageManager = getApplicationContext().getPackageManager();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                    String packageName = info.activityInfo.packageName;
                    mAppLab.addApp(packageName);
                }
                LiangApp app = new LiangApp();
                app.setPackageName("com.google.android.packageinstaller");
                app.setLock(true);
                app.save();
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean(FIRST_LOAD, false)
                        .apply();
                LogUtil.d(TAG, "doInBackground: init app info data saved");
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

    /*获取查看应用信息权限*/
    private void getUsageAccessDialog() {
        final int MY_DIALOG_THEME = R.style.myAlertDialog;
        final Context context = getApplicationContext();
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
