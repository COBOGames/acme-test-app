package com.isucorp.acmecompanytest.ui.adapters;

import com.isucorp.acmecompanytest.Info;

@Info("Created by Ivan Faez Cobo on 21/5/2021")
public interface OnTicketEvents
{
    void onTicketClick(int position);
    void onTicketEdit(int position);
    void onTicketDelete(int position);
}
