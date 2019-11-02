package com.example.bhagyarajapplication;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ClubsFragment extends Fragment {

    private View ClubsView;
    private RecyclerView ClubsRecyclerView;
    private DatabaseReference ClubsRef;

    public ClubsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ClubsRef = FirebaseDatabase.getInstance().getReference().child("Clubs");
        ClubsView = inflater.inflate(R.layout.fragment_clubs, container, false);
        ClubsRecyclerView = ClubsView.findViewById(R.id.clubs_recycler_view);
        ClubsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return ClubsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Clubs> options = new FirebaseRecyclerOptions.Builder<Clubs>()
                .setQuery(ClubsRef,Clubs.class)
                .build();
        FirebaseRecyclerAdapter<Clubs,ClubsViewHolder> adapter = new FirebaseRecyclerAdapter<Clubs, ClubsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ClubsViewHolder clubsViewHolder, int i, @NonNull Clubs clubs) {
                clubsViewHolder.clubName.setText(clubs.getClub_name());
                final String clubName = clubs.getClub_name();
                clubsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clubsIntent = new Intent(getContext(),ClubsActivity.class);
                        clubsIntent.putExtra("Name",clubName);
                        startActivity(clubsIntent);
                    }
                });
            }

            @NonNull
            @Override
            public ClubsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_display_1,parent,false);
                ClubsViewHolder viewHolder = new ClubsViewHolder(view);
                return viewHolder;
            }
        };
        ClubsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private static class ClubsViewHolder extends RecyclerView.ViewHolder{
        TextView clubName;
        public ClubsViewHolder(@NonNull View itemView) {
            super(itemView);
            clubName = itemView.findViewById(R.id.group_name_1);
        }
    }
}
