package com.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.app.BIAAssign.MyApplication;
import com.app.BIAAssign.R;
import com.app.extras.ConstantData;
import com.app.extras.MsgCallback;
import com.app.roomDao.PupilsDataBase;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * Created by Yash on 23/3/18.
 */

public class DeletePupilVM extends AndroidViewModel
{
    private PupilsDataBase databaseInstance;
    private int errorCode = 0;

    public DeletePupilVM(@NonNull Application application)
    {
        super(application);

         /*
         *  Intialization of Database Instance.
         **/

        databaseInstance = PupilsDataBase.getDatabaseInstance(this.getApplication());
    }

    public void deletePupil(final int pupilId, final MsgCallback<String> callback)
    {
        Call<JsonObject> call = MyApplication.apiService.deletePupil(ConstantData.serviceURL + ConstantData.pupilsListURL+"/"+pupilId);

        // execute network request for deleting the PupilId
        call.enqueue(new Callback<JsonObject>()
        {
            @Override
            public void onResponse(Call<JsonObject>call, Response<JsonObject> response)
            {
                if(response.code() == 204 || response.code() == 404)
                {
                    //delete pupil
                    deleteItem(pupilId);
                }
                else if(response.code() == 400)
                {
                    errorCode = 400;
                    setErrorMsg(callback);
                }
                else
                {
                    setErrorMsg(callback);
                }
            }

            @Override
            public void onFailure(Call<JsonObject>call, Throwable t)
            {
                setErrorMsg(callback);
            }
        });
    }

    private void setErrorMsg(MsgCallback<String> callback)
    {
        if(errorCode == 400)
        {
            Toast.makeText(this.getApplication(), R.string.pupilCannotDeleted,Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this.getApplication(), R.string.somethingWentWring,Toast.LENGTH_SHORT).show();
        }
        callback.msgCallback(this.getApplication().getResources().getString(R.string.error));
    }

    private void deleteItem(int pupilId)
    {
        new deleteAsyncTask(databaseInstance).execute(pupilId);
    }

    private static class deleteAsyncTask extends AsyncTask<Integer, Void, Void>
    {
        private PupilsDataBase db;

        deleteAsyncTask(PupilsDataBase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Integer... params)
        {
            int itemCount = db.pupilsDataDaoAccess().getPupilsById(params[0]).getCount();

            //Now delete the pupilId defined in DaoAccess with PupilsDatabase object

            if(itemCount == 1)
            {
                db.pupilsDataDaoAccess().deletePupilsById(params[0]);
            }

            return null;
        }
    }
}
