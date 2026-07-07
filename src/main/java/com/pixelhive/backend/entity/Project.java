package com.pixelhive.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // THE RELATIONSHIP: many projects can belong to one creator (a User).
    // @ManyToOne + @JoinColumn(name = "creator_id") turns the foreign key
    // into a real link - project.getCreator() gives you the whole User.
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // The client is optional early on, so no "nullable = false" here.
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    @Column(nullable = false)
    private String title;

    private String description;

    // Money = BigDecimal (exact), never double. Maps to NUMERIC(12,2).
    private BigDecimal price;

    @Column(nullable = false)
    private String status = "draft";

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    public Project() {
    }

    public Long getId() {
        return id;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
