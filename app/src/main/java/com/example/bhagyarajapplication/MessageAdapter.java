package com.example.bhagyarajapplication;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.view.View.GONE;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{

    private List<Messages> userMessageList;
    private Context context;
    private View view;
    private String groupName,senderName;

    public MessageAdapter(List<Messages> userMessageList,Context context) {
        this.userMessageList = userMessageList;
        this.context = context;
    }

    public class MessageViewHolder extends ViewHolder{
        TextView messageText,nameText,imageSender,pdfSender,pdfName;
        TextView messageDateTime, imageDateTime, pdfDateTime;
        ImageView imageView;
        ImageButton downloadButton;
        RelativeLayout imageLayout,messageLayout,pdfLayout;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageSender = itemView.findViewById(R.id.group_chat_image_sender);
            imageView = itemView.findViewById(R.id.group_chat_image_view_abc);
            messageText = itemView.findViewById(R.id.message_text_layout);
            nameText = itemView.findViewById(R.id.message_text_sender);
            pdfSender = itemView.findViewById(R.id.pdf_sender_name_text);
            downloadButton = itemView.findViewById(R.id.pdf_download_button);
            pdfName = itemView.findViewById(R.id.pdf_name_text_view);
            imageLayout = itemView.findViewById(R.id.image_message_linear_layout);
            messageLayout = itemView.findViewById(R.id.text_message_linear_layout);
            pdfLayout = itemView.findViewById(R.id.pdf_message_linear_layout);
            messageDateTime = itemView.findViewById(R.id.message_date_and_time);
            imageDateTime = itemView.findViewById(R.id.image_message_date_and_time);
            pdfDateTime = itemView.findViewById(R.id.pdf_message_date_and_time);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout,parent,false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, final int position) {
        groupName = GroupChatActivity.getGroupName();
        senderName = GroupChatActivity.getSenderName();
        final Messages messages = userMessageList.get(position);
        String type = messages.getType();
        final String fromUserID = messages.getFrom();
        final String messageABC = messages.getMessage();
        String date = messages.getDate();
        String time = messages.getTime();
        final String id = messages.getID();
        final DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName).child("Messages");
        if(type.equals("Text"))
        {
            holder.messageText.setText(messageABC);
            holder.nameText.setText(fromUserID);
            holder.messageDateTime.setText(date +" "+time);
            holder.messageLayout.setVisibility(View.VISIBLE);
            holder.imageLayout.setVisibility(GONE);
            holder.pdfLayout.setVisibility(GONE);
            holder.messageLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(senderName.equals(fromUserID))
                                Ref.child(id).removeValue();
                            return true;
                        }
                    });
                }
            });
        }
        if(type.equals("Image"))
        {
            holder.imageSender.setText(fromUserID);
            Picasso.get().load(messageABC).into(holder.imageView);
            holder.imageDateTime.setText(date +" "+time);
            holder.imageLayout.setVisibility(View.VISIBLE);
            holder.messageLayout.setVisibility(GONE);
            holder.pdfLayout.setVisibility(GONE);
            holder.imageLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ViewImageActivity.class);
                    intent.putExtra("image",messageABC);
                    context.startActivity(intent);
                }
            });
            holder.imageLayout.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(senderName.equals(fromUserID))
                            Ref.child(id).removeValue();
                            return true;
                        }
                    });
                }
            });
        }
        if(type.equals("PDF"))
        {
            holder.pdfSender.setText(fromUserID);
            holder.pdfName.setText(messages.getName());
            holder.pdfDateTime.setText(date +" "+time);
            holder.pdfLayout.setVisibility(View.VISIBLE);
            holder.imageLayout.setVisibility(GONE);
            holder.messageLayout.setVisibility(GONE);
            holder.downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadPDF(context,messages.getName(), DIRECTORY_DOWNLOADS,messageABC);
                }
            });
            holder.pdfName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(messageABC), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "Failed To Open The PDF, Try Again....", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.pdfName.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(senderName.equals(fromUserID))
                            Ref.child(id).removeValue();
                            return true;
                        }
                    });
                }
            });
        }
        if(type.equals("PPT"))
        {
            holder.pdfSender.setText(fromUserID);
            holder.pdfName.setText(messages.getName());
            holder.pdfDateTime.setText(date +" "+time);
            holder.pdfLayout.setVisibility(View.VISIBLE);
            holder.imageLayout.setVisibility(GONE);
            holder.messageLayout.setVisibility(GONE);
            holder.downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadPDF(context,messages.getName(), DIRECTORY_DOWNLOADS,messageABC);
                }
            });
            holder.pdfName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(messageABC), "application/mspowerpoint");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "Failed To Open The PDF, Try Again....", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.pdfName.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(senderName.equals(fromUserID))
                                Ref.child(id).removeValue();
                            return true;
                        }
                    });
                }
            });
        }
        if(type.equals("DOC"))
        {
            holder.pdfSender.setText(fromUserID);
            holder.pdfName.setText(messages.getName());
            holder.pdfDateTime.setText(date +" "+time);
            holder.pdfLayout.setVisibility(View.VISIBLE);
            holder.imageLayout.setVisibility(GONE);
            holder.messageLayout.setVisibility(GONE);
            holder.downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadPDF(context,messages.getName(), DIRECTORY_DOWNLOADS,messageABC);
                }
            });
            holder.pdfName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(messageABC), "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "Failed To Open The PDF, Try Again....", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.pdfName.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(senderName.equals(fromUserID))
                                Ref.child(id).removeValue();
                            return true;
                        }
                    });
                }
            });
        }
        if(type.equals("TXT"))
        {
            holder.pdfSender.setText(fromUserID);
            holder.pdfName.setText(messages.getName());
            holder.pdfDateTime.setText(date +" "+time);
            holder.pdfLayout.setVisibility(View.VISIBLE);
            holder.imageLayout.setVisibility(GONE);
            holder.messageLayout.setVisibility(GONE);
            holder.downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadPDF(context,messages.getName(), DIRECTORY_DOWNLOADS,messageABC);
                }
            });
            holder.pdfName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(messageABC), "text/plain");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "Failed To Open The PDF, Try Again....", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.pdfName.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(senderName.equals(fromUserID))
                                Ref.child(id).removeValue();
                            return true;
                        }
                    });
                }
            });
        }
    }

    private void DownloadPDF(Context context, String fileName, String fileDestination, String Url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(Url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,fileDestination,fileName);
        downloadManager.enqueue(request);
    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }
}