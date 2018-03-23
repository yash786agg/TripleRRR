package com.app.BIAAssign;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.app.adapter.RecyclerViewAdapter;
import com.app.extras.MsgCallback;
import com.app.extras.RcylcVItemClick;
import com.app.extras.RecyclerItemTouchHelper;
import com.app.model.PupilsDataModel;
import com.app.viewmodel.DeletePupilVM;
import com.app.viewmodel.PupilsVM;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements RcylcVItemClick,RecyclerItemTouchHelper.RecyclerItemTouchHelperListener
    ,MsgCallback<String>,View.OnClickListener
{
    //Application supports the latest Android Architecture Component ie:
    //Code Structure: MVVM (Model View View Controller) with Live Data and View Model are Used.
    //For offline storage : Android Latest "Room" database is used.
    //For Network Call: RxJava with Retrofit along with Butterknife are used

    static
    {   // To provide support of vector icon below sdk version 21.
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    // UI Widgets.
    @BindView(R.id.backBtn) ImageView backBtn;
    @BindView(R.id.pupilRcylv) RecyclerView pupilRcylv;
    @BindView(R.id.retry) Button retry;

    // Labels
    private ArrayList<PupilsDataModel> pupilsArrayList;
    private ProgressDialog progress;
    private RecyclerViewAdapter rcylcViewAdapter;
    private int deletedPosition;
    private PupilsDataModel deletedData;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Intialization of ButterKnife */
        ButterKnife.bind(this);

          /* Intialization of ProgressDialog */
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setCancelable(false);

        backBtn.setVisibility(View.GONE);

        /*  Intialization of ArrayList */
        pupilsArrayList = new ArrayList<>();

        /*  Intialization of RecyclerView Adapter */
        rcylcViewAdapter = new RecyclerViewAdapter(this,pupilsArrayList);

        /*We used LinearLayoutManager to show Data in List Form */
        pupilRcylv.setLayoutManager(new LinearLayoutManager(this));
        rcylcViewAdapter.setOnItemClickListener(this);
        pupilRcylv.setAdapter(rcylcViewAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.Callback callback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(pupilRcylv);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        getAllPupils();// Get all Pupils data.
    }

    private void getAllPupils()
    {
        // Retry button to fetch the data again if no data get from the server.
        if(retry.getVisibility() == View.VISIBLE)
            retry.setVisibility(View.GONE);

        progress.show();

        // Intialize ViewModel
        // Basic Functionality of ViewModel is to observer the data change and lifecycle in conscious way and survive configuration
        // changes on screen rotations.
        PupilsVM pupilsVM = ViewModelProviders.of(this).get(PupilsVM.class);
        pupilsVM.getPupilsList(this).observe(this, new Observer<List<PupilsDataModel>>()
        {
            @Override
            public void onChanged(@Nullable List<PupilsDataModel> pupilsDataModels)
            {
                if (pupilsDataModels != null)
                {
                    // Getting all the data from ViewModel Observer
                    if(pupilsDataModels.size() >= 1)
                    {
                        pupilsArrayList = (ArrayList<PupilsDataModel>) pupilsDataModels;

                        // Update the UI.
                        rcylcViewAdapter.updateData(pupilsArrayList);
                        dimissProgress();
                    }
                }
            }
        });
    }

    @Override//Item Click listener for recyclerview item
    public void onItemClick(View view, int position)
    {
           /*
            * Passing of selected data in a bundle will help as to take data to next screen
            * As it reduce time and effect to call the Api Request to server for each pupils.
            * */
        Bundle bundle = new Bundle();
        Intent intent = new Intent(this,PupilsDetail.class);
        //Add the bundle to the intent
        bundle.putInt(getString(R.string.pupilIdParams), pupilsArrayList.get(position).getPupilId());
        bundle.putString(getString(R.string.countryParmas), pupilsArrayList.get(position).getCountry());
        bundle.putString(getString(R.string.nameParmas), pupilsArrayList.get(position).getName());
        bundle.putString(getString(R.string.imageParmas), pupilsArrayList.get(position).getImage());
        bundle.putDouble(getString(R.string.latitudeParmas), pupilsArrayList.get(position).getLatitude());
        bundle.putDouble(getString(R.string.longitudeParmas), pupilsArrayList.get(position).getLongitude());

        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override// On swipe delete item on swipe of item in recyclerview.
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {
        // remove the item from recycler view
        deletedPosition = viewHolder.getAdapterPosition();

        int pupilId = pupilsArrayList.get(deletedPosition).getPupilId();

        rcylcViewAdapter.notifyItemRemoved(deletedPosition);
        deletedData = pupilsArrayList.remove(deletedPosition);

        // Intialize DeletePupil ViewModel
        DeletePupilVM deletePupilVM = ViewModelProviders.of(this).get(DeletePupilVM.class);
        deletePupilVM.deletePupil(pupilId,this);
    }

    @Override// Getting callback message if some error occur on api request
    public void msgCallback(String ret)
    {
        if(ret.equalsIgnoreCase(getString(R.string.error)))
            undoDeletedPupil();
        else if(ret.equalsIgnoreCase(getString(R.string.fail)))
        {
            if(pupilsArrayList.size() == 0)
                retry.setVisibility(View.VISIBLE);
        }

        dimissProgress();
    }

    private void undoDeletedPupil()// Undo Pupil Item if Pupil cannpt be deleted.
    {
        pupilsArrayList.add(deletedPosition, deletedData);
        rcylcViewAdapter.notifyItemInserted(deletedPosition);
        rcylcViewAdapter.notifyDataSetChanged();
    }

    @Override//Click listener on retry to getAllPupils data again in case of Failure
    @OnClick({R.id.retry})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.retry:

                getAllPupils();

                break;
        }
    }

    private void dimissProgress()
    {
        if(progress != null)
        {
            progress.dismiss();
            progress.cancel();
        }
    }
}
