package com.foodshare.service;

import com.foodshare.dto.ClaimRequestDTO;
import com.foodshare.dto.DonationRequestDTO;
import com.foodshare.dto.StatusUpdateDTO;
import com.foodshare.model.*;
import com.foodshare.repository.DonationClaimRepository;
import com.foodshare.repository.FoodDonationRepository;
import com.foodshare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DonationService {

    @Autowired
    private FoodDonationRepository donationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonationClaimRepository claimRepository;

    public List<FoodDonation> getAllDonations() {
        return donationRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<FoodDonation> filterDonations(FoodStatus status, String city, FoodType foodType) {
        return donationRepository.searchDonations(status, city, foodType);
    }

    public Optional<FoodDonation> getDonationById(Long id) {
        return donationRepository.findById(id);
    }

    public List<FoodDonation> getDonationsByDonor(Long donorId) {
        return donationRepository.findByDonorIdOrderByCreatedAtDesc(donorId);
    }

    public List<FoodDonation> getDonationsByClaimant(Long claimantId) {
        return donationRepository.findByClaimedByIdOrderByCreatedAtDesc(claimantId);
    }

    @Transactional
    public FoodDonation createDonation(DonationRequestDTO dto, String authenticatedEmail) {
        User authenticatedUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        if (authenticatedUser.getRole() != Role.DONOR && authenticatedUser.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Only registered food providers can publish food donations");
        }

        FoodDonation donation = new FoodDonation();
        donation.setTitle(dto.getTitle());
        donation.setDescription(dto.getDescription());
        donation.setFoodType(dto.getFoodType());
        donation.setQuantity(dto.getQuantity());
        donation.setUnit(dto.getUnit());
        donation.setCookedTime(dto.getCookedTime());
        donation.setExpiryTime(dto.getExpiryTime());
        donation.setAddress(dto.getAddress());
        donation.setCity(dto.getCity());
        donation.setContactPhone(dto.getContactPhone() != null ? dto.getContactPhone() : authenticatedUser.getPhone());
        donation.setSpecialInstructions(dto.getSpecialInstructions());
        donation.setStatus(FoodStatus.AVAILABLE);
        donation.setDonor(authenticatedUser); // Enforce authenticated user as donor

        return donationRepository.save(donation);
    }

    @Transactional
    public FoodDonation claimDonation(Long donationId, ClaimRequestDTO claimDTO, String authenticatedEmail) {
        FoodDonation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found with ID: " + donationId));

        if (donation.getStatus() != FoodStatus.AVAILABLE) {
            throw new IllegalStateException("Food donation is no longer available (current status: " + donation.getStatus() + ")");
        }

        User claimant = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Claimant not found"));

        if (claimant.getRole() != Role.NGO && claimant.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Only verified NGOs / eligible recipients can claim food donations");
        }

        donation.setClaimedBy(claimant);
        donation.setStatus(FoodStatus.CLAIMED);
        donation.setClaimedAt(LocalDateTime.now());

        // Save detailed claim record
        DonationClaim claim = new DonationClaim();
        claim.setDonation(donation);
        claim.setClaimant(claimant);
        claim.setNote(claimDTO.getNote());
        claim.setEstimatedPeopleFed(claimDTO.getEstimatedPeopleFed() != null ? claimDTO.getEstimatedPeopleFed() : donation.getQuantity());
        claim.setContactPerson(claimDTO.getContactPerson() != null ? claimDTO.getContactPerson() : claimant.getFullName());
        claim.setContactPhone(claimDTO.getContactPhone() != null ? claimDTO.getContactPhone() : claimant.getPhone());
        claimRepository.save(claim);

        return donationRepository.save(donation);
    }

    @Transactional
    public FoodDonation updateStatus(Long donationId, StatusUpdateDTO dto, String authenticatedEmail) {
        FoodDonation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new IllegalArgumentException("Donation not found with ID: " + donationId));

        User currentUser = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Enforce ownership: Only the donor of this donation, the claimant, or an ADMIN can change its status
        boolean isOwnerDonor = donation.getDonor() != null && donation.getDonor().getId().equals(currentUser.getId());
        boolean isClaimant = donation.getClaimedBy() != null && donation.getClaimedBy().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwnerDonor && !isClaimant && !isAdmin) {
            throw new IllegalArgumentException("Unauthorized: You can only update status for donations you own or claimed");
        }

        donation.setStatus(dto.getStatus());

        if (dto.getStatus() == FoodStatus.DELIVERED) {
            donation.setCompletedAt(LocalDateTime.now());
        }

        return donationRepository.save(donation);
    }

    public List<DonationClaim> getClaimsForDonation(Long donationId) {
        return claimRepository.findByDonationId(donationId);
    }

    // Role-scoped dashboard metrics
    public Map<String, Object> getScopedDashboardStats(String authenticatedEmail) {
        Map<String, Object> stats = new HashMap<>();

        if (authenticatedEmail == null) {
            // Public aggregate metrics for homepage
            return getPublicDashboardStats();
        }

        User user = userRepository.findByEmail(authenticatedEmail).orElse(null);
        if (user == null) {
            return getPublicDashboardStats();
        }

        if (user.getRole() == Role.DONOR) {
            // Provider Dashboard: strictly metrics for this authenticated provider
            long myDonations = donationRepository.countByDonorId(user.getId());
            long myAvailable = donationRepository.countByDonorIdAndStatus(user.getId(), FoodStatus.AVAILABLE);
            long myClaimed = donationRepository.countByDonorIdAndStatus(user.getId(), FoodStatus.CLAIMED);
            long myCompleted = donationRepository.countByDonorIdAndStatus(user.getId(), FoodStatus.DELIVERED);
            long myMealsRescued = donationRepository.calculateDonorRescuedMeals(user.getId());

            stats.put("scope", "PROVIDER");
            stats.put("totalDonations", myDonations);
            stats.put("availableDonations", myAvailable);
            stats.put("activeClaims", myClaimed);
            stats.put("completedDonations", myCompleted);
            stats.put("totalMealsRescued", myMealsRescued);
            stats.put("totalCommunityMembers", userRepository.count());
            return stats;
        } else if (user.getRole() == Role.NGO) {
            // Recipient Dashboard: strictly metrics for this authenticated NGO
            long myClaims = donationRepository.countByClaimedById(user.getId());
            long myPendingClaims = donationRepository.countByClaimedByIdAndStatus(user.getId(), FoodStatus.CLAIMED);
            long myDelivered = donationRepository.countByClaimedByIdAndStatus(user.getId(), FoodStatus.DELIVERED);
            long myReceivedMeals = donationRepository.calculateRecipientReceivedMeals(user.getId());
            long totalAvailable = donationRepository.countByStatus(FoodStatus.AVAILABLE);

            stats.put("scope", "RECIPIENT");
            stats.put("totalDonations", myClaims);
            stats.put("availableDonations", totalAvailable);
            stats.put("activeClaims", myPendingClaims);
            stats.put("completedDonations", myDelivered);
            stats.put("totalMealsRescued", myReceivedMeals);
            stats.put("totalCommunityMembers", userRepository.count());
            return stats;
        } else {
            // Administrator Dashboard
            return getPublicDashboardStats();
        }
    }

    public Map<String, Object> getPublicDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        long totalDonations = donationRepository.count();
        long available = donationRepository.countByStatus(FoodStatus.AVAILABLE);
        long claimed = donationRepository.countByStatus(FoodStatus.CLAIMED);
        long delivered = donationRepository.countByStatus(FoodStatus.DELIVERED);
        long totalMealsRescued = donationRepository.calculateTotalMealsRescued();
        long totalUsers = userRepository.count();

        stats.put("scope", "PUBLIC");
        stats.put("totalDonations", totalDonations);
        stats.put("availableDonations", available);
        stats.put("activeClaims", claimed);
        stats.put("completedDonations", delivered);
        stats.put("totalMealsRescued", totalMealsRescued);
        stats.put("totalCommunityMembers", totalUsers);

        return stats;
    }
}
