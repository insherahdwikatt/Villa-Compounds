package com.example.ri;

public class UserInfo {
    private static UserInfo instance;
    private String role;
    private String userId;
    private boolean isConfigured = false;

    private UserInfo() {}

    public static synchronized UserInfo getInstance() {
        if (instance == null) {
            instance = new UserInfo();
        }
        return instance;
    }

    public void setUserInfo(String role, String userId) {
        if (!isConfigured) {
            this.role = role;
            this.userId = userId;
            this.isConfigured = true;
        } else {
            throw new IllegalStateException("Config already set and cannot be changed.");
        }
    }

    public String getRole() {
        return role;
    }

    public String getUserId() {
        return userId;
    }

    public void reset() {
        this.role = null;
        this.userId = null;
        this.isConfigured = false;
    }
}
