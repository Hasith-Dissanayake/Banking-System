package com.bank.app.core.service;

import com.bank.app.core.model.User;
import com.bank.app.core.model.BankBranch;
import jakarta.ejb.Remote;
import java.util.List;

@Remote
public interface UserService {
    User getUserById(Long id);
    User getUserByEmail(String email);
    User getUserByNic(String nic);
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
    boolean validate(String email, String password);
    List<User> getAllUsers();
    
    // Super admin setup methods
    boolean isSuperAdminExists();
    void createSuperAdmin(String name, String email, String contact, String password, String nic, String address, BankBranch bankBranch);
    void createSuperAdminWithVerification(String name, String email, String contact, String password, String nic, String address, BankBranch bankBranch);
}

