package com.store.tracker.visits.entity;

import com.store.tracker.exception.VisitAlreadyClosedException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a customer visit to the store, tracking entry/exit times,
 * purchased items, and total amount spent.
 *
 * <p>Uses {@link AuditingEntityListener} to automatically populate
 * {@code createdAt} and {@code updatedAt} timestamps.
 *
 * <p>The exit timestamp and the total spent can only change through
 * {@link #close(LocalDateTime, List)}, which enforces the rule that a visit is
 * closed at most once and derives the total from the purchased lines.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "purchasedItems")
@Entity
@Table(name = "visits", indexes = {
    @Index(name = "idx_visits_entry_time", columnList = "entryTime"),
    @Index(name = "idx_visits_exit_time", columnList = "exitTime")
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

    @Setter(AccessLevel.NONE)
    private LocalDateTime exitTime;

    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<PurchasedItem> purchasedItems = new ArrayList<>();

    @Column(precision = 12, scale = 2)
    @Setter(AccessLevel.NONE)
    private BigDecimal totalSpent;

    /**
     * Optimistic-locking token. Two concurrent exits cannot both succeed: the
     * second flush fails and is reported as {@code 409 Conflict}.
     */
    @Version
    @Column(nullable = false)
    private Long version;

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
     * Closes this visit exactly once: stamps the exit time, replaces any
     * purchased items with the supplied ones and derives the total spent from
     * their line totals.
     *
     * @param at    the time the visitor left the store
     * @param items the purchased items of this visit; may be empty but not {@code null}
     * @throws VisitAlreadyClosedException if the visit has already been closed
     */
    public void close(LocalDateTime at, List<PurchasedItem> items) {
        Objects.requireNonNull(at, "at");
        Objects.requireNonNull(items, "items");
        if (isClosed()) {
            throw new VisitAlreadyClosedException(id);
        }

        this.exitTime = at;
        this.purchasedItems.clear();
        items.forEach(this::addPurchasedItem);
        this.totalSpent = items.stream()
                .map(PurchasedItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * @return {@code true} when the visitor has already left the store
     */
    public boolean isClosed() {
        return exitTime != null;
    }

    /**
     * Adds a purchased item to this visit, maintaining the bidirectional relationship.
     *
     * @param item the item to add; must not be {@code null}
     */
    public void addPurchasedItem(PurchasedItem item) {
        Objects.requireNonNull(item, "item");
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
