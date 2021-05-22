package com.isucorp.acmecompanytest.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;
import com.isucorp.acmecompanytest.entities.AbstractSugarEntity;
import com.isucorp.acmecompanytest.entities.Ticket;
import com.isucorp.acmecompanytest.helpers.TextInputLayoutHelper;
import com.isucorp.acmecompanytest.helpers.ToastHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

@Info("Created by Ivan Faez Cobo on 21/5/2021")
public class WorkTicketActivity extends AppCompatActivity
{
    // region CONSTANTS

    /**
     * Extra of String type. <br/>
     * Indicates the uuid of the {@link Ticket} created or edited when your
     * {@link Activity#onActivityResult(int, int, Intent)} is invoked.
     */
    public static final String EXTRA_TICKET_UUID = "8ac835a69b7e4170b98a118126641e3b";

    /**
     * Extra of Boolean type. <br/>
     * Indicates if the {@link Ticket} was edited (true) or created (false) when your
     * {@link Activity#onActivityResult(int, int, Intent)} is invoked.
     */
    public static final String EXTRA_TICKET_WAS_EDITED = "9e24ea43fba4457ba9b06ac8de4d0a03";

    /**
     * The date format used to parse dates [d/M/yyyy]
     */
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("d/M/yyyy", Locale.US);

    /**
     * Extra of String type. <br/>
     * Indicates the uuid of the {@link Ticket} to edit.
     */
    private static final String EXTRA_TICKET_TO_EDIT_UUID = "8dc84aff433a48f4896ccacaf3240dd8";

    // endregion

    // region PRIVATE VARIABLES

    private Ticket m_ticket;
    private boolean m_editing;

    // endregion

    // region CACHING VARIABLES

    private TextInputLayout m_tilClientName, m_tilAddress, m_tilScheduleDate;
    private EditText m_edtClientName, m_edtAddress, m_edtScheduleDate, m_edtPhone, m_edtReasonForCall;

    // endregion

    // region FACTORY METHODS

    /**
     * Use this factory method to start new instance of this activity using the provided parameters.
     * <br/><br/>
     * You can receive de results in your {@link Activity#onActivityResult(int, int, Intent)} method.
     * If {@link Activity#RESULT_OK} means we added or edited successfully. <br/>
     * - To check if a ticket was edited or created check the boolean {@link #EXTRA_TICKET_WAS_EDITED}. <br/>
     * - To get the uuid of the ticket created or edited, get the string {@link #EXTRA_TICKET_UUID}. <br/>
     *
     * @param ticket If null we start the activity to create a new ticked. If not null, we start
     *               the activity to edit it.
     */
    public static void start(Activity fromActivity, Ticket ticket, int requestCode)
    {
        // create intent and put extras
        Intent intent = new Intent(fromActivity, WorkTicketActivity.class);

        // if not null start editing it
        if (ticket != null)
            intent.putExtra(EXTRA_TICKET_TO_EDIT_UUID, ticket.getUuid());

        fromActivity.startActivityForResult(intent, requestCode);
    }

    // endregion

    // region OVERRIDES

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_ticket);

        initCachingVariables();
        initExtras();
        updateToolbarTitle();

        // show go back btn in the toolbar and set the close icon
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.work_ticket, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i)
    {
        switch (i.getItemId())
        {
            case android.R.id.home:
            {
                onBackPressed();
                return true;
            }
            case R.id.action_save:
            {
                onSaveClicked();
                return true;
            }
        }

        return super.onOptionsItemSelected(i);
    }

    // endregion

    // region PRIVATE METHODS

    private void onSaveClicked()
    {
        // clear errors
        TextInputLayoutHelper.clearError(m_tilClientName);
        TextInputLayoutHelper.clearError(m_tilAddress);
        TextInputLayoutHelper.clearError(m_tilScheduleDate);

        String clientName = m_edtClientName.getText().toString();
        String address = m_edtAddress.getText().toString();
        String date = m_edtScheduleDate.getText().toString();
        String phone = m_edtPhone.getText().toString();
        String reason = m_edtReasonForCall.getText().toString();

        boolean errors = false;

        // region CHECK REQUIRED FIELDS

        if (TextUtils.isEmpty(clientName))
        {
            TextInputLayoutHelper.setError(m_tilClientName, getString(R.string.error_msg_can_not_be_empty));
            errors = true;
        }

        if (TextUtils.isEmpty(address))
        {
            TextInputLayoutHelper.setError(m_tilAddress, getString(R.string.error_msg_can_not_be_empty));
            errors = true;
        }

        if (TextUtils.isEmpty(date))
        {
            TextInputLayoutHelper.setError(m_tilScheduleDate, getString(R.string.error_msg_can_not_be_empty));
            errors = true;
        }

        // endregion

        // end if we have errors
        if (errors) return;

        // region ASSIGN VALUES

        // parse date
        try
        {
            Date d = DATE_FORMAT.parse(date);
            m_ticket.setScheduleTime(d.getTime());
        }
        catch (Exception e)
        {
            ToastHelper.show("Error parsing Schedule date");
            return;
        }

        m_ticket.setClientName(clientName);
        m_ticket.setAddress(address);
        m_ticket.setPhone(phone);
        m_ticket.setReasonForCall(reason);

        try
        {
            // save the ticket
            m_ticket.save();

            // set result OK result and finish the activity
            Intent data = new Intent();
            data.putExtra(EXTRA_TICKET_WAS_EDITED, m_editing);
            data.putExtra(EXTRA_TICKET_UUID, m_ticket.getUuid());

            setResult(RESULT_OK, data);
            finish();
        }
        catch (Exception e)
        {
            Log.e("Error", e.getMessage());
            ToastHelper.show("Error saving ticket");
        }

        // endregion
    }

    private void initCachingVariables()
    {
        m_tilClientName = findViewById(R.id.til_client_name);
        m_tilAddress = findViewById(R.id.til_address);
        m_tilScheduleDate = findViewById(R.id.til_schedule_date);
        TextInputLayout tilPhone = findViewById(R.id.til_phone);
        TextInputLayout tilReason = findViewById(R.id.til_reason_for_call);

        m_edtClientName = m_tilClientName.getEditText();
        m_edtAddress = m_tilAddress.getEditText();
        m_edtScheduleDate = m_tilScheduleDate.getEditText();
        m_edtPhone = tilPhone.getEditText();
        m_edtReasonForCall = tilReason.getEditText();

        // when the edit texts are changed clear the error. only in required fields
        TextInputLayoutHelper.addClearErrorOnTextChanged(m_edtClientName, m_tilClientName);
        TextInputLayoutHelper.addClearErrorOnTextChanged(m_edtAddress, m_tilAddress);
        TextInputLayoutHelper.addClearErrorOnTextChanged(m_edtScheduleDate, m_tilScheduleDate);

        // region EVENTS

        findViewById(R.id.btn_get_directions).setOnClickListener(v -> {
            ToastHelper.show("TODO get direction");
        });

        // show date picker dialog when schedule date is clicked
        m_edtScheduleDate.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            // try to set the current date in the edt if not empty
            String currentDate = m_edtScheduleDate.getText().toString();
            if (!TextUtils.isEmpty(currentDate))
            {
                try
                {
                    Date date = DATE_FORMAT.parse(currentDate);
                    c.setTimeInMillis(date.getTime());
                }
                catch (Exception e)
                {
                    ToastHelper.show("Error parsing date");
                }
            }

            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONDAY);
            int d = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    WorkTicketActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        m_edtScheduleDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    },
                    y, m, d
            );
            // allow picking dates from today and on
            // for test purposes we comment the below line to allow seeing due tickets quickly
            //dialog.getDatePicker().setMinDate(System.currentTimeMillis());

            dialog.show();
        });

        // endregion
    }

    private void updateToolbarTitle()
    {
        int id = m_editing ? R.string.work_ticked_edit_title : R.string.action_new_ticket;
        setTitle(getString(id));
    }

    private void initExtras()
    {
        String uuid = getIntent().getStringExtra(EXTRA_TICKET_TO_EDIT_UUID);

        // if the uuid is empty we start creating a new one
        if (TextUtils.isEmpty(uuid))
        {
            m_editing = false;

            m_ticket = new Ticket();
            m_ticket.setScheduleTime((new Date()).getTime());
        }
        else
        {
            m_editing = true;
            m_ticket = AbstractSugarEntity.findByUuid(Ticket.class, uuid);
            assert m_ticket != null;

            m_edtClientName.setText(m_ticket.getClientName());
            m_edtAddress.setText(m_ticket.getAddress());
            m_edtScheduleDate.setText(DATE_FORMAT.format(new Date(m_ticket.getScheduleTime())));
            m_edtPhone.setText(m_ticket.getPhone());
            m_edtReasonForCall.setText(m_ticket.getReasonForCall());

            // the ticket must exist or the app will crash :)
            assert m_ticket != null;
        }
    }

    // endregion
}