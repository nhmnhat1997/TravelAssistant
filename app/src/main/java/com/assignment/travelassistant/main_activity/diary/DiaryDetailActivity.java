package com.assignment.travelassistant.main_activity.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.assignment.travelassistant.R;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;

public class DiaryDetailActivity extends AppCompatActivity {
    String myFormat = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
    TextView title, content,date;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_detail);


        title = (TextView) findViewById(R.id.edt_title);
        content = (TextView) findViewById(R.id.edt_content);
        date = (TextView) findViewById(R.id.edt_date);
        image = (ImageView) findViewById(R.id.image);

        title.setText(getIntent().getStringExtra("title"));
        content.setText(getIntent().getStringExtra("content"));
        date.setText(sdf.format(getIntent().getLongExtra("date",0)));
        if (!getIntent().getStringExtra("photoURL").equals("")) {
            Glide.with(this).load(getIntent().getStringExtra("photoURL")).into(image);
        }
    }
}
