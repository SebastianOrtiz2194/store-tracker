package com.store.tracker.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa la visita de una persona al establecimiento.
 * Incluye auditoría automática y relación con los artículos comprados.
 */
@Entity
@Table(name = "visits", indexes = {
    @Index(name = "idx_person_name", columnList = "personName"),
    @Index(name = "idx_entry_time", columnList = "entryTime")
})
@EntityListeners(AuditingEntityListener.class)
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String personName;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;
    
    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchasedItem> purchasedItems = new ArrayList<>();

    private Double totalSpent;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Visit() {
    }

    public Visit(String personName, LocalDateTime entryTime) {
        this.personName = personName;
        this.entryTime = entryTime;
    }

    // Helper methods for the relationship
    public void addPurchasedItem(PurchasedItem item) {
        purchasedItems.add(item);
        item.setVisit(this);
    }

    public void removePurchasedItem(PurchasedItem item) {
        purchasedItems.remove(item);
        item.setVisit(null);
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPersonName() { return personName; }
    public void setPersonName(String personName) { this.personName = personName; }

    public LocalDateTime getEntryTime() { return entryTime; }
    public void setEntryTime(LocalDateTime entryTime) { this.entryTime = entryTime; }

    public LocalDateTime getExitTime() { return exitTime; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }

    public List<PurchasedItem> getPurchasedItems() { return purchasedItems; }
    public void setPurchasedItems(List<PurchasedItem> purchasedItems) { this.purchasedItems = purchasedItems; }

    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
