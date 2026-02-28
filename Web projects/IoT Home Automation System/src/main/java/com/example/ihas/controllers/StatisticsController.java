package com.example.ihas.controllers;

import com.example.ihas.services.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService _statisticsService) {
        statisticsService = _statisticsService;
    }

    @GetMapping("/api/statistics")
    public ResponseEntity<?> getStatistics(Authentication auth) {
        // Check if authentication is null
        if (auth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Authentication required to access statistics");
        }

        return ResponseEntity.ok(statisticsService.getStatistics(auth.getName()));
    }
}
