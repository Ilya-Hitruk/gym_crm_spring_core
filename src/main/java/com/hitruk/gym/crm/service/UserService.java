package com.hitruk.gym.crm.service;

public interface UserService {
    boolean matchCredentials(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);
}
