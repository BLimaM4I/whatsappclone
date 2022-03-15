//
// Author: Bruno Lima
// Company: M4I
// 20/10/2021 at 12:07
//

package com.m4i.manutencao.whatsappclone.model;

public class Message {

    private String idUser;
    private String message;
    private String Photo;
    private String name;

    public Message() {
        this.setName("");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }
}
