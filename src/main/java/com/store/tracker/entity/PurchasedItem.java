package com.store.tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Represents an item purchased during a visit, including the product name,
 * unit price, and quantity.
 *
 * <p>Uses {@link AuditingEntityListener} to automatically populate
 * {@code createdAt} and {@code updatedAt} timestamps.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "visit")
@Entity
@Table(name = "purchased_items")
@EntityListeners(AuditingEntityListener.class)
public class PurchasedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime updatedAt;

    /**
     * @param name     the product name
     * @param price    the unit price
     * @param quantity the quantity purchased
     * @param visit    the parent visit; must not be {@code null}
     */
    public PurchasedItem(String name, Double price, Integer quantity, Visit visit) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.visit = visit;
    }
}
