/*
 *    Created by BLimaM4I
 *    Date: 08/03/2022
 */

package com.m4i.manutencao.whatsappclone.model;

import com.google.firebase.database.DatabaseReference;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.Base64Custom;

import java.io.Serializable;
import java.util.List;

public class Group implements Serializable {

    private String id;
    private String name;
    private String photo;
    private List<User> members;

    public Group() {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference groupRef = databaseReference.child("groups");
        String idGroupFirebase = groupRef.push().getKey();
        setId(idGroupFirebase);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void save() {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference groupRef = databaseReference.child("groups");
        groupRef.child(getId()).setValue(this);

        //save conversation for all group members
        for (User member : getMembers()) {

            String idSender = Base64Custom.encodeBase64(member.getEmail());
            String idRecipient = getId();

            Conversation conversation = new Conversation();
            conversation.setIdSender(idSender);
            conversation.setIdRecipient(idRecipient);
            conversation.setLastMessage("");
            conversation.setIsGroup("true");
            conversation.setGroup(this);
            conversation.save();
        }
    }
}