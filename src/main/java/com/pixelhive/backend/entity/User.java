package com.pixelhive.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

// @Entity tells Spring this Java class maps to a database table.
// @Table(name = "users") says which table: the one you built earlier.
@Entity
@Table(name = "users")
public class User {

    // @Id marks the primary key. IDENTITY means the DATABASE generates
    // the id (matches your "GENERATED ALWAYS AS IDENTITY" column).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    // The Java field is 'passwordHash', the column is 'password_hash'.
    // @JsonIgnore keeps this OUT of API responses so we never leak the hash.
    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String role = "client";

    // The database fills these in automatically (DEFAULT now()), so we mark
    // them not-insertable / not-updatable: the app reads them, never writes them.
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    // JPA requires an empty constructor.
    public User() {
    }

    // Getters and setters: how other code reads/changes these fields.
    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
