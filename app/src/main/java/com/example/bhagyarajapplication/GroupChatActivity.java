package com.example.bhagyarajapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private static String groupName;
    private ImageButton SendMessageButton,AddContentButton;
    private EditText InputMessageText;
    private TextView Admin;
    private ConstraintLayout layout;
    private FirebaseAuth mAuth;
    private DatabaseReference GroupRef,UsersRef;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView groupMessagesList;
    private String senderID;
    private static String senderName;
    private static String senderType;
    private String currentDate;
    private String currentTime;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = GroupChatActivity.this;
        setContentView(R.layout.activity_group_chat);
        mAuth = FirebaseAuth.getInstance();
        senderID = mAuth.getCurrentUser().getUid();
        groupName = getIntent().getExtras().get("group_name").toString();
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName).child("Messages");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        InitializeFields();
        GetUserType();
        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
        AddContentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new ContentSelectionFragment();
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_screen,fragment,"Content_Frag");
                fragmentTransaction.addToBackStack("Content_frag");
                fragmentTransaction.commit();
            }
        });
        GroupRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();
                groupMessagesList.smoothScrollToPosition(groupMessagesList.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                finish();
                overridePendingTransition(0,0);
                startActivity(getIntent());
                overridePendingTransition(0,0);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static String getGroupName(){
        return groupName;
    }

    public static String getSenderName(){
        return senderName;
    }

    public static String getSenderType(){
        return senderType;
    }

    private void GetUserType() {
        UsersRef.child(senderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    senderName = dataSnapshot.child("Name").getValue().toString();
                    senderType = dataSnapshot.child("Type").getValue().toString();
                    if(senderType.equals("Faculty")){
                        layout.setVisibility(View.VISIBLE);
                        SendMessageButton.setVisibility(View.VISIBLE);
                        Admin.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields() {
        SendMessageButton = findViewById(R.id.group_chat_send_message_button);
        InputMessageText = findViewById(R.id.group_message_text);
        groupMessagesList = findViewById(R.id.group_chat_recycler_view);
        groupMessagesList.setLayoutManager(new LinearLayoutManager(this));
        mToolbar = findViewById(R.id.group_chat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(groupName);
        messageAdapter = new MessageAdapter(messagesList,context);
        linearLayoutManager = new LinearLayoutManager(this);
        groupMessagesList.setAdapter(messageAdapter);
        groupMessagesList.setLayoutManager(linearLayoutManager);
        layout = findViewById(R.id.group_chat_layout);
        Admin = findViewById(R.id.admin_text);
        AddContentButton = findViewById(R.id.group_chat_content_add_button);
    }

    private void SendMessage() {
        String messageText = InputMessageText.getText().toString().trim();
        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "Please Enter The Message First", Toast.LENGTH_SHORT).show();
        }
        else{
            DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
            String messagePushID = userMessageKeyRef.getKey();
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());
            Calendar  calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());
            Map messageMap = new HashMap();
                messageMap.put("Message",messageText);
                messageMap.put("From",senderName);
                messageMap.put("Type","Text");
                messageMap.put("Date",currentDate);
                messageMap.put("Time",currentTime);
                messageMap.put("ID",messagePushID);
            Map messageBodyDetails = new HashMap();
                messageBodyDetails.put(messagePushID,messageMap);
            GroupRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(!task.isSuccessful()){
                        String message = task.getException().toString();
                        Toast.makeText(GroupChatActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    InputMessageText.setText("");
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GroupChatActivity.this,MainActivity.class);
        startActivity(intent);
    }

}
