package com.isucorp.acmecompanytest.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;
import com.isucorp.acmecompanytest.entities.AbstractSugarEntity;
import com.isucorp.acmecompanytest.entities.Ticket;
import com.isucorp.acmecompanytest.helpers.DialogHelper;
import com.isucorp.acmecompanytest.helpers.ToastHelper;
import com.isucorp.acmecompanytest.ui.adapters.OnTicketEvents;
import com.isucorp.acmecompanytest.ui.adapters.TicketsAdapter;
import com.orm.SugarRecord;

import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@Info("Created by Ivan Faez Cobo on 20/5/2021")
public class MainActivity extends AppCompatActivity
{
    // region

    private static final int RC_WORK_TICKET = 910;

    // endregion

    // region PRIVATE VARIABLES

    private TicketsAdapter m_adapter;

    // endregion

    // region CACHING VARIABLES

    private View m_loadingIndicator;
    private RecyclerView m_rv;
    private View m_containerNoItems;

    // endregion

    // region OVERRIDES

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initCachingVariables();
        initRecyclerView();
        new LoadTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add_ticket:
            {
                // we pass null to create a new one
                WorkTicketActivity.start(this, null, RC_WORK_TICKET);
                return true;
            }
            case R.id.action_work_ticket:
            {
                // if no tickets show message
                if (m_adapter.getItemCount() == 0)
                {
                    ToastHelper.show(getString(R.string.main_tickets_empty));
                }
                else
                {
                    // edit the most recent ticket created
                    Ticket ticket = m_adapter.getTicket(0);
                    WorkTicketActivity.start(this, ticket, RC_WORK_TICKET);
                }
                return true;
            }
            case R.id.action_get_directions:
            {
                MapsActivity.start(this, null); // we pass an empty address
                return true;
            }
            case R.id.action_sync_calendar:
            {
                if (m_adapter.getItemCount() == 0)
                {
                    ToastHelper.show(getString(R.string.main_tickets_empty));
                }
                else
                {
                    // show a confirm dialog to sync
                    DialogHelper.showConfirm(
                            MainActivity.this,
                            DialogHelper.NO_TEXT,
                            getString(R.string.confirm_sync_calendar),
                            (dialog, which) -> {
                                if (which == DialogInterface.BUTTON_POSITIVE)
                                    syncCalendar();
                            },
                            "Sync",
                            DialogHelper.DEFAULT_TEXT
                    );
                }

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == RC_WORK_TICKET && resultCode == RESULT_OK)
        {
            if (data == null) return;

            boolean edited = data.getBooleanExtra(WorkTicketActivity.EXTRA_TICKET_WAS_EDITED, false);
            String uuid = data.getStringExtra(WorkTicketActivity.EXTRA_TICKET_UUID);

            // if edited just update ui
            if (edited)
            {
                m_adapter.notifyItemChanged(uuid);

                // show toast message
                ToastHelper.show(getString(R.string.msg_ticket_edited));
            }
            else // created
            {
                // find the new entity and insert at start of the list
                Ticket ticket = AbstractSugarEntity.findByUuid(Ticket.class, uuid);

                m_adapter.insertAtStart(ticket);        // insert at start
                m_rv.smoothScrollToPosition(0);         // scroll to it
                updateBodyContainersVisibility();       // update containers

                // show toast message
                ToastHelper.show(getString(R.string.msg_ticket_added));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // endregion

    // region PRIVATE METHODS

    private void syncCalendar()
    {
        try
        {
            for (int i = 0; i < m_adapter.getItemCount(); i++)
                insertOrEditTicketInCalendar(m_adapter.getTicket(i));

            ToastHelper.show("Calendar synced!");
        }
        catch (Exception e)
        {
            Log.e("Error", "Sync calendar", e);
            ToastHelper.show("Error syncing with the calendar: " + e.getMessage());
        }
    }

    private void insertOrEditTicketInCalendar(Ticket ticket)
    {
        int ticketId = ticket.getUuid().hashCode();
        ContentResolver contentResolver = getContentResolver();

        // check if edit or insert
        boolean edit = false;
        final Cursor cursor = contentResolver.query(
                CalendarContract.Events.CONTENT_URI,
                new String[]{CalendarContract.Events._ID},
                null,
                null,
                null
        );
        {
            while (cursor.moveToNext())
            {
                final long id = cursor.getLong(0);
                if (id == ticketId)
                {
                    edit = true;
                    break;
                }
            }
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events._ID, ticketId);
        values.put(CalendarContract.Events.DTSTART, ticket.getScheduleTime());
        values.put(CalendarContract.Events.TITLE, ticket.getClientName());
        values.put(CalendarContract.Events.DESCRIPTION, "Address: " + ticket.getAddress());
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // default calendar
        values.put(CalendarContract.Events.HAS_ALARM, 1); // enable alarm
        values.put(CalendarContract.Events.DURATION, "+P1H"); // set period for 1 hour
        values.put(CalendarContract.Events.ALL_DAY, 1);

        // edit or insert event to calendar
        if (edit)
            contentResolver.update(CalendarContract.Events.CONTENT_URI, values, "_id=" + ticketId, null);
        else
            contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
    }

    /**
     * If we have tickets the m_rv is shown and the
     * m_containerNoItems is hided. The opposite is made if no tickets.
     */
    private void updateBodyContainersVisibility()
    {
        boolean hasTickets = (m_adapter.getItemCount() != 0);
        if (hasTickets)
        {
            m_rv.setVisibility(View.VISIBLE);
            m_containerNoItems.setVisibility(View.GONE);
        }
        else
        {
            m_rv.setVisibility(View.GONE);
            m_containerNoItems.setVisibility(View.VISIBLE);
        }
    }

    private void initCachingVariables()
    {
        m_loadingIndicator = findViewById(R.id.loading_indicator);
        m_containerNoItems = findViewById(R.id.container_no_items);
    }

    private void initRecyclerView()
    {
        m_rv = findViewById(R.id.rv_tickets);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        m_rv.setHasFixedSize(true);

        // use a linear layout manager
        m_rv.setLayoutManager(new LinearLayoutManager(this));

        // init and add the adapter
        m_adapter = new TicketsAdapter(this);
        initAdapterEvents();
        m_rv.setAdapter(m_adapter);

        // set vertical divider
        m_rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void initAdapterEvents()
    {
        m_adapter.setTicketEvents(new OnTicketEvents()
        {
            @Override
            public void onTicketClick(int position)
            {
                // edit on click
                final Ticket ticket = m_adapter.getTicket(position);
                WorkTicketActivity.start(MainActivity.this, ticket, RC_WORK_TICKET);
            }

            @Override
            public void onTicketEdit(int position)
            {
                final Ticket ticket = m_adapter.getTicket(position);
                WorkTicketActivity.start(MainActivity.this, ticket, RC_WORK_TICKET);
            }

            @Override
            public void onTicketDelete(int position)
            {
                final Ticket ticket = m_adapter.getTicket(position);

                // show a confirm dialog to delete
                DialogHelper.showConfirm(
                        MainActivity.this,
                        DialogHelper.NO_TEXT,
                        getString(R.string.confirm_delete_this_ticket),
                        (dialog, which) -> {
                            // delete if positive clicked
                            if (which == DialogInterface.BUTTON_POSITIVE)
                            {
                                try
                                {
                                    // delete the ticket from the database and from the recycler view
                                    ticket.delete();
                                    m_adapter.removeItem(position);

                                    // update containers
                                    updateBodyContainersVisibility();
                                }
                                catch (Exception e)
                                {
                                    ToastHelper.show("Error deleting ticket");
                                }
                            }
                        },
                        getString(R.string.action_delete),
                        DialogHelper.DEFAULT_TEXT
                );
            }
        });
    }

    // endregion

    // region NESTED TYPES

    private class LoadTask extends AsyncTask<Void, Void, List<Ticket>>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();

            m_loadingIndicator.setVisibility(View.VISIBLE); // show loading
            m_containerNoItems.setVisibility(View.GONE);    // hide no items container
        }

        @Override
        protected void onPostExecute(List<Ticket> list)
        {
            super.onPostExecute(list);

            m_loadingIndicator.setVisibility(View.GONE); // hide loading
            m_adapter.setNewList(list);                  // assign the new list
            updateBodyContainersVisibility();            // update containers
        }

        @Override
        protected List<Ticket> doInBackground(Void... params)
        {
            List<Ticket> list = SugarRecord.listAll(Ticket.class);

            // reverse the order
            Collections.reverse(list);
            return list;

            // region CREATE A QUICK TEST LIST
            /*
            List<Ticket> testList = new ArrayList<>();
            for (int i = 0; i < 25; i++)
            {
                Ticket ticket = new Ticket();
                ticket.setClientName("Hello " + i);
                ticket.setAddress("World " + i);

                testList.add(ticket);
            }

            return testList;
            */
            // endregion
        }
    }

    // endregion
}