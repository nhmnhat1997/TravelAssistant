package com.assignment.travelassistant;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.assignment.travelassistant.model.Diary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DiariesAdapter extends RecyclerView.Adapter<DiariesAdapter.MyViewHolder> {
    Context mContext;
    Activity mActivity;
    ArrayList<Diary> mList;
    String myFormat = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

    public DiariesAdapter(Context mContext, ArrayList<Diary> mList){
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.cardview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.title.setText(mList.get(position).getTitle());
        holder.content.setText(mList.get(position).getContent());
        holder.date.setText(sdf.format(mList.get(position).getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addAll(ArrayList<Diary> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void clearAll(){
        mList.clear();
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title, date, content;
        public MyViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            content = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }
}
