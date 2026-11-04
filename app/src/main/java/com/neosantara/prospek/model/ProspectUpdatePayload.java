package com.neosantara.prospek.model;

public class ProspectUpdatePayload {
    public String name;
    public String email;
    public String phone;

    public ProspectUpdatePayload(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}
