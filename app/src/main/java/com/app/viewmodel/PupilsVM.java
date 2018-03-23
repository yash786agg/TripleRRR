package com.app.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.Toast;
import com.app.BIAAssign.MyApplication;
import com.app.BIAAssign.R;
import com.app.extras.ConnectivityReceiver;
import com.app.extras.ConstantData;
import com.app.extras.MsgCallback;
import com.app.model.PupilsDataModel;
import com.app.roomDao.PupilsDataBase;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/*
 * Created by Yash on 23/3/18.
 */

public class PupilsVM extends AndroidViewModel
{
    private PupilsDataBase databaseInstance;
    private LiveData<List<PupilsDataModel>> pupilsList;
    private Disposable disposable;
    private MsgCallback<String> callback;

    public PupilsVM(@NonNull Application application)
    {
        super(application);

         /*
         *  Intialization of Database Instance.
         **/

        databaseInstance = PupilsDataBase.getDatabaseInstance(this.getApplication());

        /*
         *  Fetching all existing data from Room database for the user if the data is already available to the user.
         **/

        pupilsList = databaseInstance.pupilsDataDaoAccess().fetchAllPupils();
    }

    public LiveData<List<PupilsDataModel>> getPupilsList(MsgCallback<String> callback)
    {
        if (pupilsList == null)
        {
            /*
             *  Intialization of MutableLiveData.
             */
            pupilsList = new MutableLiveData<>();
        }

        this.callback = callback;

        /*
         *  Network connectivity check to check whether Internet is connected or not.
         */

        networkCheck();

        return pupilsList;
    }

    private void networkCheck()
    {
        if(ConnectivityReceiver.isNetworkAvailable(this.getApplication()))
        {
            /*
            * isNetworkAvailable will check whether device is connected to wifi or data connection
            * If yes then will move to next step otherwise display a message "Please Check Your Internet Connection"*/

            getData();
        }
        else
        {
            Toast.makeText(this.getApplication(), R.string.connectWifiDataConn,Toast.LENGTH_SHORT).show();
        }
    }

    private void getData()
    {
        // Do an asynchronous operation to fetch data using RxJava along with Retrofit

        /*
        * RxJava = OBSERVABLE + OBSERVER + SCHEDULERS
        * Observer: It consumes the data stream emitted by the observable and
        * Observer subscribe to the observable using subscribeOn() method to receive the data emitted by the observable.
        * In RxJava Schedulers.io() will execute the code on IO thread.*/

        disposable = MyApplication.apiService.getPuplisList(ConstantData.serviceURL + ConstantData.pupilsListURL)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<JsonObject>()
                {
                    @Override
                    public void accept(JsonObject jsonObject) throws Exception
                    {
                        if(jsonObject.toString() != null && !TextUtils.isEmpty(jsonObject.toString()))
                        {
                            JSONObject response = new JSONObject(jsonObject.toString());
                            setAllData(response);
                        }
                    }
                }, new Consumer<Throwable>()
                {
                    @Override
                    public void accept(Throwable throwable) throws Exception
                    {
                        if (throwable instanceof HttpException)
                        {
                            setErrorMsg(((HttpException)throwable).code());
                        }
                    }
                });


    }

    private void setErrorMsg(int code)
    {
        callback.msgCallback(this.getApplication().getResources().getString(R.string.fail));
        if(code == 404)
        {
            Toast.makeText(this.getApplication(), R.string.noMoreData,Toast.LENGTH_SHORT).show();
        }
        else if(code == 503 || code == 504)
        {
            Toast.makeText(this.getApplication(), R.string.noData,Toast.LENGTH_SHORT).show();
        }
        else if(code == 500)
        {
            Toast.makeText(this.getApplication(), R.string.somethingWentWring,Toast.LENGTH_SHORT).show();
        }
    }

    private void setAllData(JSONObject jsonObject)
    {
        try
        {
            if(jsonObject.getInt(this.getApplication().getResources().getString(R.string.itemCountParams)) >= 1)
            {
                // Passing data to DeliveryDataAsync for crud operation on database.
                new PupilsDataAsync(databaseInstance,this.getApplication()).execute(jsonObject);
            }
            else
            {
                Toast.makeText(this.getApplication(), R.string.noData,Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private static class PupilsDataAsync extends AsyncTask<JSONObject, Void, Void>
    {
        private PupilsDataBase db;
        private WeakReference<Context> contextRef;

        PupilsDataAsync(PupilsDataBase appDatabase,Context context)
        {
            db = appDatabase;
            contextRef = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(JSONObject ... jsonObject)
        {
            int itemCount,pupilId = 0;
            JSONObject response = jsonObject[0];
            Context context = contextRef.get();

            try
            {
                JSONArray jsonArray = response.getJSONArray(context.getResources().getString(R.string.itemsParams));

                for (int i = 0; i < jsonArray.length(); i++)
                {
                    PupilsDataModel pupilsDataModel = new PupilsDataModel();
                    JSONObject object = jsonArray.getJSONObject(i);

                    if(object.has(context.getResources().getString(R.string.pupilIdParams)))
                        if (!(object.isNull(context.getResources().getString(R.string.pupilIdParams))))
                        {
                            pupilsDataModel.setPupilId(object.getInt(context.getResources().getString(R.string.pupilIdParams)));
                            pupilId =  object.getInt(context.getResources().getString(R.string.pupilIdParams));
                        }

                    if(object.has(context.getResources().getString(R.string.countryParmas)))
                        if (!(object.isNull(context.getResources().getString(R.string.countryParmas))))
                            pupilsDataModel.setCountry(object.getString(context.getResources().getString(R.string.countryParmas)));

                    if(object.has(context.getResources().getString(R.string.nameParmas)))
                        if (!(object.isNull(context.getResources().getString(R.string.nameParmas))))
                            pupilsDataModel.setName(object.getString(context.getResources().getString(R.string.nameParmas)));

                    if(object.has(context.getResources().getString(R.string.imageParmas)))
                        if (!(object.isNull(context.getResources().getString(R.string.imageParmas))))
                            pupilsDataModel.setImage(object.getString(context.getResources().getString(R.string.imageParmas)));

                    if(object.has(context.getResources().getString(R.string.latitudeParmas)))
                        if (!(object.isNull(context.getResources().getString(R.string.latitudeParmas))))
                            pupilsDataModel.setLatitude(object.getDouble(context.getResources().getString(R.string.latitudeParmas)));

                    if(object.has(context.getResources().getString(R.string.longitudeParmas)))
                        if (!(object.isNull(context.getResources().getString(R.string.longitudeParmas))))
                            pupilsDataModel.setLongitude(object.getDouble(context.getResources().getString(R.string.longitudeParmas)));

                    itemCount = db.pupilsDataDaoAccess().getPupilsById(pupilId).getCount();

                    //Now access all the methods defined in DaoAccess with PupilDatabase object

                    if(itemCount == 0)
                    {
                        // insertPupils to insert new data in Database.
                        db.pupilsDataDaoAccess().insertPupils(pupilsDataModel);
                    }
                    else
                    {
                        // updatePupils to update existing data in Database.
                        db.pupilsDataDaoAccess().updatePupils(pupilsDataModel);
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCleared()
    {
        // Note: We do not need to call super.onCleared() because the base implementation is empty.
        if(disposable != null && !disposable.isDisposed())
        {
            disposable.dispose();// To dispose or unSuscribe the RxJava Observer
        }
    }
}
