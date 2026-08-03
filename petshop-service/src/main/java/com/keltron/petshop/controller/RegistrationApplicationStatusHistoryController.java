package com.keltron.petshop.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.keltron.petshop.dto.RegistrationApplicationStatusHistoryDto;
import com.keltron.petshop.services.impl.RegistrationApplicationStatusHistoryServiceImpl;

@RestController
@RequestMapping("/petshop/auth/registration-application-status-history")
public class RegistrationApplicationStatusHistoryController {
	

    @Autowired
    private RegistrationApplicationStatusHistoryServiceImpl historyService;

    /**
     * Get Status History (Latest First)
     */
    @GetMapping("/application/{applicationId}")
    public List<RegistrationApplicationStatusHistoryDto> getHistory(
            @PathVariable Long applicationId) {

        return historyService.getHistoryByApplicationId(applicationId);
    }
    @GetMapping("/test")
    public String test() {
        return "Controller Working";
    }

    /**
     * Get Status History (Oldest First)
     */
    @GetMapping("/application/{applicationId}/timeline")
    public List<RegistrationApplicationStatusHistoryDto> getTimeline(
            @PathVariable Long applicationId) {

        return historyService.getHistoryByApplicationIdChronological(applicationId);
    }
}