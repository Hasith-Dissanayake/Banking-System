package com.bank.app.core.model;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_branches")
@NamedQueries({
    @NamedQuery(name = "BankBranch.findAll", query = "SELECT b FROM BankBranch b ORDER BY b.branchName"),
    @NamedQuery(name = "BankBranch.findById", query = "SELECT b FROM BankBranch b WHERE b.id = :id"),
    @NamedQuery(name = "BankBranch.findActive", query = "SELECT b FROM BankBranch b WHERE b.isActive = true ORDER BY b.branchName")
})
@Cacheable(false)
public class BankBranch implements java.io.Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "branch_name", unique = true, nullable = false)
    private String branchName;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    public BankBranch() {
    }
    
    public BankBranch(String branchName) {
        this.branchName = branchName;
        this.isActive = true;
    }
    
    public BankBranch(String branchName, Boolean isActive) {
        this.branchName = branchName;
        this.isActive = isActive;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getBranchName() {
        return branchName;
    }
    
    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    @Override
    public String toString() {
        return branchName;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        BankBranch that = (BankBranch) obj;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
} 