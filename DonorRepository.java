package com.bbms.backend.Repository;

import com.bbms.backend.entity.Donor;
import com.bbms.backend.entity.DonorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonorRepository extends JpaRepository<Donor, Long> {

    // 🔍 Find donor by NIC (safe - Optional)
    Optional<Donor> findByNic(String nic);

    // 🔍 Find donor by Email (safe - Optional)
    Optional<Donor> findByEmail(String email);

    // 📊 Get donors by status (PENDING / ACTIVE / REJECTED)
    List<Donor> findByStatus(DonorStatus status);

    // 📊 Count donors by status
    long countByStatus(DonorStatus status);
}