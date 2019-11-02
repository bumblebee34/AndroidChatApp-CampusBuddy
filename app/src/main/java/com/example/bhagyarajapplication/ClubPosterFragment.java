package com.example.bhagyarajapplication;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.yalantis.ucrop.UCrop;

import static android.app.Activity.RESULT_OK;

public class ClubPosterFragment extends Fragment {

    private View view;
    private static final int GalleryPick = 1;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference ClubsRef,UsersRef;
    private StorageReference UserProfileImageRef;
    private String clubName,currentUserID,userType;
    private FloatingActionButton fab;
    private PhotoView photoView;
    private ImageView photo;
    private String poster;


    public ClubPosterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ClubsRef = FirebaseDatabase.getInstance().getReference().child("Clubs");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        clubName = ((ClubsActivity) getActivity()).GetClubName();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Posters");
        view = inflater.inflate(R.layout.fragment_club_poster, container, false);
        loadingBar = new ProgressDialog(getContext());
        InitializeFields();
        SetDescription();
        CheckUser();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),ViewImageActivity.class);
                intent.putExtra("image",poster);
                getContext().startActivity(intent);
            }
        });
        return view;
    }

    private void InitializeFields() {
        //photoView = view.findViewById(R.id.photo_view);
        photo = view.findViewById(R.id.photo_view);
        fab = view.findViewById(R.id.clubs_poster_fab);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
        loadingBar.setTitle("Uploading");
        loadingBar.setMessage("Please Wait\nIf File size is large it will take some time Don't press back button");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Uri resultUri = data.getData();
        final StorageReference filePath = UserProfileImageRef.child( clubName+ ".jpg");
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
                    ClubsRef.child(clubName).child("Poster").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                loadingBar.dismiss();
                                Toast.makeText(getContext(), "Image Saved", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                loadingBar.dismiss();
                                String message = task.getException().toString();
                                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });}
    }

    private void SetDescription() {
        ClubsRef.child(clubName).child("Poster").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                poster = dataSnapshot.getValue().toString();
                Picasso.get().load(poster).into(photo);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckUser() {
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userType = dataSnapshot.child("Type").getValue().toString();
                    if(userType.equals(clubName)){
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
