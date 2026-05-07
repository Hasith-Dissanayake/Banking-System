package com.bank.app.core.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "account_types")
public class AccountType implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    public AccountType() {}
    public AccountType(String name) { this.name = name; }
    public AccountType(String name, String description) { this.name = name; this.description = description; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}