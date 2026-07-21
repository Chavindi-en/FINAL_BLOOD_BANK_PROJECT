package com.bbms.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donationId;

   // add at top

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "donor_id", nullable = false)
    @JsonIgnore
    private Donor donor;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "screening_id", unique = true)
    @JsonIgnore
    private DonorScreening screening;


    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate donationDate;

    @Column(nullable = false)
    private Double unitsCollected;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus donationStatus = DonationStatus.COMPLETED;

    private String remarks;

    public Donation() {}

    @PrePersist
    protected void onCreate() {
        this.donationDate = LocalDate.now();
    }

    // getters & setters
    // GETTERS & SETTERS

    public Long getDonationId() {
        return donationId;
    }

    public void setDonationId(Long donationId) {
        this.donationId = donationId;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public DonorScreening getScreening() {
        return screening;
    }
    public Long getDonorId() {
        return donor != null ? donor.getDonorId(): null;
    }

    public Long getScreeningId() {
        return screening != null ? screening.getScreeningId() : null;
    }


    public void setScreening(DonorScreening screening) {
        this.screening = screening;
    }

    public LocalDate getDonationDate() {
        return donationDate;
    }

    public void setDonationDate(LocalDate donationDate) {
        this.donationDate = donationDate;
    }

    public Double getUnitsCollected() {
        return unitsCollected;
    }

    public void setUnitsCollected(Double unitsCollected) {
        this.unitsCollected = unitsCollected;
    }

    public DonationStatus getDonationStatus() {
        return donationStatus;
    }

    public void setDonationStatus(DonationStatus donationStatus) {
        this.donationStatus = donationStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
