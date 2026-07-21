package com.bbms.backend.controller;

import com.bbms.backend.entity.Donor;
import com.bbms.backend.service.DonorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donor-management")  // 🔥 updated URL
@CrossOrigin(origins = "*")
public class DonorManagementController {

    private final DonorService service;

    public DonorManagementController(DonorService service) {
        this.service = service;
    }

    // ✅ GET ALL DONORS
    @GetMapping
    public List<Donor> getAllDonors() {
        return service.getAllDonors();
    }
}