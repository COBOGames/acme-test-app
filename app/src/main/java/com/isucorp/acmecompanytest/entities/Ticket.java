package com.isucorp.acmecompanytest.entities;

import com.isucorp.acmecompanytest.Info;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

@Info("Created by Ivan Faez Cobo on 20/5/2021")
@Table(name = "ticket")
public class Ticket extends AbstractSugarEntity
{
    // region PRIVATE VARIABLES

    @Column(name = "clientName")
    private String clientName;

    @Column(name = "address")
    private String address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "schedule_time")
    private long scheduleTime;

    @Column(name = "reason_for_call")
    private String reasonForCall;

    // endregion

    // region CONSTRUCTORS

    public Ticket()
    {
        // required empty constructor for Sugar
    }

    // endregion

    // region GETTER/SETTERS

    public String getClientName()
    {
        return clientName;
    }

    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getPhone()
    {
        return phone;
    }

    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    public long getScheduleTime()
    {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime)
    {
        this.scheduleTime = scheduleTime;
    }

    public String getReasonForCall()
    {
        return reasonForCall;
    }

    public void setReasonForCall(String reasonForCall)
    {
        this.reasonForCall = reasonForCall;
    }

    // endregion
}
