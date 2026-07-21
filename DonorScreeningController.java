package com.bbms.backend.controller;

import com.bbms.backend.Repository.DonorRepository;
import com.bbms.backend.Repository.DonorScreeningRepository;
import com.bbms.backend.entity.Donor;
import com.bbms.backend.entity.DonorScreening;
import com.bbms.backend.entity.DonorStatus;
import com.bbms.backend.entity.ScreeningStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/screenings")
@CrossOrigin(origins = "*")
public class DonorScreeningController {

    private final DonorScreeningRepository screeningRepository;
    private final DonorRepository donorRepository;

    public DonorScreeningController(DonorScreeningRepository screeningRepository,
                                    DonorRepository donorRepository) {
        this.screeningRepository = screeningRepository;
        this.donorRepository = donorRepository;
    }

    // ✅ CREATE
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @PostMapping("/{donorId}")
    public ResponseEntity<?> addScreening(@PathVariable Long donorId,
                                          @RequestBody DonorScreening screening) {

        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        if (donor.getStatus() != DonorStatus.ACTIVE) {
            return ResponseEntity.badRequest().body("Only ACTIVE donors can be screened.");
        }

        if (donor.getNextEligibleDate() != null &&
                LocalDate.now().isBefore(donor.getNextEligibleDate())) {
            return ResponseEntity.badRequest()
                    .body("Donor is not eligible for screening until " + donor.getNextEligibleDate());
        }

        if (screening.getWeight() == null || screening.getHemoglobin() == null) {
            return ResponseEntity.badRequest().body("Weight and hemoglobin are required.");
        }

        if (screening.getScreeningDate() == null) {
            screening.setScreeningDate(LocalDate.now());
        }

        screening.setDonor(donor);

        if (screening.getWeight() < 50 || screening.getHemoglobin() < 12.5) {
            screening.setEligibilityStatus(ScreeningStatus.TEMPORARILY_DEFERRED);
        } else {
            screening.setEligibilityStatus(ScreeningStatus.ELIGIBLE);
        }

        DonorScreening saved = screeningRepository.save(screening);
        return ResponseEntity.ok(saved);
    }

    // 🔥 ✅ ADD THIS (UPDATE METHOD)
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateScreening(@PathVariable Long id,
                                             @RequestBody DonorScreening updated) {

        DonorScreening screening = screeningRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Screening not found"));

        screening.setHemoglobin(updated.getHemoglobin());
        screening.setWeight(updated.getWeight());
        screening.setBloodPressure(updated.getBloodPressure());
        screening.setTemperature(updated.getTemperature());
        screening.setPulseRate(updated.getPulseRate());
        screening.setMedicalHistory(updated.getMedicalHistory());
        screening.setRemarks(updated.getRemarks());

        if (screening.getWeight() < 50 || screening.getHemoglobin() < 12.5 ||  screening.getTemperature() >= 37.5 ||screening.getPulseRate()>100) {
            screening.setEligibilityStatus(ScreeningStatus.TEMPORARILY_DEFERRED);
        } else {
            screening.setEligibilityStatus(ScreeningStatus.ELIGIBLE);
        }

        return ResponseEntity.ok(screeningRepository.save(screening));
    }
    // 🔥 ✅ ADD DELETE HERE
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScreening(@PathVariable Long id) {
        screeningRepository.deleteById(id);
        return ResponseEntity.ok("Deleted successfully");
    }
    // ✅ GET ALL
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @GetMapping
    public List<DonorScreening> getAllScreenings() {
        return screeningRepository.findAll();
    }

    // ✅ GET BY ID
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITAL_STAFF')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getScreeningById(@PathVariable Long id) {
        return screeningRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Screening not found"));
    }

    // ✅ GET BY DONOR
    @PreAuthorize("hasAnyRole('ADMIN','HOSPITTAL_STAFF')")
    @GetMapping("/donor/{donorId}")
    public ResponseEntity<?> getScreeningsByDonor(@PathVariable Long donorId) {
        Donor donor = donorRepository.findById(donorId)
                .orElseThrow(() -> new RuntimeException("Donor not found"));

        return ResponseEntity.ok(screeningRepository.findByDonor(donor));
    }
}