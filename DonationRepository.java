package com.bbms.backend.Repository;

import com.bbms.backend.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import com.bbms.backend.entity.DonorScreening;
import java.util.List;
import com.bbms.backend.entity.DonationStatus;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    boolean existsByScreening(DonorScreening screening);
    List<Donation> findByDonationStatus(DonationStatus donationStatus);

}
