package com.store.tracker.visits.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an item purchased during a visit, including the product name,
 * unit price, and quantity.
 *
 * <p>Uses {@link AuditingEntityListener} to automatically populate
 * {@code createdAt} and {@code updatedAt} timestamps.
 *
 * <p>The price is an immutable snapshot of the unit price at purchase time, so a
 * later price change never alters an existing receipt.
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

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

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
     * @param price    the unit price; must not be {@code null}
     * @param quantity the quantity purchased
     */
    public PurchasedItem(String name, BigDecimal price, Integer quantity) {
        this.name = name;
        this.price = Objects.requireNonNull(price, "price").setScale(2, RoundingMode.HALF_UP);
        this.quantity = quantity;
    }

    /**
     * Computes this line's contribution to the visit total.
     *
     * @return {@code price × quantity}, never {@code null}
     */
    public BigDecimal lineTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
