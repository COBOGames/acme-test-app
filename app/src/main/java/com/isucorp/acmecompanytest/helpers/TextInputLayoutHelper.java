package com.isucorp.acmecompanytest.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public final class TextInputLayoutHelper
{
    public static void addClearErrorOnTextChanged(final EditText edt, final TextInputLayout til)
    {
        edt.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                clearError(til);
            }

            @Override
            public void afterTextChanged(Editable s)
            {
            }
        });
    }

    public static void setError(TextInputLayout til, String message)
    {
        til.setErrorEnabled(true);
        til.setError(message);
    }

    public static void clearError(TextInputLayout til)
    {
        til.setErrorEnabled(false);
        til.setError(null);
    }
}
