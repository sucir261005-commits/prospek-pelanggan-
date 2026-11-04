package com.neosantara.prospek.model;

public class ProspectPayload {
    public String name;
    public String email;
    public String phone;
    public String source;
    public String status;

    public ProspectPayload(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.source = "website";
        this.status = "new";
    }
}
