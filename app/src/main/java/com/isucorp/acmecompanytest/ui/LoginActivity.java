package com.isucorp.acmecompanytest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;
import com.isucorp.acmecompanytest.helpers.TextInputLayoutHelper;

import androidx.appcompat.app.AppCompatActivity;

@Info("Created by Ivan Faez Cobo on 20/5/2021")
public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{

    // region CACHING VARIABLES

    private TextInputLayout m_tilUsername, m_tilPassword;
    private EditText m_edtUsername, m_edtPassword;

    // endregion

    // region OVERRIDES

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initCachingVariables();
        findViewById(R.id.btn_login_start).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_login_start:
            {
                // clear errors
                TextInputLayoutHelper.clearError(m_tilUsername);
                TextInputLayoutHelper.clearError(m_tilPassword);

                // get values from widgets
                String username = m_edtUsername.getText().toString().trim();
                String password = m_edtPassword.getText().toString().trim();

                // if empty set error
                if (TextUtils.isEmpty(username))
                    TextInputLayoutHelper.setError(m_tilUsername, getString(R.string.error_msg_can_not_be_empty));

                // if empty set error
                if (TextUtils.isEmpty(password))
                    TextInputLayoutHelper.setError(m_tilPassword, getString(R.string.error_msg_can_not_be_empty));

                // continue if no errors
                boolean usernameHasErrors = !TextUtils.isEmpty(m_tilUsername.getError());
                boolean passwordHasErrors = !TextUtils.isEmpty(m_tilPassword.getError());

                if (!usernameHasErrors && !passwordHasErrors)
                {
                    // check username and pass
                    // for the moment use admin/admin
                    if (!username.equals("admin") || !password.equals("admin"))
                        TextInputLayoutHelper.setError(m_tilPassword, getString(R.string.error_msg_wrong_username_or_pass));
                    else
                    {
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }
                }

                break;
            }
        }
    }

    // endregion

    // region PRIVATE METHODS

    private void initCachingVariables()
    {
        m_tilUsername = findViewById(R.id.til_login_email);
        m_tilPassword = findViewById(R.id.til_login_password);
        m_edtUsername = m_tilUsername.getEditText();
        m_edtPassword = m_tilPassword.getEditText();

        // when the edts text are changed clear the error
        TextInputLayoutHelper.addClearErrorOnTextChanged(m_edtUsername, m_tilUsername);
        TextInputLayoutHelper.addClearErrorOnTextChanged(m_edtPassword, m_tilPassword);
    }
}