//
// Author: Bruno Lima
// Company: M4I
// 03/08/2021 at 18:20
//

package com.m4i.manutencao.whatsappclone.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;
import com.m4i.manutencao.whatsappclone.helper.FirebaseUserAccess;

import java.util.HashMap;
import java.util.Map;

public class User {

    //Variables used
    private String userId;
    private String name;
    private String email;
    private String password;
    private String photo;

    public User() {
    }

    public void save() {

        DatabaseReference firebaseRef = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference currentUser = firebaseRef.child("users").child(getUserId());
        currentUser.setValue(this);
    }

    public void update() {

        String userIdentifier = FirebaseUserAccess.getUserId();
        DatabaseReference database = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference userRef = database.child("users")
                .child(userIdentifier);
        Map<String, Object> usersValues = convertToMap();
        userRef.updateChildren(usersValues);
    }

    @Exclude
    public Map<String, Object> convertToMap() {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", getEmail());
        userMap.put("name", getName());
        userMap.put("photo", getPhoto());
        return userMap;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Exclude
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
