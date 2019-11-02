package com.example.bhagyarajapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class UserSelectionActivity extends AppCompatActivity {

    private String UserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        String[] plants = new String[]{
                "None",
                "Faculty",
                "Student"
        };
        Spinner spinner = findViewById(R.id.teacher_student);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,plants
        );
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Toast.makeText(UserSelectionActivity.this, "Select", Toast.LENGTH_SHORT).show();
                }
                else if(position == 1){
                    UserType = "Faculty";
                    Intent facultyIntent = new Intent(UserSelectionActivity.this,SignUpActivity.class);
                    facultyIntent.putExtra("User_type",UserType);
//                    facultyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(facultyIntent);
                }
                else if(position == 2){
                    UserType = "Student";
                    Intent studentIntent = new Intent(UserSelectionActivity.this,SignUpActivity.class);
                    studentIntent.putExtra("User_type",UserType);
//                    studentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(studentIntent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SendUserToLoginActivity();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(UserSelectionActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }
}
