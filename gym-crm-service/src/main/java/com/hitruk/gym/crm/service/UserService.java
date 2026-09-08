package com.hitruk.gym.crm.service;

public interface UserService {
    void changePassword(String username, String oldPassword, String newPassword);
}
