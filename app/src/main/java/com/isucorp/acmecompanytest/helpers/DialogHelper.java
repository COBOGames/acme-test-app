package com.isucorp.acmecompanytest.helpers;

import android.content.Context;
import android.content.DialogInterface;

import com.isucorp.acmecompanytest.App;
import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;

import androidx.appcompat.app.AlertDialog;

@Info("Created by Ivan Faez Cobo on 22/5/2021")
public class DialogHelper
{
    // region CONSTANTS

    public static final String NO_TEXT = "0";
    public static final String DEFAULT_TEXT = "1";

    // endregion

    // region CONFIRM METHODS

    /**
     * Show a cancelable confirm dialog.
     * <br/>
     * NOTE: The dialog is dismissed when a button is clicked.
     *
     * @param context            The context
     * @param title              Use {@link #NO_TEXT} to hide it or {@link #DEFAULT_TEXT} to use
     *                           the default CONFIRMATION text.
     * @param message            The message.
     * @param onAnyButtonClicked If 'null' the dialog is only dismissed on any button.
     * @param positiveBtnName    Use {@link #NO_TEXT} to hide it or {@link #DEFAULT_TEXT} to use
     *                           the built-in ACCEPT text.
     * @param negativeBtnName    Use {@link #NO_TEXT} to hide it or {@link #DEFAULT_TEXT} to use
     *                           the built-in CANCEL text.
     */
    public static AlertDialog showConfirm(
            Context context,
            String title,
            String message,
            final DialogInterface.OnClickListener onAnyButtonClicked,
            String positiveBtnName,
            String negativeBtnName
    )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // message
        builder.setMessage(message);

        String titleText = getText(title, getConfirmationString());
        String positiveText = getText(positiveBtnName, getAcceptString());
        String negativeText = getText(negativeBtnName, getCancelString());

        // title
        if (titleText != null) builder.setTitle(titleText);

        // buttons
        DialogInterface.OnClickListener onClickListener = new OnAnyButtonClicked(onAnyButtonClicked);
        if (positiveText != null) builder.setPositiveButton(positiveText, onClickListener);
        if (negativeText != null) builder.setNegativeButton(negativeText, onClickListener);

        // show it
        AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    // endregion

    /**
     * Returns the built-in ACCEPT string.
     */
    public static String getAcceptString()
    {
        return App.getContext().getString(android.R.string.yes);
    }

    /**
     * Returns the built-in CANCEL string.
     */
    public static String getCancelString()
    {
        return App.getContext().getString(android.R.string.cancel);
    }

    public static String getConfirmationString()
    {
        return App.getContext().getString(R.string.confirm_dialog_default_title);
    }

    /**
     * If the text is {@link #NO_TEXT}, the null is returned. Otherwise, if the text is not
     * {@link #DEFAULT_TEXT}, the text returned.
     */
    private static String getText(String text, String default_)
    {
        // if no text, return null
        if (text.equals(NO_TEXT))
            return null;

        // return the text if not using the default text
        if (!text.equals(DEFAULT_TEXT))
            return text;

        return default_;
    }

    // region NESTED TYPES

    private static class OnAnyButtonClicked implements DialogInterface.OnClickListener
    {
        private final DialogInterface.OnClickListener customListener;

        private OnAnyButtonClicked(DialogInterface.OnClickListener listener)
        {
            customListener = listener;
        }

        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            if (customListener != null)
                customListener.onClick(dialog, which);

            dialog.dismiss();
        }
    }

    // endregion
}
