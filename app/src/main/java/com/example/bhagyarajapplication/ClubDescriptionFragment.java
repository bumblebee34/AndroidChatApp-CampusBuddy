package com.example.bhagyarajapplication;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ClubDescriptionFragment extends Fragment {

    private View view;
    private TextView description;
    private EditText editDescription;
    private FloatingActionButton fab,fabSubmit;
    private FirebaseAuth mAuth;
    private DatabaseReference ClubsRef,UsersRef;
    private String name,currentUserID,userType,info;


    public ClubDescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        name = ((ClubsActivity) getActivity()).GetClubName();
        ClubsRef = FirebaseDatabase.getInstance().getReference().child("Clubs").child(name);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        view = inflater.inflate(R.layout.fragment_club_description, container, false);
        InitializeFields();
        SetDescription();
        CheckUser();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditClubDescription();
            }
        });
        return view;
    }

    private void SetDescription() {
        ClubsRef.child("Description").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    info = dataSnapshot.getValue().toString();
                    description.setText(info);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void EditClubDescription() {
        info = description.getText().toString().trim();
        editDescription.setText(info);
        description.setVisibility(View.INVISIBLE);
        editDescription.setVisibility(View.VISIBLE);
        fab.setVisibility(View.INVISIBLE);
        fabSubmit.setVisibility(View.VISIBLE);
        fabSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info = editDescription.getText().toString().trim();
                ClubsRef.child("Description").setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Description Edited Successfully", Toast.LENGTH_SHORT).show();
                            fab.setVisibility(View.VISIBLE);
                            fabSubmit.setVisibility(View.INVISIBLE);
                            editDescription.setVisibility(View.INVISIBLE);
                            description.setVisibility(View.VISIBLE);
                        }
                        else {
                            String message = task.getException().toString();
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void CheckUser() {
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userType = dataSnapshot.child("Type").getValue().toString();
                    if(userType.equals(name)){
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {
        description = view.findViewById(R.id.club_description_text_view);
        description.setMovementMethod(new ScrollingMovementMethod());
        editDescription = view.findViewById(R.id.club_description_edit_text_view);
        fab = view.findViewById(R.id.clubs_description_fab);
        fabSubmit = view.findViewById(R.id.clubs_description_submit_fab);
    }
}
