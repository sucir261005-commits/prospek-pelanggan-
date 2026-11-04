package com.neosantara.prospek.model;

public class LoginResponse {
    public String token;
    public String token_type;
    public User user;

    public static class User {
        public long id;
        public String name;
        public String email;
    }
}
