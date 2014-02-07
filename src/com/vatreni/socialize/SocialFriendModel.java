package com.vatreni.socialize;

/**
 * Created by Ghaydysh Taras
 * gorthaur12@gmail.com
 */
public class SocialFriendModel {
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private int id;
    private boolean checked = false;

    public SocialFriendModel(int id, String firstName, String lastName, String avatarUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.avatarUrl = avatarUrl;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}

