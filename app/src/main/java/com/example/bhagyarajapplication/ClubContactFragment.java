package com.example.bhagyarajapplication;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.util.HashMap;
import java.util.Map;


public class ClubContactFragment extends Fragment {

    private View view;
    private TextView phone,web;
    private EditText editPhone,editWeb;
    private FloatingActionButton fab,fabSubmit;
    private FirebaseAuth mAuth;
    private DatabaseReference ClubsRef,UsersRef;
    private String name,currentUserID,userType,phoneInfo,webInfo;

    public ClubContactFragment() {
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
        view = inflater.inflate(R.layout.fragment_club_contact, container, false);
        InitializeFields();
        SetDescription();
        CheckUser();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditContactDescription();
            }
        });
        return  view;
    }

    private void InitializeFields() {
        fab = view.findViewById(R.id.clubs_contact_fab);
        fabSubmit = view.findViewById(R.id.clubs_contact_submit_fab);
        phone = view.findViewById(R.id.club_contact_text);
        web = view.findViewById(R.id.club_web_text);
        editPhone = view.findViewById(R.id.club_contact_edit_text);
        editWeb = view.findViewById(R.id.club_web_edit_text);
    }

    private void SetDescription() {
        ClubsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    phoneInfo = dataSnapshot.child("Phone").getValue().toString();
                    webInfo = dataSnapshot.child("Web").getValue().toString();
                    phone.setText(phoneInfo);
                    web.setText(webInfo);
                }
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

    private void EditContactDescription() {
        phoneInfo = phone.getText().toString();
        webInfo = web.getText().toString();
        editPhone.setText(phoneInfo);
        editWeb.setText(webInfo);
        phone.setVisibility(View.INVISIBLE);
        web.setVisibility(View.INVISIBLE);
        editPhone.setVisibility(View.VISIBLE);
        editWeb.setVisibility(View.VISIBLE);
        fab.setVisibility(View.INVISIBLE);
        fabSubmit.setVisibility(View.VISIBLE);
        fabSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneInfo = editPhone.getText().toString().trim();
                webInfo = editWeb.getText().toString().trim();
                Map map = new HashMap();
                    map.put("Phone",phoneInfo);
                    map.put("Web",webInfo);
                ClubsRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Description Edited Successfully", Toast.LENGTH_SHORT).show();
                            fab.setVisibility(View.VISIBLE);
                            fabSubmit.setVisibility(View.INVISIBLE);
                            editPhone.setVisibility(View.INVISIBLE);
                            editWeb.setVisibility(View.INVISIBLE);
                            phone.setVisibility(View.VISIBLE);
                            web.setVisibility(View.VISIBLE);
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
}
