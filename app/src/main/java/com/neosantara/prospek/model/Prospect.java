package com.neosantara.prospek.model;

public class Prospect {
    public long id;
    public String name;
    public String company_name;
    public String job_title;
    public String email;
    public String phone;
    public String city;
    public String source;
    public String status;
    public String notes;
    public String next_follow_up_at;
    public Long assigned_user_id;
    public String created_at;
    public String updated_at;

    @Override
    public String toString() {
        StringBuilder details = new StringBuilder(name);

        if (company_name != null && !company_name.isBlank()) {
            details.append(" · ").append(company_name);
        }

        String contact = email != null && !email.isBlank() ? email : phone;

        if (contact != null && !contact.isBlank()) {
            details.append("\n").append(contact);
        }

        if (status != null && !status.isBlank()) {
            details.append(" · ").append(status);
        }

        return details.toString();
    }
}
