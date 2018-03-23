package com.app.apirequest;

import com.google.gson.JsonObject;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Url;

/*
 * Created by Yash on 23/3/18.
 */

public interface ApiInterface
{
    /*
    * Observable packs the data that can be passed around from one thread to another thread.
    * They basically emit the data periodically or only once in their life cycle based on their configurations.
    * */

    @GET// @GET is the type of request
    Observable<JsonObject> getPuplisList(@Url String url);//@Url We will pass the Url using @Url tag as it will ignore the base url

    @DELETE// @GET is the type of request
    Call<JsonObject> deletePupil(@Url String url);//@Url We will pass the Url using @Url tag as it will ignore the base url

}
