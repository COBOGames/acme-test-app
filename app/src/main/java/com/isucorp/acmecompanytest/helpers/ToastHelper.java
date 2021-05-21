package com.isucorp.acmecompanytest.helpers;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.isucorp.acmecompanytest.App;
import com.isucorp.acmecompanytest.Info;

@Info("Created by Ivan Faez Cobo on 21/05/2021")
public final class ToastHelper
{
    public static void show(String message)
    {
        Toast.makeText(App.getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String message)
    {
        Toast.makeText(App.getContext(), message, Toast.LENGTH_LONG).show();
    }


    public static Snackbar showSnackbarShort(View view, String message)
    {
        return showSnackbar(view, message, Snackbar.LENGTH_SHORT);
    }

    public static Snackbar showSnackbarLong(View view, String message)
    {
        return showSnackbar(view, message, Snackbar.LENGTH_LONG);
    }

    public static Snackbar showSnackbar(View view, String message, int length)
    {
        final Snackbar snackbar = Snackbar.make(view, message, length);
        snackbar.show();
        setColorToSnackbarText(snackbar);

        return snackbar;
    }

    private static final int SNACKBAR_TEXT_COLOR = Color.parseColor("#DEffffff");

    private static void setColorToSnackbarText(Snackbar snackbar)
    {
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(SNACKBAR_TEXT_COLOR);
    }
}
