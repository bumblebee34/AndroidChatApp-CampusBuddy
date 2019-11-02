package com.example.bhagyarajapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText UserEmail, UserPassword;
    private Button SignUpButton;
    private TextView AlreadyHaveAnAccount;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;
    private String UserType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        InitializeFields();
        UserType = getIntent().getExtras().get("User_type").toString();
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });
        AlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
    }

    private void InitializeFields() {
        UserEmail = findViewById(R.id.user_signup_email);
        UserPassword = findViewById(R.id.user_signup_password);
        SignUpButton = findViewById(R.id.user_signup_button);
        AlreadyHaveAnAccount = findViewById(R.id.already_have_an_account);
        loadingBar = new ProgressDialog(this);
    }

    private void SignUp() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Your Password", Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Creating Account");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        RootRef.child("Users").child(currentUserID).child("Type").setValue(UserType);
                        Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        if(UserType.equals("Faculty")){
                            SendUserToFacultyPersonalInfoActivity();
                        }
                        else if(UserType.equals("Student")) {
                            SendUserToPersonalInfoActivity();
                        }
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SendUserToLoginActivity();
    }

    private void SendUserToFacultyPersonalInfoActivity() {
        Intent facultyInfoIntent = new Intent(SignUpActivity.this,FacultyPersonalInfoActivity.class);
        facultyInfoIntent.putExtra("Activity","SignUp");
//        facultyInfoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(facultyInfoIntent);
//        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(SignUpActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void SendUserToPersonalInfoActivity() {
        Intent infoIntent = new Intent(SignUpActivity.this,PersonalInfoActivity.class);
        infoIntent.putExtra("Activity","SignUp");
//        infoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(infoIntent);
//        finish();
    }
}
