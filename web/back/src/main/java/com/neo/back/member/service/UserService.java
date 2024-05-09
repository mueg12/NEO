package com.neo.back.member.service;

public interface UserService {
    boolean changePassword(String username,String currentPassword, String newPassword);
    boolean resetPassword(String username);

}
