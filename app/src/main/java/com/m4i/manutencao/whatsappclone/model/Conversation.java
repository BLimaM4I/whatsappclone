package com.m4i.manutencao.whatsappclone.model;

import com.google.firebase.database.DatabaseReference;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;

public class Conversation {

    private String idSender;
    private String idRecipient;
    private String lastMessage;
    private User userLastMessage;
    private String isGroup;
    private Group group;

    public Conversation() {
        this.setIsGroup("false");
    }

    public void save() {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference conversationReference = databaseReference.child("conversations");
        conversationReference.child(this.getIdSender())
                .child(this.getIdRecipient())
                .setValue(this);
    }

    public String getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(String isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdRecipient() {
        return idRecipient;
    }

    public void setIdRecipient(String idRecipient) {
        this.idRecipient = idRecipient;
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