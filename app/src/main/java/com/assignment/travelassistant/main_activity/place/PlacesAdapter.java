package com.assignment.travelassistant.main_activity.place;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.travelassistant.R;
import com.assignment.travelassistant.model.Diary;
import com.assignment.travelassistant.model.Place;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder> {

    Context mContext;
    Activity mActivity;
    ArrayList<Place> mList;
    String myFormat = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

    public PlacesAdapter(Context mContext, ArrayList<Place> mList){
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public PlacesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.place_cardview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesAdapter.MyViewHolder holder, int position) {
        holder.name.setText(mList.get(position).getName());
        holder.time.setText(mList.get(position).getOpen() + " - " + mList.get(position).getClose());
        holder.address.setText(mList.get(position).getAddress());
        Glide.with(mContext).load(mList.get(position).getImg()).into(holder.imgPlace);
        holder.cardviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlaceDetail.class);
                intent.putExtra("name", mList.get(position).getName());
                intent.putExtra("address", mList.get(position).getAddress());
                intent.putExtra("open", mList.get(position).getOpen());
                intent.putExtra("close", mList.get(position).getClose());
                intent.putExtra("detail", mList.get(position).getDetail());
                intent.putExtra("img", mList.get(position).getImg());
                intent.putExtra("latitude", mList.get(position).getLatitude());
                intent.putExtra("longittude", mList.get(position).getLongittude());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addAll(ArrayList<Place> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearAll(){
        mList.clear();
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name, time, address;
        ImageView imgPlace;
        LinearLayout cardviewLayout;
        public MyViewHolder(View itemView){
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            address = (TextView) itemView.findViewById(R.id.tv_address);
            imgPlace = (ImageView) itemView.findViewById(R.id.img_place);
            cardviewLayout = (LinearLayout) itemView.findViewById(R.id.cardview_linear);
        }
    }
}
