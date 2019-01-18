package com.hsquaretech.android_common_helpers.android_common_helpers.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.hsquaretech.android_common_helpers.BuildConfig;
import com.hsquaretech.android_common_helpers.android_common_helpers.models.SampleConfigs;
import com.hsquaretech.android_common_helpers.android_common_helpers.android_core_helpers.helpers.imui;

import net.gotev.uploadservice.UploadService;

import io.fabric.sdk.android.Fabric;

import static com.hsquaretech.android_common_helpers.android_common_helpers.android_core_helpers.config.config.config;

/**
 * Created by mac-hitesh on 3/8/18.
 */
public class App extends CoreApp
{
    private static final String TAG = App.class.getSimpleName();
    private static SampleConfigs sampleConfigs;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Log.i("im_application", "im_application onCreate xmppBackgroundService ACTION_START_SERVICE called");

        //FirebaseApp.initializeApp(getApplicationContext().);
        //crashlytics
        if (config.ENV >= 2)
        {
            Fabric.with(this, new Crashlytics());
        }


        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID;
        // Or, you can define it manually.
        UploadService.NAMESPACE = "com.hsquaretech";

        //init(this);
//        initSampleConfigs();

        //
        intiateQb(getApplicationContext());
    }


    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Log.i("im_application", "im_application onLowMemory called");
    };

    /**
     * when activity on trim memory this function is called.
     */
    @Override
    @SuppressLint("NewApi")
    public void onTrimMemory(int level)
    {
        if( imui.singleton().versionSDK_INT() >= 14 )
        {
            super.onTrimMemory(level);
        }
        Log.i("im_application", "im_application onTrimMemory level=" + level);
    };

    public static void intiateQb(Context context)
    {
//        initSampleConfigs();
        CoreApp.intiateCoreQb(context);
    }

}
