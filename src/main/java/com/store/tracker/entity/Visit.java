package com.store.tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer visit to the store, tracking entry/exit times,
 * purchased items, and total amount spent.
 *
 * <p>Uses {@link AuditingEntityListener} to automatically populate
 * {@code createdAt} and {@code updatedAt} timestamps.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "purchasedItems")
@Entity
@Table(name = "visits", indexes = {
    @Index(name = "idx_person_name", columnList = "personName"),
    @Index(name = "idx_entry_time", columnList = "entryTime")
})
@EntityListeners(AuditingEntityListener.class)
public class Visit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String personName;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    private LocalDateTime exitTime;

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<PurchasedItem> purchasedItems = new ArrayList<>();

    private Double totalSpent;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    /**
     * @param personName the visitor's name
     * @param entryTime  the time the visitor entered the store
     */
    public Visit(String personName, LocalDateTime entryTime) {
        this.personName = personName;
        this.entryTime = entryTime;
    }

    /**
     * Adds a purchased item to this visit, maintaining the bidirectional relationship.
     *
     * @param item the item to add; must not be {@code null}
     */
    public void addPurchasedItem(PurchasedItem item) {
        purchasedItems.add(item);
        item.setVisit(this);
    }

    /**
     * Removes a purchased item from this visit, clearing the bidirectional relationship.
     *
     * @param item the item to remove; must not be {@code null}
     */
    public void removePurchasedItem(PurchasedItem item) {
        purchasedItems.remove(item);
        item.setVisit(null);
    }
}
