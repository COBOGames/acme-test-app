package com.isucorp.acmecompanytest.entities;

import android.text.TextUtils;

import com.orm.SugarRecord;
import com.orm.dsl.Column;

import java.util.List;
import java.util.UUID;

public abstract class AbstractSugarEntity extends SugarRecord
{
    // region CONSTANTS

    private static final String COLUMN_NAME_UUID = "uuid";
    private static final String WHERE_UUID = COLUMN_NAME_UUID + " = ?";

    // endregion

    // region PRIVATE VARIABLES

    @Column(name = COLUMN_NAME_UUID)
    protected String uuid;

    // endregion

    // region GETTER/SETTERS

    public boolean hasUuid()
    {
        return !TextUtils.isEmpty(uuid);
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String m_uuid)
    {
        this.uuid = m_uuid;
    }

    public void setRandomUuid()
    {
        uuid = UUID.randomUUID().toString();
    }

    // endregion

    // region HELPER METHODS

    public static <T extends AbstractSugarEntity> T findByUuid(Class<T> classOfT, String uuid)
    {
        List<T> list = SugarRecord.find(classOfT, WHERE_UUID, uuid);
        return list.isEmpty() ? null : list.get(0);
    }

    // endregion

    // region OVERRIDES

    @Override
    public boolean equals(Object o)
    {
        // If the object is compared with itself then return true
        if (o == this)
            return true;

        if (!(o instanceof AbstractSugarEntity))
            return false;

        // typecast o to AbstractEntity so that we can compare data members
        AbstractSugarEntity a = (AbstractSugarEntity) o;

        // Compare the data members and return accordingly
        return TextUtils.equals(getUuid(), a.getUuid());
    }

    /**
     * Assigns a random uuid if the uuid is empty.
     */
    @Override
    public long save()
    {
        if (TextUtils.isEmpty(uuid))
            setRandomUuid();

        return super.save();
    }

    // endregion

}
