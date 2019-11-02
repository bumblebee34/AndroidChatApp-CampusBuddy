package com.example.bhagyarajapplication;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragment=null;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private CircleImageView profileImage;
    private TextView profileName,profilePRN;
    private String currentUserID;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.i("bbh","DOne");
            SendUserToLoginActivity();
        }
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        RootRef = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();
        fragment = new GroupsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.screen,fragment);
        fragmentTransaction.commit();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);
//        profileImage = hView.findViewById(R.id.profile_image_nav);
//        profileName = hView.findViewById(R.id.profile_name_nav);
//        profilePRN = hView.findViewById(R.id.profile_prn_nav);
        RetrieveUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            SendUserToLoginActivity();
        }
    }

    private void RetrieveUserInfo() {
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String type = dataSnapshot.child("Type").getValue().toString();
                    if(type.equals("Student")) {
                        if (dataSnapshot.hasChild("Profile Image")) {
                            String userImage = dataSnapshot.child("Profile Image").getValue().toString();
                            String userName = dataSnapshot.child("Name").getValue().toString();
                            String userPRN = dataSnapshot.child("PRN").getValue().toString();
//                            profileName.setText(userName);
//                            profilePRN.setText(userPRN);
//                            Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(profileImage);
                        } else {
                            String userName = dataSnapshot.child("Name").getValue().toString();
                            String userPRN = dataSnapshot.child("PRN").getValue().toString();
//                            profileName.setText(userName);
//                            profilePRN.setText(userPRN);
                        }
                    }
                    else if(type.equals("Faculty")){
                        if (dataSnapshot.hasChild("Profile Image")) {
                            String userImage = dataSnapshot.child("Profile Image").getValue().toString();
                            String userName = dataSnapshot.child("Name").getValue().toString();
                            String userSubject = dataSnapshot.child("Main Subject").getValue().toString();
//                            profileName.setText(userName);
//                            profilePRN.setText(userSubject);
//                            Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(profileImage);
                        } else {
                            String userName = dataSnapshot.child("Name").getValue().toString();
                            String userSubject = dataSnapshot.child("Main Subject").getValue().toString();
//                            profileName.setText(userName);
//                            profilePRN.setText(userSubject);
//                            Picasso.get().load(R.drawable.profile_image).into(profileImage);
                        }
                    }
                    else{
                        if (dataSnapshot.hasChild("Profile Image")) {
                            String userImage = dataSnapshot.child("Profile Image").getValue().toString();
                            String userName = dataSnapshot.child("Name").getValue().toString();
//                            profileName.setText(userName);
//                            Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(profileImage);
                        } else {
                            String userName = dataSnapshot.child("Name").getValue().toString();
//                            profileName.setText(userName);
//                            Picasso.get().load(R.drawable.profile_image).into(profileImage);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.groups) {
            fragment = new GroupsFragment();
        }
        else if (id == R.id.clubs) {
            fragment = new ClubsFragment();
        }
        else if (id == R.id.events) {
            fragment = new EventsFragment();
        }
        else if (id == R.id.about) {
            fragment = new AboutUsFragment();
        }
        else if (id == R.id.logout) {
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        else if (id == R.id.settings) {
            Intent settingsIntent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settingsIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
        if(fragment!=null){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.screen,fragment);
            fragmentTransaction.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

}
