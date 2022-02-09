package com.m4i.manutencao.whatsappclone.model;

import com.google.firebase.database.DatabaseReference;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;

public class Conversation {

    private String idSender;
    private String idReceiver;
    private String lastMessage;
    private User userLastMessage;

    public Conversation() {
    }

    public void save() {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference conversationReference = databaseReference.child("conversation");
        conversationReference.child(this.getIdSender())
                .child(this.getIdReceiver())
                .setValue(this);
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public User getUserLastMessage() {
        return userLastMessage;
    }

    public void setUserLastMessage(User userLastMessage) {
        this.userLastMessage = userLastMessage;
    }
}
