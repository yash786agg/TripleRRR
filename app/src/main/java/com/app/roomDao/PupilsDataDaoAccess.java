package com.app.roomDao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;
import com.app.model.PupilsDataModel;
import java.util.List;
import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/*
 * Created by Yash on 23/3/18.
 */

@Dao
//Data Access Object ,it is a main component of Room and are responsible for defining the methods that access the database.
public interface PupilsDataDaoAccess
{
    /**
     * Fetch all pupils item from the database.
     */

    @Query("SELECT * FROM PupilsDataModel")
    LiveData<List<PupilsDataModel>> fetchAllPupils();

    /**
     * Inserts new pupils item into the database.
     */

    @Insert(onConflict = REPLACE)
    void insertPupils(PupilsDataModel pupilsData);

    /**
     * Update the item. The item is identified by the pupilId .
     */

    @Update(onConflict = REPLACE)
    void updatePupils(PupilsDataModel pupilsData);

    /**
     * Counts the number of items in the table.
     *
     * @return The number of pupils list.
     */

    @Query("SELECT * FROM PupilsDataModel where pupilId = :pupilId")
    Cursor getPupilsById(int pupilId);

    /**
     * delete the item. The item is identified by the pupilId .
     */

    @Query("DELETE FROM PupilsDataModel where pupilId = :pupilId")
    void deletePupilsById(int pupilId);
}
