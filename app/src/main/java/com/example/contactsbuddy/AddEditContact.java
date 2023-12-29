package com.example.contactsbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

public class AddEditContact extends AppCompatActivity {

    private ImageView profileIv;
    private EditText nameEt,phoneEt,emailEt,noteEt;
    private FloatingActionButton fab;

    //String variable
    private String id,/*image,*/name,phone,email,note,addedTime,updatedTime;
    private Boolean isEditMode;

    private ActionBar actionBar;

    // permission constant
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final int IMAGE_FROM_GALLERY_CODE = 300;
    private static final int IMAGE_FROM_CAMERA_CODE = 400;


    // string array of permission
    private String[] cameraPermission;
    private String[] storagePermission;

    private Uri imageUri;

    private DbHelper dbHelper;

    private FloatingActionButton first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_contact);

        // init db
        dbHelper = new DbHelper(this);

        // init permission
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //actionBar = getSupportActionBar();

        //actionBar.setTitle("Add Contacts");
        // back button
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setDisplayShowHomeEnabled(true);

        profileIv = findViewById(R.id.profileIv);
        nameEt = findViewById(R.id.nameEt);
        phoneEt = findViewById(R.id.phoneEt);
        emailEt = findViewById(R.id.emailEt);
        noteEt = findViewById(R.id.noteEt);
        fab = findViewById(R.id.fab);

        // get intent data
        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("isEditMode",false);

        if (isEditMode){
            // get the other value from intent
            id = intent.getStringExtra("ID");
            name = intent.getStringExtra("NAME");
            phone = intent.getStringExtra("PHONE");
            email = intent.getStringExtra("EMAIL");
            note = intent.getStringExtra("NOTE");
            addedTime = intent.getStringExtra("ADDEDTIME");
            updatedTime = intent.getStringExtra("UPDATEDTIME");
//            image = intent.getStringExtra("IMAGE");

            // set value
            nameEt.setText(name);
            phoneEt.setText(phone);
            emailEt.setText(email);
            noteEt.setText(note);

//            imageUri = Uri.parse(image);

//            if (image.equals("")){
//                profileIv.setImageResource(R.drawable.baseline_person_24);
//            }
//            else {
//                profileIv.setImageURI(imageUri);
//            }
        } else {

        }

        // add event handler
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        FloatingActionButton first = findViewById(R.id.first);
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // move to new activity to add contact
                Intent intent = new Intent(AddEditContact.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showImagePickerDialog() {
        // option for dialog
        String options[] = {"Camera", "Gallery"};

        // alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // set Title
        builder.setTitle("Choose an Option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // handle item click
                if (which == 0)
                {
                    // camera selected
                    if (!checkCameraPermission()){
                        // request camera permission
                        requestCameraPermission();
                    }
                    else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    // Gallery selected
                    if (!checkStoragePermission()){
                        // request storage permission
                        requestStoragePermission();
                    }
                    else {
                        pickFromGallery();
                    }
                }
            }
        }).create().show();
    }

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*"); // only image

        startActivityForResult(galleryIntent,IMAGE_FROM_GALLERY_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"IMAGE_TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION,"IMAGE_DETAILS");

        // save imageUri
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        // intent to open camera
        Intent cameraIntent  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);

        startActivityForResult(cameraIntent,IMAGE_FROM_CAMERA_CODE);
    }

    private void saveData() {
        // get user inputs
        name = nameEt.getText().toString();
        phone = phoneEt.getText().toString();
        email = emailEt.getText().toString();
        note = noteEt.getText().toString();

        // get current time
        String timeStamp = ""+System.currentTimeMillis();

        // check empty data
        if(!name.isEmpty() || !phone.isEmpty() || !email.isEmpty() || !note.isEmpty())
        {
            // check add or edit mode to save data
            if(isEditMode){
                // edit mode
                updatedTime = timeStamp;
                dbHelper.updateContact(
                        "" + id,
                        "" + name,
                        "" + phone,
                        "" + email,
                        "" + note,
                        "" + addedTime,
                        "" + updatedTime
                );
                Toast.makeText(getApplicationContext(), "Updated Successfully ", Toast.LENGTH_SHORT).show();
            }
            else {
                // add mode
                // function for save data on SQLite database
                long id = dbHelper.insertContact(
                        "" + name,
                        "" + phone,
                        "" + email,
                        "" + note,
                        "" + timeStamp,
                        "" + timeStamp
                );

                // toast message for successfully data inserted
                Toast.makeText(getApplicationContext(), "Inserted Successfully ", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            // show toast message
            Toast.makeText(getApplicationContext(), "Nothing to Save", Toast.LENGTH_SHORT).show();
        }
    }

    // back button click
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    // check camera permission
    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result & result1;
    }

    // request for camera permission
    private void requestCameraPermission()
    {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_PERMISSION_CODE);
    }


    // check storage permission
    private boolean checkStoragePermission(){
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result1;
    }

    // request for camera permission
    private void requestStoragePermission()
    {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_PERMISSION_CODE);
    }

    // handle request permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Camera permission granted
                    if (permissions.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        // Storage permission granted
                        pickFromCamera();
                    } else {
                        Toast.makeText(getApplicationContext(), "Storage Permission Needed.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Camera Permission Needed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Storage permission granted
                    pickFromGallery();
                } else {
                    Toast.makeText(getApplicationContext(), "Storage Permission Needed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK){
//            if(requestCode == IMAGE_FROM_GALLERY_CODE){
//                CropImage.activity(data.getData())
//                        .setGuideLines(CropImageView.GuideLines.ON)
//                        .setAspectRatio(1,1)
//                        .start(this);
//                } else if (requestCode == IMAGE_FROM_CAMERA_CODE) {
//                CropImage.activity(imageUri)
//                        .setGuideLines(CropImageView.GuideLines.ON)
//                        .setAspectRatio(1,1)
//                        .start(this);
//            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//                CropImage.ActivityResult result = CropImage.getActivityResult(data);
//                imageUri = result.getUri();
//                profileIv.setImageURI(imageUri);
//            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}