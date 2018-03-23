package com.app.BIAAssign;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
 * Created by amura on 23/3/18.
 */

public class PupilsDetail extends AppCompatActivity implements OnMapReadyCallback,View.OnClickListener
{
    // UI Widgets.
    @BindView(R.id.appTitle) TextView appTitle;
    @BindView(R.id.pupilImg) ImageView pupilImg;
    @BindView(R.id.pupilName) TextView pupilName;
    @BindView(R.id.pupilCountry) TextView pupilCountry;
    @BindView(R.id.pupilImgProgress) ProgressBar pupilImgProgress;

    // Labels
    private GoogleMap googleMap;
    private double latitude,longitude;
    private String name,country;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pupils_details_layout);

        ButterKnife.bind(this);

        /*
         *  Intialization of MapFragment.
         */

        MapFragment mapLayout = (MapFragment) getFragmentManager().findFragmentById(R.id.mapLayout);

        appTitle.setText(getString(R.string.pupilDetail));

        // Getting data in bundle form Previous Screen.
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            String imgUrl = bundle.getString(getString(R.string.imageParmas));
            if (!TextUtils.isEmpty(imgUrl))
            {
                Picasso.with(this)
                        .load(imgUrl)
                        .into(pupilImg, new Callback()
                        {
                            @Override
                            public void onSuccess()
                            {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                                {
                                    pupilImg.setBackground(null);
                                }
                                else
                                {
                                    pupilImg.setBackgroundDrawable(null);
                                }

                                pupilImgProgress.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError()
                            {
                                pupilImgProgress.setVisibility(View.GONE);
                            }
                        });
            }

            name = bundle.getString(getString(R.string.nameParmas));
            if (!TextUtils.isEmpty(name))
            {
                pupilName.setText(name);
            }

            country = bundle.getString(getString(R.string.countryParmas));
            if (!TextUtils.isEmpty(country))
            {
                pupilCountry.setText(country);
            }

            latitude = bundle.getDouble(getString(R.string.latitudeParmas));
            longitude = bundle.getDouble(getString(R.string.longitudeParmas));

            if(latitude != 0.0 && longitude != 0.0)
            {
                mapLayout.getMapAsync(this);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map)
    {
        // To create the sign in key for google map --> https://developers.google.com/maps/documentation/android-api/signup
        googleMap = map;

         /*
         *  Intialization of Latitude and Longitude.
         */
        LatLng latlng_object = new LatLng(latitude, longitude);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng_object, 10));

        /*
         *  Adding of marker to show the location based on latlng_object with address..
         */

        map.addMarker(new MarkerOptions().title(name).snippet(country).position(latlng_object));
        map.moveCamera(CameraUpdateFactory.newLatLng(latlng_object));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
        map.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(googleMap != null)
        {
            googleMap.clear();
        }
    }

    @Override
    @OnClick({R.id.backBtn})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.backBtn:

                moveToPreviousScreen();

                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        moveToPreviousScreen();
    }

    private void moveToPreviousScreen()
    {
        finish();
    }
}
