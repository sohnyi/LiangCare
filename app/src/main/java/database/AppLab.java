package database;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import org.litepal.tablemanager.Connector;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sohnyi on 2017/3/18.
 */

public class AppLab {

    private static final String TAG = "ApppLab";

    private static AppLab sAppLab;
    private Context mContext;

    private List<LiangApp> mLiangApps;

    public static AppLab get(Context context) {
        if (sAppLab == null) {
            sAppLab = new AppLab(context);
        }

        return sAppLab;
    }

    private AppLab(Context context) {
        mContext = context.getApplicationContext();
        Connector.getDatabase();
    }

    public List<LiangApp> getAllApps(final Context context) {

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = context.getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString());
            }
        });

        if (activities != null) {
            for (ResolveInfo app : activities) {
                LiangApp liangApp = new LiangApp();
            }
        }

        Log.d(TAG, "Found " + activities.size() + " activities.");


        return null;
    }

    public LiangApp getAllApps(int id) {
            return null;
    }

    public void addApp(LiangApp app) {

    }
}
