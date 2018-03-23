package com.app.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.app.BIAAssign.R;
import com.app.extras.RcylcVItemClick;
import com.app.model.PupilsDataModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by Yash on 23/3/18.
 */


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
{
    private ArrayList<PupilsDataModel> pupilsArrayList;
    private LayoutInflater layoutInflater;
    private Context context;
    private RcylcVItemClick clickListener;

    public RecyclerViewAdapter(Context context, ArrayList<PupilsDataModel> pupilsArrayList)
    {
        /*
         * RecyclerViewAdapter Constructor to Initialize Data which we get from MainActivity
         */

        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        this.pupilsArrayList = pupilsArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        /*
         * LayoutInflater is used to Inflate the view
         * from adapter_layout
         * for showing data in RecyclerView
         */

        View view = layoutInflater.inflate(R.layout.adapter_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapter.MyViewHolder holder, final int position)
    {
        /*
         * onBindViewHolder is used to Set all the respective data
         * to Textview or Imageview form pupilsArrayList
         * ArrayList Object.
         */
        PupilsDataModel pupilsDataModel = pupilsArrayList.get(position);

        if (!TextUtils.isEmpty(pupilsDataModel.getName()))
        {
            String name = context.getResources().getString(R.string.name)+" "+pupilsDataModel.getName();
            holder.nameTxt.setText(name);
        }


        if (!TextUtils.isEmpty(pupilsDataModel.getCountry()))
        {
            String country = context.getResources().getString(R.string.country)+" "+pupilsDataModel.getCountry();
            holder.countrytxt.setText(country);
        }

        if (!TextUtils.isEmpty(pupilsDataModel.getImage()))
        {
            Picasso.with(context)
                    .load(pupilsDataModel.getImage())
                    .resize(150,100)
                    .into(holder.flagImgv, new Callback()
                    {
                        @Override
                        public void onSuccess()
                        {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            {
                                holder.flagImgv.setBackground(null);
                            }
                            else
                            {
                                holder.flagImgv.setBackgroundDrawable(null);
                            }

                            holder.flagImgProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError()
                        {
                            holder.flagImgProgress.setVisibility(View.GONE);
                        }
                    });
        }
    }

    @Override
    public int getItemCount()
    {
        /*
         * getItemCount is used to get the size of respective pupilsArrayList
         */

        return pupilsArrayList.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        /*
         * Return the view type of the item at position for the purposes of view recycling.
         */

        return position;
    }

    public void updateData(ArrayList<PupilsDataModel> pupilsArrayList)
    {
        /*
         * updateData method is to add items in the pupilsArrayList and notify the adapter for the data change.
         */

        this.pupilsArrayList = pupilsArrayList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(final RcylcVItemClick mItemClickListener)
    {
        this.clickListener = mItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        @BindView(R.id.flagImgv) ImageView flagImgv;
        @BindView(R.id.nameTxt) TextView nameTxt;
        @BindView(R.id.countrytxt) TextView countrytxt;
        @BindView(R.id.flagImgProgress) ProgressBar flagImgProgress;

        /*
         * MyViewHolder is used to Initializing the view.
         */

        MyViewHolder(View itemView)
        {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            if (clickListener != null)
            {
                clickListener.onItemClick(view,getAdapterPosition());
            }
        }
    }
}

