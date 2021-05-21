package com.isucorp.acmecompanytest.helpers;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;

import androidx.annotation.MenuRes;
import androidx.appcompat.widget.PopupMenu;

@Info("Created by Ivan Faez Cobo on 21/5/2021")
public final class PopupMenuHelper
{

    public static PopupMenu show(Context context, View anchor, @MenuRes int menuResId)
    {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.inflate(menuResId);
        popup.show();

        return popup;
    }

    public static PopupMenu show(Context context, View anchor, @MenuRes int menuResId,
                                 PopupMenu.OnMenuItemClickListener listener)
    {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.inflate(menuResId);
        popup.setOnMenuItemClickListener(listener);
        popup.show();

        return popup;
    }

    public static PopupMenu showOverflow(Context context, View anchor, @MenuRes int menuResId,
                                         PopupMenu.OnMenuItemClickListener listener)
    {
        PopupMenu popup = createOverflow(context, anchor, menuResId, listener);
        popup.show();

        return popup;
    }

    /**
     * Create a {@link PopupMenu} but without showing it. You must to show it.
     * Useful for modifying items before calling the show() method.
     */
    public static PopupMenu createOverflow(Context context, View anchor, @MenuRes int menuResId,
                                           PopupMenu.OnMenuItemClickListener listener)
    {
        PopupMenu popup = new PopupMenu(context, anchor, Gravity.START, 0, R.style.PopupMenu_Overflow);
        popup.inflate(menuResId);
        popup.setOnMenuItemClickListener(listener);

        return popup;
    }
}
