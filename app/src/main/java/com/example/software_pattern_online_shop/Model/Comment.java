package com.example.software_pattern_online_shop.Model;

import java.io.Serializable;

public class Comment implements Serializable  {
    String comment, userName;

    public Comment() {}

    public Comment(String comment, String userName) {
        this.comment = comment;
        this.userName = userName;
    }

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
