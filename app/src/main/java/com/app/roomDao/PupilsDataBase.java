package com.app.roomDao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import com.app.BIAAssign.R;
import com.app.model.PupilsDataModel;

/*
 * Created by Yash on 23/3/18.
 */

@Database(entities = {PupilsDataModel.class}, version = 1)
public abstract class PupilsDataBase extends RoomDatabase
{
    /**
     * @return The DAO for the PupilsDatabase table.
     */
    public abstract PupilsDataDaoAccess pupilsDataDaoAccess();

    /** The PupilsDataBase instance */
    private static PupilsDataBase mInstance;

    /**
     * Gets the singleton instance of PupilsDataBase.
     *
     * @param context The context.
     * @return The singleton instance of PupilsDataBase.
     */
    public static PupilsDataBase getDatabaseInstance(Context context)
    {
        if (mInstance == null)
        {                                                                           /* The name of the Database table. */
            mInstance = Room.databaseBuilder(context.getApplicationContext(), PupilsDataBase.class, context.getResources().getString(R.string.databaseTableName)).build();
        }

        return mInstance;
    }
}
