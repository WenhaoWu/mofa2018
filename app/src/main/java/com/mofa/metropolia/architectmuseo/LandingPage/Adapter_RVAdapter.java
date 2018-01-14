package com.mofa.metropolia.architectmuseo.LandingPage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mofa.metropolia.architectmuseo.POIListView.Activity_POIMainListView;
import com.mofa.metropolia.architectmuseo.R;
import com.mofa.metropolia.architectmuseo.Utils.ImageLoader;
import com.mofa.metropolia.architectmuseo.Utils.TestActivity;

import java.util.ArrayList;

public class Adapter_RVAdapter extends android.support.v7.widget.RecyclerView.Adapter<Adapter_RVAdapter.mViewHolder> {

    private ArrayList<Object_RVItem> dataList;
    private Context mContext;

    Adapter_RVAdapter(ArrayList<Object_RVItem> dataList, Context mContext) {
        this.dataList = dataList;
        this.mContext = mContext;
    }

    static class mViewHolder extends RecyclerView.ViewHolder {
        CardView cv ;
        ImageView imgV;
        TextView txtV;
        RelativeLayout rv;

        mViewHolder(View itemView) {
            super(itemView);
            this.cv = (CardView)itemView.findViewById(R.id.Landing_RV_CV);
            this.imgV = (ImageView) itemView.findViewById(R.id.Landing_RV_CV_img);
            this.txtV = (TextView)itemView.findViewById(R.id.Landing_RV_CV_txt);
            this.rv = (RelativeLayout)itemView.findViewById(R.id.cardView_RV);
        }
    }

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_item_cardview, parent, false);

        return new mViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final mViewHolder holder, int position) {

        String imgUrl;
        if (position==0){
            holder.txtV.setText("All");
            imgUrl = "http://www.arkkitehtuurimuseo.fi/newpro/Wikitude_1/public/img/logot_app-2.jpg";
        }
        else {
            holder.txtV.setText(dataList.get(position-1).getCateName());
            imgUrl = dataList.get(position-1).getImage64();
        }

        ImageLoader.loadImageMax(mContext, imgUrl, holder.imgV, 800 ,220);

        holder.imgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(Activity_POIMainListView.TAG_CATE, holder.txtV.getText());
                intent.setClass(mContext, Activity_POIMainListView.class);
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataList.size()+1;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
