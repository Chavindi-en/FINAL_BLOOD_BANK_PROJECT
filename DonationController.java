package com.bbms.backend.controller;

import com.bbms.backend.Repository.*;
import com.bbms.backend.entity.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    private final DonationRepository donationRepository;
    private final DonorRepository donorRepository;
    private final DonorScreeningRepository screeningRepository;

    public DonationController(DonationRepository donationRepository,
                              DonorRepository donorRepository,
                              DonorScreeningRepository screeningRepository) {
        this.donationRepository = donationRepository;
        this.donorRepository = donorRepository;
        this.screeningRepository = screeningRepository;
    }
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @PostMapping("/{donorId}/{screeningId}")
    public ResponseEntity<?> createDonation(@PathVariable Long donorId,
                                            @PathVariable Long screeningId,
                                            @RequestBody Donation donation) {

        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        // 🔴 Check donor status
        if (donor.getStatus() != DonorStatus.ACTIVE) {
            return ResponseEntity.badRequest().body("Donor is not ACTIVE");
        }

        DonorScreening screening = screeningRepository.findById(screeningId)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        if (screening.getEligibilityStatus() != ScreeningStatus.ELIGIBLE) {
            return ResponseEntity.badRequest().body("Donor not eligible");
        }

        if (screening.getDonor() == null ||
                !screening.getDonor().getDonorId().equals(donorId)) {
            return ResponseEntity.badRequest().body("Mismatch donor & screening");
        }

        if (donationRepository.existsByScreening(screening)) {
            return ResponseEntity.badRequest().body("Already donated for this screening");
        }

        donation.setDonor(donor);
        donation.setScreening(screening);
        donation.setDonationDate(LocalDate.now());
        donation.setDonationStatus(DonationStatus.COMPLETED);

        return ResponseEntity.ok(donationRepository.save(donation));
    }
    // 🔥 UPDATE DONATION
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDonation(@PathVariable Long id,
                                            @RequestBody Donation updated) {

        Donation donation = donationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        // ✅ Update only allowed fields
        donation.setUnitsCollected(updated.getUnitsCollected());
        donation.setRemarks(updated.getRemarks());
        donation.setDonationStatus(updated.getDonationStatus());

        return ResponseEntity.ok(donationRepository.save(donation));
    }

    // ✅ GET ALL
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @GetMapping
    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    // ✅ GET BY ID
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationById(@PathVariable Long id) {
        return donationRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Donation not found"));
    }

    // 🔥 DELETE
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonation(@PathVariable Long id) {
        donationRepository.deleteById(id);
        return ResponseEntity.ok("Donation deleted successfully");
    }

    // 🔥 GET ONLY COMPLETED DONATIONS (FOR BLOOD TEST PAGE)
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF','LAB_STAFF')")
    @GetMapping("/completed")
    public ResponseEntity<?> getCompletedDonations() {
        try {
            return ResponseEntity.ok(
                    donationRepository.findAll().stream()
                            .filter(d -> d.getDonationStatus() == DonationStatus.COMPLETED)
                            .map(d -> {
                                return java.util.Map.of(
                                        "donationId", d.getDonationId(),
                                        "donorId", d.getDonorId(),
                                        "screeningId", d.getScreeningId()

                                );
                            })
                            .toList()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }


}