package com.ocpay.wallet.bean.response;

import java.io.Serializable;

public class SignInResponse {

    private UserInfo data;
    private boolean success;
    private int code;

    public void setData(UserInfo data) {
        this.data = data;
    }

    public UserInfo getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class UserInfo implements Serializable {

        private int resourceOwnerId;
        private String accessToken;
        private String refreshToken;
        private String scope;
        private String message;
        private long expiresIn;

        public UserInfo() {

        }

        public UserInfo(String message) {
            this.message = message;
        }

        public void setResourceOwnerId(int resourceOwnerId) {
            this.resourceOwnerId = resourceOwnerId;
        }

        public int getResourceOwnerId() {
            return resourceOwnerId;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getScope() {
            return scope;
        }

        public void setExpiresIn(long expiresIn) {
            this.expiresIn = expiresIn;
        }

        public long getExpiresIn() {
            return expiresIn;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}