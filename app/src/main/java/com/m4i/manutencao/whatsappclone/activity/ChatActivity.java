package com.m4i.manutencao.whatsappclone.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.adapter.MessagesAdapter;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.Base64Custom;
import com.m4i.manutencao.whatsappclone.helper.FirebaseUserAccess;
import com.m4i.manutencao.whatsappclone.model.Conversation;
import com.m4i.manutencao.whatsappclone.model.Group;
import com.m4i.manutencao.whatsappclone.model.Message;
import com.m4i.manutencao.whatsappclone.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private static final int CAMERA_SELECTED = 100;
    private final List<Message> messages = new ArrayList<>();
    private TextView tvName;
    private CircleImageView civPhoto;
    private User userSender, userRecipient;
    private EditText sendMessage;
    private ImageView ivCamera;
    //User that sends a message and the receiver of the message
    private String idUserSender;
    private String idUserRecipient;
    //Recycler view variables
    private RecyclerView rvMessages;
    private MessagesAdapter messagesAdapter;
    //Firebase
    private DatabaseReference database;
    private DatabaseReference messagesRef;
    private ChildEventListener childEventListenerMessages;
    private StorageReference storage;
    //Group
    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Toolbar config
        Toolbar toolbar = findViewById(R.id.chat_activity_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initial settings
        tvName = findViewById(R.id.chat_activity_tvName);
        civPhoto = findViewById(R.id.chat_activity_civPhotoChat);
        sendMessage = findViewById(R.id.content_chat_etSendMessage);
        ivCamera = findViewById(R.id.content_chat_ivCamera);

        //RecyclerView definition
        rvMessages = findViewById(R.id.content_chat_rvMessages);

        //Recover data from user that is going to send. Is the on that is logged in
        idUserSender = FirebaseUserAccess.getUserId();
        userSender = FirebaseUserAccess.getDataFromLoggedUser();

        //Recover user data that was selected
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.containsKey("groupChat")) {
                group = (Group) bundle.getSerializable("groupChat");
                idUserRecipient = group.getId();
                tvName.setText(group.getName());

                String photo = group.getPhoto();
                if (photo != null) {
                    Uri url = Uri.parse(photo);
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(civPhoto);
                } else {
                    civPhoto.setImageResource(R.drawable.standard_photo_24);
                }


            } else {
                userRecipient = (User) bundle.getSerializable("contactsChat");
                tvName.setText(userRecipient.getName());

                //get photo
                String photo = userRecipient.getPhoto();
                if (photo != null) {
                    Uri url = Uri.parse(userRecipient.getPhoto());
                    Glide.with(ChatActivity.this)
                            .load(url)
                            .into(civPhoto);
                } else {
                    civPhoto.setImageResource(R.drawable.standard_photo_24);
                }

                //Recover data from the user that receives the message
                idUserRecipient = Base64Custom.encodeBase64(userRecipient.getEmail());
            }


        }

        //Adapter config
        messagesAdapter = new MessagesAdapter(messages, getApplicationContext());

        //RecyclerView config
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setHasFixedSize(true);
        rvMessages.setAdapter(messagesAdapter);

        //Database reference
        database = FirebaseConfiguration.getFirebaseDatabase();
        storage = FirebaseConfiguration.getFirebaseStorage();
        messagesRef = database.child("messages")
                .child(idUserSender)
                .child(idUserRecipient);

        //Click event

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, CAMERA_SELECTED);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case CAMERA_SELECTED:
                        image = (Bitmap) data.getExtras().get("data");
                        break;
                }
                if (image != null) {
                    //Compress image
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();

                    //Create unique photo name so that we do not overwrite the same reference in Firebase
                    String imageName = UUID.randomUUID().toString();

                    //Save image into Firebase
                    final StorageReference imageRef = storage.child("images")
                            .child("photos")
                            .child(idUserSender)
                            .child(imageName);

                    UploadTask uploadtask = imageRef.putBytes(imageData);
                    uploadtask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Error in uploading photo to Firebase!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (userRecipient != null) {
                                        Uri url = task.getResult();
                                        Message msg = new Message();
                                        msg.setIdUser(idUserSender);
                                        msg.setMessage("image.jpeg");
                                        msg.setPhoto(url.toString());

                                        //Save photo in sender
                                        saveMessage(idUserSender, idUserRecipient, msg);

                                        //Save photo in receiver
                                        saveMessage(idUserRecipient, idUserSender, msg);
                                    } else {

                                        for (User member : group.getMembers()) {

                                            String idGroupSender = Base64Custom.encodeBase64(member.getEmail());
                                            String idUserLoggedGroup = FirebaseUserAccess.getUserId();

                                            Uri url = task.getResult();
                                            Message message = new Message();
                                            message.setIdUser(idUserLoggedGroup);
                                            message.setMessage("image.jpeg");
                                            message.setName(userSender.getName());
                                            message.setPhoto(url.toString());

                                            //Save message to member
                                            saveMessage(idGroupSender, idUserRecipient, message);

                                            //Save conversation
                                            saveConversation(idGroupSender, idUserRecipient, userRecipient, message, true);
                                        }
                                    }

                                    Toast.makeText(ChatActivity.this, "Success in sending the photo to Firebase", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(View v) {
        String messageTxt = sendMessage.getText().toString();

        if (!messageTxt.isEmpty()) {

            if (userRecipient != null) {

                Message msg = new Message();
                msg.setIdUser(idUserSender);
                msg.setMessage(messageTxt);

                //Save message to receiver
                saveMessage(idUserSender, idUserRecipient, msg);

                //Save message to sender
                saveMessage(idUserRecipient, idUserSender, msg);

                //Save conversation in sender
                saveConversation(idUserSender, idUserRecipient, userRecipient, msg, false);

                //save conversation in receiver
                saveConversation(idUserRecipient, idUserSender, userSender, msg, false);

            } else {

                for (User member : group.getMembers()) {

                    String idGroupSender = Base64Custom.encodeBase64(member.getEmail());
                    String idUserLoggedGroup = FirebaseUserAccess.getUserId();

                    Message message = new Message();
                    message.setIdUser(idUserLoggedGroup);
                    message.setMessage(messageTxt);
                    message.setName(userSender.getName());

                    //Save message to member
                    saveMessage(idGroupSender, idUserRecipient, message);

                    //Save conversation
                    saveConversation(idGroupSender, idUserRecipient, userRecipient, message, true);
                }
            }


        } else {
            Toast.makeText(ChatActivity.this, "Please write a message to send!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveConversation(String idSender, String idRecipient, User userToShow, Message msg, boolean isGroup) {

        Conversation conversationSender = new Conversation();
        conversationSender.setIdSender(idSender);
        conversationSender.setIdRecipient(idRecipient);
        conversationSender.setLastMessage(msg.getMessage());

        if (isGroup) {
            //Group coversation
            conversationSender.setIsGroup("true");
            conversationSender.setGroup(group);
        } else {
            //Normal conversation
            conversationSender.setUserLastMessage(userToShow);
            conversationSender.setIsGroup("false");
        }

        conversationSender.save();

    }

    private void saveMessage(String idSender, String idRecipient, Message msg) {
        DatabaseReference database = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference msgRef = database.child("messages");
        msgRef.child(idSender)
                .child(idRecipient)
                .push()
                .setValue(msg);


        //clean the message box
        sendMessage.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        recoverMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        messagesRef.removeEventListener(childEventListenerMessages);
    }

    private void recoverMessages() {

        messages.clear();

        childEventListenerMessages = messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message msg = snapshot.getValue(Message.class);
                messages.add(msg);
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}