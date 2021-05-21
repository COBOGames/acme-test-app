package com.isucorp.acmecompanytest;

// https://stackoverflow.com/questions/2002288/static-way-to-get-context-in-android

import android.app.Application;
import android.content.Context;

import com.orm.SugarContext;

@Info("Created by Ivan Faez Cobo on 20/5/2021")
public class App extends Application
{
    // region PRIVATE VARIABLES

    private static Context s_context;

    // endregion

    // region OVERRIDES

    @Override
    public void onCreate()
    {
        super.onCreate();

        s_context = getApplicationContext();
        SugarContext.init(this);
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        SugarContext.terminate();
    }

    // endregion

    // region PUBLIC METHODS

    public static Context getContext()
    {
        return s_context;
    }

    // endregion
}
