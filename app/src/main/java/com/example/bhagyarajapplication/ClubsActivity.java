package com.example.bhagyarajapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.tabs.TabLayout;

public class ClubsActivity extends AppCompatActivity {

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapter tabsAccessorAdapter;
    private String clubName;
    private Toolbar mToolbar;
    private TextView club;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);
        clubName = getIntent().getExtras().get("Name").toString();
        InitializeFields();
        myViewPager = findViewById(R.id.club_view_pager);
        tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(tabsAccessorAdapter);
        myTabLayout = findViewById(R.id.club_tab_layout);
        myTabLayout.setupWithViewPager(myViewPager);
        club.setText(clubName);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void InitializeFields() {
        mToolbar = findViewById(R.id.clubs_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        back = findViewById(R.id.clubs_back_button);
        club = findViewById(R.id.club_toolbar_text);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public String GetClubName() {
        return clubName;
    }
}
