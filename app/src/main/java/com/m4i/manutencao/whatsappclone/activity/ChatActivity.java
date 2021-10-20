package com.m4i.manutencao.whatsappclone.activity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.m4i.manutencao.whatsappclone.R;
import com.m4i.manutencao.whatsappclone.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private TextView tvName;
    private CircleImageView civPhoto;
    private User userSelectedToSendMessage;

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
        civPhoto = findViewById(R.id.chat_activity_ciPhotoChat);

        //Recover user data that was selected
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userSelectedToSendMessage = (User) bundle.getSerializable("contactsChat");
            tvName.setText(userSelectedToSendMessage.getName());

            //get photo
            String photo = userSelectedToSendMessage.getPhoto();
            if (photo != null) {
                Uri url = Uri.parse(userSelectedToSendMessage.getPhoto());
                Glide.with(ChatActivity.this)
                        .load(url)
                        .into(civPhoto);
            } else {
                civPhoto.setImageResource(R.drawable.standard_photo_24);
            }
        }

    }
}
