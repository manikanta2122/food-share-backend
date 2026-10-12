package com.foodshare.repository;

import com.foodshare.model.DonationClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonationClaimRepository extends JpaRepository<DonationClaim, Long> {
    List<DonationClaim> findByDonationId(Long donationId);
    List<DonationClaim> findByClaimantIdOrderByCreatedAtDesc(Long claimantId);
}
