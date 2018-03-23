package com.app.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

/*
 * Created by Yash on 23/3/18.
 */

@Entity
//Entity represents a class that holds a database row For each entity, a database table is created to hold the items.
public class PupilsDataModel implements Parcelable
{
    /* PupilsDataModel is Parcelable
    *  1- Parcelable is well documented in the Android SDK; serialization on the other hand is available in Java
	   2- Parcelable creates less garbage objects in comparison to Serialization.
	   3- Android Parcelable came out to be faster than the Java Serialization technique*/

    /** The unique ID  of the Database */

    @PrimaryKey
    @ColumnInfo(name = "pupilId")
    private int pupilId;

    /** The name of the country column. */
    @ColumnInfo(name = "country")
    private String country;

    /** The name of the name column. */
    @ColumnInfo(name = "name")
    private String name;

    /** The name of the image column. */
    @ColumnInfo(name = "image")
    private String image;

    /** The name of the latitude column. */
    @ColumnInfo(name = "latitude")
    private double latitude;

    /** The name of the longitude column. */
    @ColumnInfo(name = "longitude")
    private double longitude;


    public PupilsDataModel()
    {}

    public int getPupilId() {
        return pupilId;
    }

    public void setPupilId(int pupilId) {
        this.pupilId = pupilId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString()
    {
        return "PupilsDataModel{" +
                "pupilId='" + pupilId + '\'' +
                "country='" + country + '\'' +
                "name='" + name + '\'' +
                "image='" + image + '\'' +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(pupilId);
        dest.writeString(country);
        dest.writeString(name);
        dest.writeString(image);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public PupilsDataModel(Parcel in)
    {
        pupilId = in.readInt();
        country = in.readString();
        name = in.readString();
        image = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<PupilsDataModel> CREATOR = new Creator<PupilsDataModel>() {
        @Override
        public PupilsDataModel createFromParcel(Parcel in) {
            return new PupilsDataModel(in);
        }

        @Override
        public PupilsDataModel[] newArray(int size) {
            return new PupilsDataModel[size];
        }
    };
}
