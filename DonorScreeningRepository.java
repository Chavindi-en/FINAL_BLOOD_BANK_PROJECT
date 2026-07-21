package com.bbms.backend.Repository;

import com.bbms.backend.entity.Donor;
import com.bbms.backend.entity.DonorScreening;
import com.bbms.backend.entity.ScreeningStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonorScreeningRepository extends JpaRepository<DonorScreening, Long> {

    List<DonorScreening> findByDonor(Donor donor);

    List<DonorScreening> findByEligibilityStatus(ScreeningStatus eligibilityStatus);
}