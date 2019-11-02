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

public class ClubVenueFragment extends Fragment {

    private View view;
    private TextView date,time,room;
    private EditText editDate,editTime,editRoom;
    private FloatingActionButton fab,fabSubmit;
    private FirebaseAuth mAuth;
    private DatabaseReference ClubsRef,UsersRef;
    private String name,currentUserID,userType,dateInfo,timeInfo,roomInfo;

    public ClubVenueFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        name = ((ClubsActivity) getActivity()).GetClubName();
        ClubsRef = FirebaseDatabase.getInstance().getReference().child("Clubs").child(name);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        view = inflater.inflate(R.layout.fragment_club_venue, container, false);
        InitializeFields();
        SetDescription();
        CheckUser();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditClubVenue();
            }
        });
        return view;
    }

    private void SetDescription() {
        ClubsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dateInfo = dataSnapshot.child("Date").getValue().toString().trim();
                    timeInfo = dataSnapshot.child("Time").getValue().toString().trim();
                    roomInfo = dataSnapshot.child("Room").getValue().toString().trim();
                    date.setText(dateInfo);
                    time.setText(timeInfo);
                    room.setText(roomInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void CheckUser(){
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

    private void EditClubVenue(){
        dateInfo = date.getText().toString();
        timeInfo = time.getText().toString();
        roomInfo = room.getText().toString();
        editDate.setText(dateInfo);
        editTime.setText(timeInfo);
        editRoom.setText(roomInfo);
        date.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);
        room.setVisibility(View.INVISIBLE);
        editDate.setVisibility(View.VISIBLE);
        editTime.setVisibility(View.VISIBLE);
        editRoom.setVisibility(View.VISIBLE);
        fab.setVisibility(View.INVISIBLE);
        fabSubmit.setVisibility(View.VISIBLE);
        fabSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateInfo = editDate.getText().toString().trim();
                timeInfo = editTime.getText().toString().trim();
                roomInfo = editRoom.getText().toString().trim();
                Map map = new HashMap();
                    map.put("Date",dateInfo);
                    map.put("Time",timeInfo);
                    map.put("Room",roomInfo);
                ClubsRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Venue Edited Successfully", Toast.LENGTH_SHORT).show();
                            fab.setVisibility(View.VISIBLE);
                            fabSubmit.setVisibility(View.INVISIBLE);
                            editDate.setVisibility(View.INVISIBLE);
                            editTime.setVisibility(View.INVISIBLE);
                            editRoom.setVisibility(View.INVISIBLE);
                            date.setVisibility(View.VISIBLE);
                            time.setVisibility(View.VISIBLE);
                            room.setVisibility(View.VISIBLE);
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

    private void InitializeFields() {
        date = view.findViewById(R.id.club_venue_date_text_view);
        time = view.findViewById(R.id.club_venue_time_text_view);
        room = view.findViewById(R.id.club_venue_room_text_view);
        editDate = view.findViewById(R.id.club_venue_date_edit_text_view);
        editTime = view.findViewById(R.id.club_venue_time_edit_text_view);
        editRoom = view.findViewById(R.id.club_venue_room_edit_text_view);
        fab = view.findViewById(R.id.clubs_venue_fab);
        fabSubmit = view.findViewById(R.id.clubs_venue_submit_fab);
    }

}
