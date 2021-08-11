//
// Author: Bruno Lima
// Company: M4I
// 03/08/2021 at 18:20
//

package com.m4i.manutencao.whatsappclone.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.m4i.manutencao.whatsappclone.config.FirebaseConfiguration;

public class User {

    private String userId;
    private String name;
    private String email;
    private String password;

    public User() {
    }

    public void save() {

        DatabaseReference firebaseRef = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference currentUser = firebaseRef.child("users").child(getUserId());
        currentUser.setValue(this);
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
