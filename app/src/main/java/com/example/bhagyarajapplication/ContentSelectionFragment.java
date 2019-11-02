package com.example.bhagyarajapplication;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.yalantis.ucrop.util.FileUtils.getPath;

public class ContentSelectionFragment extends Fragment {

    private View view;
    private ImageButton image,pdf,ppt,doc,text;
    private static int GalleryPick = 1;
    private StorageReference GroupChatImagesRef,GroupChatPDFRef;
    private String groupName,senderName,senderType;
    private DatabaseReference GroupRef;
    private ProgressDialog loadingBar;
    private String fileName,currentDate,currentTime;

    public ContentSelectionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_content_selection, container, false);
        groupName = ((GroupChatActivity)getActivity()).getGroupName();
        senderName = ((GroupChatActivity)getActivity()).getSenderName();
        senderType = ((GroupChatActivity)getActivity()).getSenderType();
        GroupChatImagesRef = FirebaseStorage.getInstance().getReference().child(groupName).child("Image");
        GroupChatPDFRef = FirebaseStorage.getInstance().getReference().child(groupName).child("PDF");
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName).child("Messages");
        InitializeFields();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
        pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryPick = 2;
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("application/pdf");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
        ppt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryPick = 3;
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("application/vnd.ms-powerpoint");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryPick = 4;
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryPick = 5;
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("text/plain");
                startActivityForResult(galleryIntent, GalleryPick);
            }
        });
        return view;
    }

    private void InitializeFields() {
        image = view.findViewById(R.id.add_image_button);
        pdf = view.findViewById(R.id.add_pdf_button);
        ppt = view.findViewById(R.id.add_ppt_button);
        doc = view.findViewById(R.id.add_word_button);
        text = view.findViewById(R.id.add_text_button);
        loadingBar = new ProgressDialog(getContext());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null){
        loadingBar.setTitle("Uploading");
        loadingBar.setMessage("Please Wait\nIf File size is large it will take some time Don't press back button");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = currentDateFormat.format(calForDate.getTime());
        Calendar  calForTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        currentTime = currentTimeFormat.format(calForTime.getTime());
        if(requestCode==1)
        {
            Uri resultUri = data.getData();
            Cursor returnCursor = getActivity().getApplicationContext().getContentResolver().query(resultUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
            //Toast.makeText(getContext(), fileName, Toast.LENGTH_SHORT).show();
//            DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
//            String messagePushID = userMessageKeyRef.getKey();
            final StorageReference filePath = GroupChatImagesRef.child(fileName);
            filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        final String downloadUri = downUri.toString();
                        DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
                        String messagePushID = userMessageKeyRef.getKey();
                        Map messageMap = new HashMap();
                        messageMap.put("Message",downloadUri);
                        messageMap.put("From",senderName);
                        messageMap.put("Type","Image");
                        messageMap.put("Date",currentDate);
                        messageMap.put("Time",currentTime);
                        messageMap.put("ID",messagePushID);
                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messagePushID,messageMap);
                        GroupRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    Fragment fragment = new BlankFragment();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.content_screen,fragment);
                                    fragmentTransaction.commit();
                                    Toast.makeText(getContext(), "Image Uploaded", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    loadingBar.dismiss();
                                    String message = task.getException().toString();
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        if(requestCode==2)
        {
            Uri resultUri = data.getData();
            Cursor returnCursor = getActivity().getApplicationContext().getContentResolver().query(resultUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
//            DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
//            String messagePushID = userMessageKeyRef.getKey();
            final StorageReference filePath = GroupChatPDFRef.child( fileName);
            filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        final String downloadUri = downUri.toString();
                        DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
                        String messagePushID = userMessageKeyRef.getKey();
                        Map messageMap = new HashMap();
                        messageMap.put("Message",downloadUri);
                        messageMap.put("From",senderName);
                        messageMap.put("Type","PDF");
                        messageMap.put("Name",fileName);
                        messageMap.put("Date",currentDate);
                        messageMap.put("Time",currentTime);
                        messageMap.put("ID",messagePushID);
                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messagePushID,messageMap);
                        GroupRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    Fragment fragment = new BlankFragment();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.content_screen,fragment);
                                    fragmentTransaction.commit();
                                    Toast.makeText(getContext(), "PDF Uploaded", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    loadingBar.dismiss();
                                    String message = task.getException().toString();
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        if(requestCode==3)
        {
            Uri resultUri = data.getData();
            Cursor returnCursor = getActivity().getApplicationContext().getContentResolver().query(resultUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
//            DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
//            String messagePushID = userMessageKeyRef.getKey();
            final StorageReference filePath = GroupChatPDFRef.child( fileName);
            filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        final String downloadUri = downUri.toString();
                        DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
                        String messagePushID = userMessageKeyRef.getKey();
                        Map messageMap = new HashMap();
                        messageMap.put("Message",downloadUri);
                        messageMap.put("From",senderName);
                        messageMap.put("Type","PPT");
                        messageMap.put("Name",fileName);
                        messageMap.put("Date",currentDate);
                        messageMap.put("Time",currentTime);
                        messageMap.put("ID",messagePushID);
                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messagePushID,messageMap);
                        GroupRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    Fragment fragment = new BlankFragment();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.content_screen,fragment);
                                    fragmentTransaction.commit();
                                    Toast.makeText(getContext(), "PPT Uploaded", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    loadingBar.dismiss();
                                    String message = task.getException().toString();
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        if(requestCode==4)
        {
            Uri resultUri = data.getData();
            Cursor returnCursor = getActivity().getApplicationContext().getContentResolver().query(resultUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
//            DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
//            String messagePushID = userMessageKeyRef.getKey();
            final StorageReference filePath = GroupChatPDFRef.child( fileName);
            filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        final String downloadUri = downUri.toString();
                        DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
                        String messagePushID = userMessageKeyRef.getKey();
                        Map messageMap = new HashMap();
                        messageMap.put("Message",downloadUri);
                        messageMap.put("From",senderName);
                        messageMap.put("Type","DOC");
                        messageMap.put("Name",fileName);
                        messageMap.put("Date",currentDate);
                        messageMap.put("Time",currentTime);
                        messageMap.put("ID",messagePushID);
                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messagePushID,messageMap);
                        GroupRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    Fragment fragment = new BlankFragment();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.content_screen,fragment);
                                    fragmentTransaction.commit();
                                    Toast.makeText(getContext(), "DOC Uploaded", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    loadingBar.dismiss();
                                    String message = task.getException().toString();
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
        if(requestCode==5)
        {
            Uri resultUri = data.getData();
            Cursor returnCursor = getActivity().getApplicationContext().getContentResolver().query(resultUri, null, null, null, null);
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            fileName = returnCursor.getString(nameIndex);
//            DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
//            String messagePushID = userMessageKeyRef.getKey();
            final StorageReference filePath = GroupChatPDFRef.child( fileName);
            filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        final String downloadUri = downUri.toString();
                        DatabaseReference userMessageKeyRef = GroupRef.child(groupName).child("Messages").push();
                        String messagePushID = userMessageKeyRef.getKey();
                        Map messageMap = new HashMap();
                        messageMap.put("Message",downloadUri);
                        messageMap.put("From",senderName);
                        messageMap.put("Type","TXT");
                        messageMap.put("Name",fileName);
                        messageMap.put("Date",currentDate);
                        messageMap.put("Time",currentTime);
                        messageMap.put("ID",messagePushID);
                        Map messageBodyDetails = new HashMap();
                        messageBodyDetails.put(messagePushID,messageMap);
                        GroupRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    loadingBar.dismiss();
                                    Fragment fragment = new BlankFragment();
                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.content_screen,fragment);
                                    fragmentTransaction.commit();
                                    Toast.makeText(getContext(), "TEXT Uploaded", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    loadingBar.dismiss();
                                    String message = task.getException().toString();
                                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }}
    }
}
