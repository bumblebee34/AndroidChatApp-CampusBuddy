package com.example.bhagyarajapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class ViewImageActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private PhotoView photoView;
    private String photo;
    private ImageButton back;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        InitializeFields();
        photo = (String) getIntent().getExtras().get("image");
        Picasso.get().load(photo).into(photoView);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void InitializeFields() {
        mToolbar = findViewById(R.id.image_view_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        photoView = findViewById(R.id.image_view_photo_view);
        back = findViewById(R.id.image_view_back_button);
        title = findViewById(R.id.image_view_toolbar_text);
    }
}
