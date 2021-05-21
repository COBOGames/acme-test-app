package com.isucorp.acmecompanytest.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;
import com.isucorp.acmecompanytest.entities.AbstractSugarEntity;
import com.isucorp.acmecompanytest.entities.Ticket;

import java.util.Date;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

@Info("Created by Ivan Faez Cobo on 21/5/2021")
public class WorkTicketActivity extends AppCompatActivity
{
    // region PRIVATE VARIABLES

    private Ticket m_ticket;
    private boolean m_editing;

    // endregion

    // region CONSTANTS

    /**
     * Extra of String type. <br/>
     * Indicates the uuid of the {@link Ticket} to edit.
     */
    private static final String EXTRA_TICKET_TO_EDIT_UUID = "8dc84aff433a48f4896ccacaf3240dd8";

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
        updateToolbarTitle();

        // show go back arrow in the toolbar
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem i)
    {
        if (i.getItemId() == android.R.id.home)
        {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(i);
    }

    // endregion

    // region PRIVATE METHODS

    private void updateToolbarTitle()
    {
        int id = m_editing ?  R.string.work_ticked_edit_title : R.string.action_new_ticket;
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