package com.hsquaretech.android_common_helpers.android_common_helpers.views;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by mac-hitesh on 8/2/18.
 */

public class spinnerAdapter extends ArrayAdapter<spinnerObject>
{
    List<spinnerObject> optVal = null;
    int optValSize = 0;

    public spinnerAdapter(Context context, int textViewResourceId, List<spinnerObject> objects )
    {
        super(context, textViewResourceId, objects);
        optVal = objects;
        optValSize = optVal.size();

    }

    public int getPosition ( String id )
    {
        for (int i = 0; i < optValSize; i++ )
        {
            if( optVal.get( i ).getId().equals( id ) )
            {
                return i;
            }
        }

        return 0;
    }

    public String getDatabaseIdByPosition ( int id )
    {
        spinnerObject selObj = optVal.get( id );
        if( selObj != null )
        {
            return selObj.getId();
        }

        return "0";
    }

    public void clearAdp()
    {

        for (spinnerObject element : optVal)
        {
            remove(element);
        }

        optVal = null;
        optValSize = 0;
    }

}
