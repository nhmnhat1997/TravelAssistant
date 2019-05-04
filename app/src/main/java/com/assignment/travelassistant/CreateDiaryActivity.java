package com.assignment.travelassistant;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.travelassistant.model.Diary;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateDiaryActivity extends AppCompatActivity {

    String myFormat = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
    EditText title, content;
    TextView date;
    AppCompatButton saveDiary;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dbRef;
    FirebaseAuth mAuth;
    Date currentDate = new Date();

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener releaseDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            myCalendar.set(Calendar.YEAR,i);
            myCalendar.set(Calendar.MONTH,i1);
            myCalendar.set(Calendar.DAY_OF_MONTH,i2);
            updateDate(date);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_diary);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Add diary");

        title = (EditText) findViewById(R.id.edt_title);
        date = (TextView) findViewById(R.id.edt_date);
        content = (EditText) findViewById(R.id.edt_content);
        saveDiary = (AppCompatButton) findViewById(R.id.btnSaveDiary);
        date.setText(sdf.format(currentDate));

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference();

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b){
                    hideKeyboard(view);
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(view);
                DatePickerDialog dialog = new DatePickerDialog(CreateDiaryActivity.this,releaseDate,myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        saveDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValid()) {
                    String id = getMD5(mAuth.getCurrentUser().getEmail());
                    String key = dbRef.child(id).push().getKey();
                    Diary diary = new Diary();
                    diary.setTitle(title.getText().toString());
                    diary.setContent(content.getText().toString());
                    diary.setTimeStamp(currentDate.getTime());
                    diary.setTimeCreated((new Date()).getTime());
                    dbRef.child(id).child(key).setValue(diary);
                    Toast.makeText(CreateDiaryActivity.this, "Save diary success!",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateDate(TextView Time){
        currentDate = myCalendar.getTime();
        Time.setText(sdf.format(myCalendar.getTime()));
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean checkValid(){
        if (title.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(CreateDiaryActivity.this, "Title must not be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (date.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(CreateDiaryActivity.this, "Date must not be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        if (content.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(CreateDiaryActivity.this, "Content must not be empty", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            return convertByteToHex(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String convertByteToHex(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            sb.append(Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}
