package com.etna.gpe.mycloseshop.ms_shop_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "location")
@Getter
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    String address;

    @Column(nullable = false)
    String city;

    @Column(name = "postal_code", nullable = false)
    String postalCode;

    @Column(name = "optional_information")
    String optionalInformation;

    @OneToOne
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Date.from(Instant.now());
        this.updatedAt = Date.from(Instant.now());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Date.from(Instant.now());
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setOptionalInformation(String optionalInformation) {
        this.optionalInformation = optionalInformation;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
