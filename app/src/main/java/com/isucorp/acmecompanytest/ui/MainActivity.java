package com.isucorp.acmecompanytest.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;
import com.isucorp.acmecompanytest.entities.Ticket;
import com.isucorp.acmecompanytest.helpers.ToastHelper;
import com.isucorp.acmecompanytest.ui.adapters.OnTicketEvents;
import com.isucorp.acmecompanytest.ui.adapters.TicketsAdapter;
import com.orm.SugarRecord;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

@Info("Created by Ivan Faez Cobo on 20/5/2021")
public class MainActivity extends AppCompatActivity
{
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
                WorkTicketActivity.start(this, null); // we pass null to create a new one
                return true;
            }
            case R.id.action_work_ticket:
            {
                ToastHelper.show("TODO go to 'Work ticket'");
                return true;
            }
            case R.id.action_get_directions:
            {
                ToastHelper.show("TODO go to 'Get directions'");
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    // endregion

    // region PRIVATE METHODS

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
                final Ticket ticket = m_adapter.getTicket(position);
                ToastHelper.show("TODO click at " + position);
            }

            @Override
            public void onTicketEdit(int position)
            {
                final Ticket ticket = m_adapter.getTicket(position);
                ToastHelper.show("TODO edit at " + position);
            }

            @Override
            public void onTicketDelete(int position)
            {
                final Ticket ticket = m_adapter.getTicket(position);
                ToastHelper.show("TODO delete at " + position);
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
            return SugarRecord.listAll(Ticket.class);

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