package com.mcy.framework.user;

import android.content.Context;

public class User {

    private static User instance;

    private Context context;

    public static User getInstance(Context context) {
        if (instance == null) {
            synchronized (User.class) {
                if (instance == null) {
                    instance = new User(context);
                }
            }
        }
        return instance;
    }

    public User(Context context) {
        this.context = context;
    }

    public static User getInstance() {
        return instance;
    }

    public static void updateCurrentUser(User user){
        instance = user;
    }

    private int id;
    private String username;
    private String password;
    private String email;
    private int sex;
    private String address;
    private String create_time;
    private String token = "";

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User() {
    }
}
