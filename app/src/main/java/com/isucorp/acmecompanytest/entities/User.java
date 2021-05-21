package com.isucorp.acmecompanytest.entities;

import com.isucorp.acmecompanytest.Info;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

@Info("Created by Ivan Faez Cobo on 20/5/2021")
@Table(name = "user")
public class User extends AbstractSugarEntity
{
    // region PRIVATE VARIABLES

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    // endregion

    // region CONSTRUCTORS

    public User()
    {
        // required empty constructor for Sugar
    }

    // endregion

    // region GETTER/SETTERS

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    // endregion
}
