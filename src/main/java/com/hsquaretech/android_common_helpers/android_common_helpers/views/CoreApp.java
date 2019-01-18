package com.hsquaretech.android_common_helpers.android_common_helpers.views;

import android.app.Application;
import android.content.Context;

import com.hsquaretech.android_common_helpers.android_common_helpers.android_core_helpers.log.log;
import com.hsquaretech.android_common_helpers.android_common_helpers.models.QbConfigs;
import com.hsquaretech.android_common_helpers.android_common_helpers.android_core_helpers.config.app_constants;

/**
 * Created by mac-hitesh on 3/8/18.
 */
public class CoreApp extends Application {
    public static final String TAG = CoreApp.class.getSimpleName();

    private static CoreApp instance;
    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config.json";
    private static QbConfigs qbConfigs;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        //initQbConfigs();
        //initCredentials();
    }

    public static void intiateCoreQb(Context context)
    {
        initQbConfigs();
    }
    private static void initQbConfigs()
    {
        log.singleton().debug("qbconfigname: "+ getQbConfigFileName());
       // qbConfigs = CoreConfigUtils.getCoreConfigsOrNull(getQbConfigFileName());
    }

    public static synchronized CoreApp getInstance() {
        return instance;
    }

    static final String APP_ID = app_constants.APP_ID;
    static final String AUTH_KEY = app_constants.AUTH_KEY;
    static final String AUTH_SECRET = app_constants.AUTH_SECRET;
    static final String ACCOUNT_KEY = app_constants.ACCOUNT_KEY;


    public QbConfigs getQbConfigs(){
        return qbConfigs;
    }

    protected static String getQbConfigFileName(){
        return QB_CONFIG_DEFAULT_FILE_NAME;
    }
}
