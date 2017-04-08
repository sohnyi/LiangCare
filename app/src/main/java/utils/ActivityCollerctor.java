package utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sohnyi on 2017/4/8.
 */

public class ActivityCollerctor {
    public static List<Activity> sActivities = new ArrayList<>();

    public static void addActivity(Activity activity) {
        sActivities.add(activity);
    }

    public static void removeActivity(Activity activity) {
        sActivities.remove(activity);
    }

    public static void finishAllActivities() {
        for (Activity activity: sActivities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
