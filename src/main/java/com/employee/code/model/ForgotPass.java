package com.employee.code.model;

public class ForgotPass {
    private String email;
    private String newPassword;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "forgotPass{" +
                "email='" + email + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
