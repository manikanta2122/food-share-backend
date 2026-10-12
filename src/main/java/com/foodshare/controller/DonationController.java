package com.foodshare.controller;

import com.foodshare.dto.ClaimRequestDTO;
import com.foodshare.dto.DonationRequestDTO;
import com.foodshare.dto.StatusUpdateDTO;
import com.foodshare.model.DonationClaim;
import com.foodshare.model.FoodDonation;
import com.foodshare.model.FoodStatus;
import com.foodshare.model.FoodType;
import com.foodshare.service.DonationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    @Autowired
    private DonationService donationService;

    @GetMapping
    public ResponseEntity<List<FoodDonation>> getDonations(
            @RequestParam(required = false) FoodStatus status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) FoodType foodType) {
        return ResponseEntity.ok(donationService.filterDonations(status, city, foodType));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationById(@PathVariable Long id) {
        return donationService.getDonationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<FoodDonation>> getDonationsByDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(donationService.getDonationsByDonor(donorId));
    }

    @GetMapping("/claimant/{claimantId}")
    public ResponseEntity<List<FoodDonation>> getDonationsByClaimant(@PathVariable Long claimantId) {
        return ResponseEntity.ok(donationService.getDonationsByClaimant(claimantId));
    }

    @PostMapping
    public ResponseEntity<?> createDonation(@Valid @RequestBody DonationRequestDTO dto, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createError("Authentication required"));
        }
        try {
            FoodDonation created = donationService.createDonation(dto, authentication.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(createError(ex.getMessage()));
        }
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<?> claimDonation(@PathVariable Long id, @Valid @RequestBody ClaimRequestDTO dto, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createError("Authentication required"));
        }
        try {
            FoodDonation claimed = donationService.claimDonation(id, dto, authentication.getName());
            return ResponseEntity.ok(claimed);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(createError(ex.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateDTO dto, Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(createError("Authentication required"));
        }
        try {
            FoodDonation updated = donationService.updateStatus(id, dto, authentication.getName());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(createError(ex.getMessage()));
        }
    }

    @GetMapping("/{id}/claims")
    public ResponseEntity<List<DonationClaim>> getClaims(@PathVariable Long id) {
        return ResponseEntity.ok(donationService.getClaimsForDonation(id));
    }

    private Map<String, String> createError(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        return error;
    }
}
