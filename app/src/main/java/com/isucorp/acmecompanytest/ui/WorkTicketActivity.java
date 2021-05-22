package com.isucorp.acmecompanytest.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;
import com.isucorp.acmecompanytest.entities.AbstractSugarEntity;
import com.isucorp.acmecompanytest.entities.Ticket;
import com.isucorp.acmecompanytest.helpers.ToastHelper;

import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

@Info("Created by Ivan Faez Cobo on 21/5/2021")
public class WorkTicketActivity extends AppCompatActivity
{
    // region CONSTANTS

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
     *
     * @param ticket If null we start the activity to create a new ticked. If not null, we start
     *               the activity to edit it.
     */
    public static void start(Activity fromActivity, Ticket ticket)
    {
        // create intent and put extras
        Intent intent = new Intent(fromActivity, WorkTicketActivity.class);

        // if not null start editing it
        if (ticket != null)
            intent.putExtra(EXTRA_TICKET_TO_EDIT_UUID, ticket.getUuid());

        fromActivity.startActivity(intent);
    }

    // endregion

    // region OVERRIDES

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_ticket);

        initExtras();
        initCachingVariables();
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
        ToastHelper.show("TODO Save");
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

        // region EVENTS

        findViewById(R.id.btn_get_directions).setOnClickListener(v -> {
            ToastHelper.show("TODO get direction");
        });

        // show date picker dialog when schedule date is clicked
        m_edtScheduleDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
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
            dialog.getDatePicker().setMinDate(c.getTimeInMillis()); // allow picking future dates only
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

            // the ticket must exist or the app will crash :)
            assert m_ticket != null;
        }
    }

    // endregion
}