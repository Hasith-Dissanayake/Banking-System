package com.bank.app.web.util;

import com.bank.app.core.model.AccountType;
import com.bank.app.core.model.AccountStatus;

public class AccountUtils {
    
    public static String getAccountTypeColor(AccountType type) {
        if (type == null || type.getName() == null) return "secondary";
        String name = type.getName();
        switch (name) {
            case "SAVINGS": return "success";
            case "CHECKING": return "primary";
            case "FIXED_DEPOSIT": return "warning";
            default: return "secondary";
        }
    }
    
    public static String getStatusColor(AccountStatus status) {
        if (status == null) return "secondary";
        
        switch (status) {
            case ACTIVE: return "success";
            case INACTIVE: return "secondary";
            case SUSPENDED: return "warning";
            case CLOSED: return "danger";
            case PENDING: return "info";
            case DORMANT: return "dark";
            default: return "secondary";
        }
    }
    
    public static String getAccountTypeIcon(AccountType type) {
        if (type == null || type.getName() == null) return "fas fa-question";
        String name = type.getName();
        switch (name) {
            case "SAVINGS": return "fas fa-piggy-bank";
            case "CHECKING": return "fas fa-credit-card";
            case "FIXED_DEPOSIT": return "fas fa-lock";
            default: return "fas fa-question";
        }
    }
    
    public static String getStatusIcon(AccountStatus status) {
        if (status == null) return "fas fa-question";
        
        switch (status) {
            case ACTIVE: return "fas fa-check-circle";
            case INACTIVE: return "fas fa-pause-circle";
            case SUSPENDED: return "fas fa-exclamation-triangle";
            case CLOSED: return "fas fa-times-circle";
            case PENDING: return "fas fa-clock";
            case DORMANT: return "fas fa-bed";
            default: return "fas fa-question";
        }
    }
} 