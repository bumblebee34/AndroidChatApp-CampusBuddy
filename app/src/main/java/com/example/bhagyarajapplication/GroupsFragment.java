package com.example.bhagyarajapplication;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class GroupsFragment extends Fragment {

    private View GroupsView;
    private RecyclerView GroupsRecyclerView;
    private DatabaseReference GroupsRef,UserRef,UserGroupsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;//,curentUserBranch,currentUserType,faculty="Faculty";

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        GroupsRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        //UserGroupsRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("UserGroups");
        GroupsView = inflater.inflate(R.layout.fragment_groups, container, false);
        GroupsRecyclerView = GroupsView.findViewById(R.id.groups_recycler_view);
        GroupsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //GetCurrentUserBranch();
        return GroupsView;
    }

//    private void GetCurrentUserBranch() {
//        UserRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot.exists()){
//                    curentUserBranch = dataSnapshot.child("Branch").getValue().toString();
//                    currentUserType = dataSnapshot.child("Type").getValue().toString();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Groups> options = new FirebaseRecyclerOptions.Builder<Groups>()
                .setQuery(GroupsRef,Groups.class)
                .build();
        FirebaseRecyclerAdapter<Groups,GroupsViewHolder> adapter = new FirebaseRecyclerAdapter<Groups, GroupsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull GroupsViewHolder groupsViewHolder, int i, @NonNull Groups groups) {
                final String groupName = groups.getGroup_name();
                groupsViewHolder.groupName.setText(groups.getGroup_name());
                groupsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent groupChatIntent = new Intent(getContext(),GroupChatActivity.class);
                        groupChatIntent.putExtra("group_name",groupName);
                        startActivity(groupChatIntent);
                    }
                });
            }

            @NonNull
            @Override
            public GroupsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_display_1,parent,false);
                GroupsViewHolder viewHolder = new GroupsViewHolder(view);
                return viewHolder;
            }
        };
        GroupsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private static class GroupsViewHolder extends RecyclerView.ViewHolder{
        TextView groupName;
        public GroupsViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.group_name_1);
        }
    }
}
