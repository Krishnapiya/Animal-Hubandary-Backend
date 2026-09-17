package com.keltron.citizen.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.keltron.citizen.dto.ComplaintRegistrationStatusHistoryDto;
import com.keltron.citizen.services.impl.ComplaintRegistrationStatusHistoryServiceImpl;

@RestController
@RequestMapping("/citizen/auth/complaint-registration-status-history")
public class ComplaintRegistrationStatusHistoryController {

    @Autowired
    private ComplaintRegistrationStatusHistoryServiceImpl historyService;

    /**
     * Get Status History (Latest First)
     */
    @GetMapping("/complaint/{complaintId}")
    public List<ComplaintRegistrationStatusHistoryDto> getHistory(
            @PathVariable Long complaintId) {

        return historyService.getHistoryByComplaintId(complaintId);
    }

    /**
     * Test Controller
     */
    @GetMapping("/test")
    public String test() {
        return "Controller Working";
    }

    /**
     * Get Status History (Oldest First)
     */
    @GetMapping("/complaint/{complaintId}/timeline")
    public List<ComplaintRegistrationStatusHistoryDto> getTimeline(
            @PathVariable Long complaintId) {

        return historyService
                .getHistoryByComplaintIdChronological(complaintId);
    }
}