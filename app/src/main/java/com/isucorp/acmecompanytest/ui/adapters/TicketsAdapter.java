package com.isucorp.acmecompanytest.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.isucorp.acmecompanytest.Info;
import com.isucorp.acmecompanytest.R;
import com.isucorp.acmecompanytest.entities.AbstractSugarEntity;
import com.isucorp.acmecompanytest.entities.Ticket;
import com.isucorp.acmecompanytest.helpers.PopupMenuHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

@Info("Created by Ivan Faez Cobo on 20/5/2021")
public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.ViewHolder>
{
    // region PRIVATE VARIABLES

    private OnTicketEvents m_ticketEvent;

    private List<Ticket> m_list = new ArrayList<>();
    private final LayoutInflater m_inflater;

    // endregion

    // region CONSTRUCTORS

    public TicketsAdapter(Context context)
    {
        m_inflater = LayoutInflater.from(context);
    }

    // endregion


    // region OVERRIDES

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = m_inflater.inflate(R.layout.item_ticket, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.onBind(m_list.get(position));
    }

    @Override
    public int getItemCount()
    {
        return m_list.size();
    }

    // endregion

    // region GETTER/SETTERS

    public OnTicketEvents getOnTicketEvents()
    {
        return m_ticketEvent;
    }

    public void setTicketEvents(OnTicketEvents event)
    {
        this.m_ticketEvent = event;
    }

    // endregion

    // region PUBLIC METHODS

    public Ticket getTicket(int position)
    {
        return m_list.get(position);
    }

    public void setNewList(List<Ticket> list)
    {
        m_list = list;
        notifyDataSetChanged();
    }

    public void removeItem(String uuid)
    {
        int position = getRequestPosition(uuid);
        removeItem(position);
    }

    public void removeItem(int position)
    {
        if (position != -1)
        {
            m_list.remove(position);
            notifyDataSetChanged();
        }
    }

    /**
     * Add the ticket at the start and notify the change.
     */
    public void insertAtStart(Ticket ticket)
    {
        if (m_list.size() == 0)
            m_list.add(ticket);
        else
            m_list.add(0, ticket);

        notifyDataSetChanged();
    }

    /**
     * Invoke the notifyItemChanged(position) for the item that contains the given uuid.
     * Not invoked if not found.
     */
    public void notifyItemChanged(String uuid)
    {
        Ticket ticket = AbstractSugarEntity.findByUuid(Ticket.class, uuid);

        // replace the request to the m_list and notify
        int position = getRequestPosition(uuid);
        if (position != -1)
        {
            m_list.set(position, ticket);
            notifyItemChanged(position);
        }
    }

    // endregion

    // region PRIVATE METHODS

    private int getRequestPosition(String uuid)
    {
        for (int i = 0; i < m_list.size(); i++)
            if (uuid.equals(m_list.get(i).getUuid()))
                return i;

        return -1;
    }

    // endregion

    // region NESTED TYPES

    static class ViewHolder extends RecyclerView.ViewHolder
    {
        // region PRIVATE VARIABLES

        private final TextView m_tvClientName;
        private final TextView m_tvAddress;

        // endregion

        ViewHolder(View itemView, TicketsAdapter adapter)
        {
            super(itemView);

            m_tvClientName = itemView.findViewById(R.id.tv_ticket_client_name);
            m_tvAddress = itemView.findViewById(R.id.tv_ticket_address);

            initEvents(adapter);
        }

        void onBind(Ticket ticket)
        {
            m_tvClientName.setText(ticket.getClientName());
            m_tvAddress.setText(ticket.getAddress());
        }

        protected void initEvents(final TicketsAdapter adapter)
        {
            // on ticket click event
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                {
                    OnTicketEvents event = adapter.getOnTicketEvents();
                    if (event != null)
                        event.onTicketClick(position);
                }
            });

            // btn options
            final View btnOptions = itemView.findViewById(R.id.btn_ticket_options);
            btnOptions.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION)
                {
                    OnTicketEvents event = adapter.getOnTicketEvents();
                    if (event == null)
                        return;

                    // show the options menu
                    PopupMenuHelper.show(itemView.getContext(), btnOptions, R.menu.ticket_options_popup,
                            item -> {
                                switch (item.getItemId())
                                {
                                    // edit clicked
                                    case R.id.action_edit:
                                    {
                                        event.onTicketEdit(position);
                                        return true;
                                    }
                                    // delete clicked
                                    case R.id.action_delete:
                                    {
                                        event.onTicketDelete(position);
                                        return true;
                                    }
                                }

                                return false;
                            });
                }
            });
        }
    }

    // endregion
}
