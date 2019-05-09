package com.assignment.travelassistant.main_activity.diary;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assignment.travelassistant.R;
import com.assignment.travelassistant.model.Diary;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    StorageReference mStorageRef;
    ImageView image;
    FirebaseAuth mAuth;
    Date currentDate = new Date();
    File mImageFile;
    String currentPhotoURL = "";

    public static final int CAMERA = 1;
    public static final int GALLERY = 0;

    Calendar myCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener releaseDate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            myCalendar.set(Calendar.YEAR, i);
            myCalendar.set(Calendar.MONTH, i1);
            myCalendar.set(Calendar.DAY_OF_MONTH, i2);
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
        image = (ImageView) findViewById(R.id.image);
        Glide.with(this).load(R.drawable.default_img).into(image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        date.setText(sdf.format(currentDate));

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        dbRef = firebaseDatabase.getReference();
        String id = getMD5(mAuth.getCurrentUser().getEmail());
        mStorageRef = FirebaseStorage.getInstance().getReference();

        date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    hideKeyboard(view);
                }
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hideKeyboard(view);
                DatePickerDialog dialog = new DatePickerDialog(CreateDiaryActivity.this, releaseDate, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
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
                    diary.setPhotoURLs(currentPhotoURL);
                    dbRef.child(id).child(key).setValue(diary);
                    Toast.makeText(CreateDiaryActivity.this, "Save diary success!", Toast.LENGTH_LONG).show();
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

    public void updateDate(TextView Time) {
        currentDate = myCalendar.getTime();
        Time.setText(sdf.format(myCalendar.getTime()));
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public boolean checkValid() {
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

    private void selectImage() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();

    }

    public void choosePhotoFromGallary() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY);
            return;
        }
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA);
            return;
        }
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    String id = getMD5(mAuth.getCurrentUser().getEmail());
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //image.setImageBitmap(bitmap);
                    mImageFile = new File(getPath(this, contentURI));
                    long time = (new Date()).getTime();
                    String currentTime = String.valueOf(time);
                    StorageReference files = mStorageRef.child("image-"+id).child(currentTime+"-"+mImageFile.getName());
                    files.putFile(contentURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            files.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    currentPhotoURL = uri.toString();
                                    Glide.with(CreateDiaryActivity.this).load(uri).into(image);
                                    Toast.makeText(CreateDiaryActivity.this, "Upload image success!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    //saveImage(bitmap,requestCode);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast t = Toast.makeText(CreateDiaryActivity.this, "Failed!", Toast.LENGTH_SHORT);
                    t.show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            saveImage(thumbnail, requestCode);

        }
    }

    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public void saveImage(Bitmap myBitmap, int requestCode) {
        if (requestCode == CAMERA) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String id = getMD5(mAuth.getCurrentUser().getEmail());
            long time = (new Date()).getTime();
            String currentTime = String.valueOf(time);
            StorageReference files = mStorageRef.child("image-"+id).child(currentTime+".jpg");
            files.putBytes(bytes.toByteArray()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   files.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           currentPhotoURL = uri.toString();
                           Glide.with(CreateDiaryActivity.this).load(uri).into(image);
                           Toast.makeText(CreateDiaryActivity.this, "Upload image successfully!", Toast.LENGTH_LONG).show();
                       }
                   });
                }
            });

//            File wallpaperDirectory = new File(
//                    Environment.getExternalStorageDirectory() + "/image");
//            // have the object build the directory structure, if needed.
//            if (!wallpaperDirectory.exists()) {
//                wallpaperDirectory.mkdirs();
//            }
//
//            try {
//                File f = new File(wallpaperDirectory, Calendar.getInstance()
//                        .getTimeInMillis() + ".jpg");
//                f.createNewFile();
//                FileOutputStream fo = new FileOutputStream(f);
//                fo.write(bytes.toByteArray());
//                MediaScannerConnection.scanFile(this,
//                        new String[]{f.getPath()},
//                        new String[]{"image/jpeg"}, null);
//                fo.close();
//
//
//                mImageFile = f;
//                return f.getAbsolutePath();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
//        return "";
        }
    }
}
