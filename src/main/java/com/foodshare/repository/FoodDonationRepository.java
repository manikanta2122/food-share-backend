package com.foodshare.repository;

import com.foodshare.model.FoodDonation;
import com.foodshare.model.FoodStatus;
import com.foodshare.model.FoodType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodDonationRepository extends JpaRepository<FoodDonation, Long> {

    List<FoodDonation> findByStatusOrderByCreatedAtDesc(FoodStatus status);

    List<FoodDonation> findAllByOrderByCreatedAtDesc();

    List<FoodDonation> findByDonorIdOrderByCreatedAtDesc(Long donorId);

    List<FoodDonation> findByClaimedByIdOrderByCreatedAtDesc(Long claimedById);

    @Query("SELECT d FROM FoodDonation d WHERE " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:city IS NULL OR LOWER(d.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:foodType IS NULL OR d.foodType = :foodType) " +
           "ORDER BY d.createdAt DESC")
    List<FoodDonation> searchDonations(
            @Param("status") FoodStatus status,
            @Param("city") String city,
            @Param("foodType") FoodType foodType
    );

    long countByStatus(FoodStatus status);

    long countByDonorId(Long donorId);

    long countByDonorIdAndStatus(Long donorId, FoodStatus status);

    long countByClaimedById(Long claimedById);

    long countByClaimedByIdAndStatus(Long claimedById, FoodStatus status);

    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM FoodDonation d WHERE d.status IN ('CLAIMED', 'PICKED_UP', 'DELIVERED')")
    long calculateTotalMealsRescued();

    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM FoodDonation d WHERE d.donor.id = :donorId AND d.status IN ('CLAIMED', 'PICKED_UP', 'DELIVERED')")
    long calculateDonorRescuedMeals(@Param("donorId") Long donorId);

    @Query("SELECT COALESCE(SUM(d.quantity), 0) FROM FoodDonation d WHERE d.claimedBy.id = :recipientId AND d.status IN ('CLAIMED', 'PICKED_UP', 'DELIVERED')")
    long calculateRecipientReceivedMeals(@Param("recipientId") Long recipientId);
}
