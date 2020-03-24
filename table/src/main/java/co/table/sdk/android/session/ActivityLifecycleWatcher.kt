package co.table.sdk.android.session

import android.app.Activity
import android.app.Application
import android.os.Bundle

internal class ActivityLifecycleWatcher : Application.ActivityLifecycleCallbacks {

    var currentActivity: Activity? = null

    override fun onActivityPaused(activity: Activity) {
        currentActivity = null
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
        currentActivity = null
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }


}