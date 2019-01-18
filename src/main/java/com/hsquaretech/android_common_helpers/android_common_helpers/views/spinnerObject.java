package com.hsquaretech.android_common_helpers.android_common_helpers.views;

/**
 * Created by mac-hitesh on 8/2/18.
 */

public class spinnerObject {
    private String databaseId;
    private String databaseValue;

    public spinnerObject ( String databaseId , String databaseValue ) {
        this.databaseId = databaseId;
        this.databaseValue = databaseValue;
    }

    public String getId () {
        return databaseId;
    }

    public String getValue () {
        return databaseValue;
    }

    @Override
    public String toString () {
        return databaseValue;
    }
}
