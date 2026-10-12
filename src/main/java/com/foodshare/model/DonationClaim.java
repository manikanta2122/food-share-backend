package com.foodshare.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation_claims")
public class DonationClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donation_id", nullable = false)
    private FoodDonation donation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @Column(length = 500)
    private String note;

    private Integer estimatedPeopleFed;

    private String contactPerson;

    private String contactPhone;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public DonationClaim() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public FoodDonation getDonation() { return donation; }
    public void setDonation(FoodDonation donation) { this.donation = donation; }

    public User getClaimant() { return claimant; }
    public void setClaimant(User claimant) { this.claimant = claimant; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Integer getEstimatedPeopleFed() { return estimatedPeopleFed; }
    public void setEstimatedPeopleFed(Integer estimatedPeopleFed) { this.estimatedPeopleFed = estimatedPeopleFed; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
