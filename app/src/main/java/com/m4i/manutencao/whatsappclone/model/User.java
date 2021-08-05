//
// Author: Bruno Lima
// Company: M4I
// 03/08/2021 at 18:20
//

package com.m4i.manutencao.whatsappclone.model;

public class User {
    private String name;
    private String email;
    private String password;

    public User() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
