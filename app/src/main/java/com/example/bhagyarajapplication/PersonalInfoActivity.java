package com.example.bhagyarajapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import java.util.HashMap;
import java.util.Map;

public class PersonalInfoActivity extends AppCompatActivity {

    private String Branch;
    private EditText UserName,UserPRN;
    private Button SubmitButton;
    private DatabaseReference UserRef;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private StorageReference UserProfileImageRef;
    private String CurrentUserID;
    private ImageButton ProfileImage;
    private static final int GalleryPick = 1;
    private ProgressDialog loadingBar;
    private String callingActivity;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Image");
        callingActivity = getIntent().getExtras().get("Activity").toString();
        InitializeFields();

        String[] animals = new String[]{
                "None",
                "FY IT",
                "SY IT",
                "TY IT",
                "BTECH IT",
        };
        Spinner spinner = findViewById(R.id.branch_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,animals);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position){
                    case 0:
                        //Toast.makeText(PersonalInfoActivity.this, "Please Select Your Branch", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Branch = "FY IT";
                        break;
                    case 2:
                        Branch = "SY IT";
                        break;
                    case 3:
                        Branch = "TY IT";
                        break;
                    case 4:
                        Branch = "BTECH IT";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdatePersonalInfo();
            }
        });
    }

    private void InitializeFields() {
        UserName = findViewById(R.id.user_name);
        UserPRN = findViewById(R.id.user_prn);
        SubmitButton = findViewById(R.id.user_info_submit_button);
        ProfileImage = findViewById(R.id.add_profile_image_button);
        loadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
        if(requestCode==GalleryPick && resultCode==RESULT_OK && data!=null){
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        loadingBar.setTitle("Uploading");
        loadingBar.setMessage("Please Wait\nIf File size is large it will take some time\nDon't press back button");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Uri resultUri = result.getUri();
            final StorageReference filePath = UserProfileImageRef.child(CurrentUserID + ".jpg");
            filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        final String downloadUri = downUri.toString();
                        RootRef.child("Users").child(CurrentUserID).child("Profile Image").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    Toast.makeText(PersonalInfoActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    loadingBar.dismiss();
                                    String message = task.getException().toString();
                                    Toast.makeText(PersonalInfoActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }}
    }

    private void UpdatePersonalInfo() {
        String name = UserName.getText().toString();
        String prn = UserPRN.getText().toString();
        HashMap<String,Object> map = new HashMap<>();
        map.put("Name",name);
        map.put("PRN",prn);
        UserRef.child("Groups").child("group_name").setValue(Branch);
        UserRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    if (callingActivity.equals("Settings")) {
                        SendUserToMainActivity();
                    } else {
                        SendUserToMainActivity();
                    }
                }
            }
        });
    }

    private void SendUserToSettingsActivity() {
        Intent settingIntent = new Intent(PersonalInfoActivity.this,SettingsActivity.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingIntent);
        finish();
    }

    private void SendUserToMainActivity() {
        Intent loginIntent = new Intent(PersonalInfoActivity.this,MainActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
