package com.bank.app.ejb.bean;

import com.bank.app.core.model.User;
import com.bank.app.core.model.UserType;
import com.bank.app.core.model.Status;
import com.bank.app.core.model.BankBranch;
import com.bank.app.core.service.UserService;
import com.bank.app.core.util.Encryption;
import com.bank.app.core.interceptor.SecurityLogged;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

@Stateless
@SecurityLogged
public class UserSessionBean implements UserService {
    @PersistenceContext
    private EntityManager em;

    @Override
    public User getUserById(Long id) {
        return em.find(User.class, id);
    }

    @Override
    public User getUserByEmail(String email) {
        try {
            TypedQuery<User> query = em.createNamedQuery("User.findByEmail", User.class)
                    .setParameter("email", email);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public User getUserByNic(String nic) {
        try {
            TypedQuery<User> query = em.createNamedQuery("User.findByNic", User.class)
                    .setParameter("nic", nic);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void addUser(User user) {
        // Only encrypt password if it's not already encrypted (MD5 produces 32-char hex)
        if (user.getPassword() != null && user.getPassword().length() != 32) {
            user.setPassword(Encryption.encrypt(user.getPassword()));
        }
        em.persist(user);
    }

    @Override
    public void updateUser(User user) {
        // If password is being updated, encrypt it
        User existingUser = em.find(User.class, user.getId());
        if (existingUser != null && !existingUser.getPassword().equals(user.getPassword())) {
            user.setPassword(Encryption.encrypt(user.getPassword()));
        }
        em.merge(user);
    }

    @Override
    public void deleteUser(User user) {
        User userToDelete = em.find(User.class, user.getId());
        if (userToDelete != null) {
            em.remove(userToDelete);
        }
    }

    @Override
    public boolean validate(String email, String password) {
        try {
            // Get user by email first
            User user = getUserByEmail(email);
            if (user == null) {
                return false;
            }
            
            // Check if user is active
            if (user.getStatus() != Status.ACTIVE) {
                return false;
            }
            
            // Compare encrypted passwords
            String encryptedPassword = Encryption.encrypt(password);
            return user.getPassword().equals(encryptedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() {
        return em.createNamedQuery("User.findAll", User.class).getResultList();
    }

    public boolean isSuperAdminExists() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.userType = :userType", Long.class)
            .setParameter("userType", UserType.SUPER_ADMIN);
        return query.getSingleResult() > 0;
    }

    public void createSuperAdmin(String name, String email, String contact, String password, String nic, String address, BankBranch bankBranch) {
        User superAdmin = new User(name, email, contact, password, nic, address, bankBranch);
        superAdmin.setUserType(UserType.SUPER_ADMIN);
        superAdmin.setStatus(Status.ACTIVE);
        addUser(superAdmin);
    }
    
    public void createSuperAdminWithVerification(String name, String email, String contact, String password, String nic, String address, BankBranch bankBranch) {
        User superAdmin = new User(name, email, contact, password, nic, address, bankBranch);
        superAdmin.setUserType(UserType.SUPER_ADMIN);
        superAdmin.setStatus(Status.INACTIVE); // Will be activated after email verification
        addUser(superAdmin);
    }
}
