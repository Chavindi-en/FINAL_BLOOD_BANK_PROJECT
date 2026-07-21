package com.bbms.backend.service;

import com.bbms.backend.Repository.DonorRepository;
import com.bbms.backend.entity.Donor;
import com.bbms.backend.entity.DonorStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;
import java.util.List;

@Service
public class DonorService {

    private final DonorRepository repo;

    public DonorService(DonorRepository repo) {
        this.repo = repo;
    }

    public Donor register(Donor donor) {

        // 🔴 VALIDATIONS
        if (donor.getFullName() == null || donor.getFullName().trim().isEmpty()) {
            throw new RuntimeException("Full name is required.");
        }

        if (donor.getNic() == null || donor.getNic().trim().isEmpty()) {
            throw new RuntimeException("NIC is required.");
        }

        if (donor.getDob() == null) {
            throw new RuntimeException("Date of birth is required.");
        }

        if (donor.getPhone() == null || donor.getPhone().trim().isEmpty()) {
            throw new RuntimeException("Phone number is required.");
        }

        if (donor.getGender() == null) {
            throw new RuntimeException("Gender is required.");
        }

        // 🔥 OPTIONAL BLOOD GROUP (NO VALIDATION HERE)

        // 🔴 DUPLICATE CHECK
        if (repo.findByNic(donor.getNic()).isPresent()) {
            throw new RuntimeException("This NIC is already registered.");
        }

        if (donor.getEmail() != null && !donor.getEmail().isEmpty()) {
            if (repo.findByEmail(donor.getEmail()).isPresent()) {
                throw new RuntimeException("Email already exists.");
            }
        }

        // 🔴 AGE VALIDATION
        int age = Period.between(donor.getDob(), LocalDate.now()).getYears();
        if (age < 18) {
            throw new RuntimeException("Must be 18+ to register.");
        }

        // 🔴 PHONE VALIDATION
        Pattern phonePattern = Pattern.compile("^(0\\d{9})$");
        if (!phonePattern.matcher(donor.getPhone()).matches()) {
            throw new RuntimeException("Invalid phone number.");
        }

        // DEFAULT VALUES
        donor.setStatus(DonorStatus.PENDING_VERIFICATION);
        donor.setApprovedBy(null);
        donor.setApprovedAt(null);
        donor.setRejectionReason(null);
        donor.setLastDonationDate(null);
        donor.setNextEligibleDate(null);

        return repo.save(donor);
    }

    public String checkStatus(String nic) {

        return repo.findByNic(nic)
                .map(d -> d.getStatus().name())
                .orElse("NOT_REGISTERED");
    }

    public List<Donor> getAllDonors() {
        return repo.findAll();
    }
}
