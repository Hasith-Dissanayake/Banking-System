package com.bank.app.web.security;

import com.bank.app.core.model.User;
import com.bank.app.core.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Set;

@ApplicationScoped
public class AppIdentityStore implements IdentityStore {

    private UserService getUserService() {
        try {
            InitialContext context = new InitialContext();
            return (UserService) context.lookup("java:global/banking-system-ear/auth-module/UserSessionBean!com.bank.app.core.service.UserService");
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup UserService", e);
        }
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        if (credential instanceof UsernamePasswordCredential){
            UsernamePasswordCredential upc = (UsernamePasswordCredential) credential;
            UserService userService = getUserService();

            if (userService.validate(upc.getCaller(), upc.getPasswordAsString())){
                User user = userService.getUserByEmail(upc.getCaller());
                if (user != null) {
                    return new CredentialValidationResult(user.getEmail(), Set.of(user.getUserType().name()));
                }
            }
        }
        return CredentialValidationResult.INVALID_RESULT;
    }
}
