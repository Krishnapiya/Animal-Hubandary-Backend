package com.keltron.dogbreeder.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.keltron.dogbreeder.dto.DogBreederApplicationStatusHistoryDto;
import com.keltron.dogbreeder.services.impl.DogBreederApplicationStatusHistoryServiceImpl;

@RestController
@RequestMapping("/dogbreeder/auth/application-status-history")
public class DogBreederApplicationStatusHistoryController {

    @Autowired
    private DogBreederApplicationStatusHistoryServiceImpl historyService;

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<DogBreederApplicationStatusHistoryDto>> getHistory(
            @PathVariable("applicationId") Long applicationId) {

        List<DogBreederApplicationStatusHistoryDto> history = 
                historyService.getHistoryByApplicationId(applicationId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/application/{applicationId}/timeline")
    public ResponseEntity<List<DogBreederApplicationStatusHistoryDto>> getTimeline(
            @PathVariable("applicationId") Long applicationId) {

        List<DogBreederApplicationStatusHistoryDto> timeline = 
                historyService.getHistoryByApplicationIdChronological(applicationId);
        return ResponseEntity.ok(timeline);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Dog Breeder Status History Controller Working");
    }
}