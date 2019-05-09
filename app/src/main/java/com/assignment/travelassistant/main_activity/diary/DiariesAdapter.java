package com.assignment.travelassistant.main_activity.diary;

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
import com.bumptech.glide.Glide;

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
        if (mList.get(position).getPhotoURLs() != null
        && !mList.get(position).getPhotoURLs().equals("")) {
            Glide.with(mContext).load(mList.get(position).getPhotoURLs()).into(holder.imgDiary);
        }
        holder.cardviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DiaryDetailActivity.class);
                intent.putExtra("title", mList.get(position).getTitle());
                intent.putExtra("content",mList.get(position).getContent());
                intent.putExtra("date",mList.get(position).getTimeStamp());
                if (mList.get(position).getPhotoURLs() != null
                        && !mList.get(position).getPhotoURLs().equals("")) {
                    intent.putExtra("photoURL", mList.get(position).getPhotoURLs());
                } else {
                    intent.putExtra("photoURL", "");
                }
                mContext.startActivity(intent);
            }
        });
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
        ImageView imgDiary;
        LinearLayout cardviewLayout;
        public MyViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            imgDiary = (ImageView) itemView.findViewById(R.id.img_diary);
            cardviewLayout = (LinearLayout) itemView.findViewById(R.id.cardview_linear);
        }
    }
}
