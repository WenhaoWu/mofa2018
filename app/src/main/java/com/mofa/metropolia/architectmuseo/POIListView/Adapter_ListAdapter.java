package com.mofa.metropolia.architectmuseo.POIListView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mofa.metropolia.architectmuseo.LandingPage.Adapter_RVAdapter;
import com.mofa.metropolia.architectmuseo.Object_POI;
import com.mofa.metropolia.architectmuseo.POIDetail.Activity_POIActivity;
import com.mofa.metropolia.architectmuseo.R;
import com.mofa.metropolia.architectmuseo.Utils.ImageLoader;

import java.util.List;
import java.util.Locale;

/**
 * Created by wenhao on 25.11.2017.
 */

class Adapter_ListAdapter extends RecyclerView.Adapter<Adapter_ListAdapter.mViewHolder> {

    private final Context context;
    private int sortID;
    private final List<Object_POI> values;

    Adapter_ListAdapter(Context context, List<Object_POI> objects, int sortID) {
        this.context = context;
        this.values = objects;
        this.sortID = sortID;
    }

    static class mViewHolder extends RecyclerView.ViewHolder{
        ImageView imgV;
        TextView title;
        TextView subTitle;
        TextView rateScore;
        RatingBar ratingBar;

        View container;

        mViewHolder(View itemView) {
            super(itemView);
            this.container = itemView;
            this.imgV = (ImageView) itemView.findViewById(R.id.POIRowImage);
            this.title = (TextView)itemView.findViewById(R.id.POIRowFriLine);
            this.subTitle = (TextView)itemView.findViewById(R.id.POIRowSecLine);
            this.rateScore = (TextView)itemView.findViewById(R.id.POIRowRateScore);
            this.ratingBar = (RatingBar)itemView.findViewById(R.id.POIRowRateBar);
        }
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_poi_list_row_layout, parent, false);

        return new mViewHolder(v);
    }

    @Override
    public void onBindViewHolder(mViewHolder holder, final int position) {
        String url = values.get(position).getImgBase64();
        ImageLoader.loadImageMax(context, url, holder.imgV, 200 ,100);

        final int id = values.get(position).getID();

        holder.title.setText(values.get(position).getName());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(Activity_POIActivity.ARG_ID, id);
                intent.setClass(context, Activity_POIActivity.class);
                context.startActivity(intent);
            }
        });

        switch (sortID){
            case 0:
                double dis = values.get(position).getDisTo()/(float)1000;
                String s = String.format(Locale.ENGLISH,"%.2f", dis);
                String s2 = s + " km";
                holder.subTitle.setText(s2);
                break;
            case 1:
                RatingBar rateBar = holder.ratingBar;
                rateBar.setEnabled(false);
                rateBar.setVisibility(View.VISIBLE);
                rateBar.setRating((float) values.get(position).getRate_score());
                s = String.format(Locale.ENGLISH, "%.2f", values.get(position).getRate_score());
                String s1 = s + " / 5.00";
                holder.rateScore.setText(s1);
                break;
            case 2:
                holder.subTitle.setText(values.get(position).getReasonForSug());
            default:
                break;
        }
    }


    @Override
    public int getItemCount() {
        return values.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
