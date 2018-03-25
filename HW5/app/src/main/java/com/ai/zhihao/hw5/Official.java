package com.ai.zhihao.hw5;

import java.util.HashMap;

/**
 * Created by zhihaoai on 3/24/18.
 */

public class Official {

    private static String defaultString = "No Data Provided";
    private String office;
    private String name;
    private String address;
    private String party;
    private String phone;
    private String url;
    private String email;
    private String photoUrl;
    private HashMap<String, String> channels;

    public Official(String office, String name, String address, String party, String phone, String url, String email, String photoUrl, HashMap<String, String> channels) {
        this.office = office;
        this.name = name;
        this.address = address;
        this.party = party;
        this.phone = phone;
        this.url = url;
        this.email = email;
        this.photoUrl = photoUrl;
        this.channels = channels;
    }

    public String getOffice() {
        return office;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getParty() {
        return party;
    }

    public String getUrl() {
        return url;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public HashMap<String, String> getChannels() {
        return channels;
    }
}
