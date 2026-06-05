package com.resumeiq.controller;

import com.resumeiq.model.ApplicationStatus;
import com.resumeiq.model.JobApplication;
import com.resumeiq.repository.CandidateRepository;
import com.resumeiq.repository.JobApplicationRepository;
import com.resumeiq.repository.JobRoleRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {
    private final JobRoleRepository jobs;
    private final CandidateRepository candidates;
    private final JobApplicationRepository applications;

    public AnalyticsController(JobRoleRepository jobs, CandidateRepository candidates, JobApplicationRepository applications) {
        this.jobs = jobs;
        this.candidates = candidates;
        this.applications = applications;
    }

    @GetMapping
    public Map<String, Object> dashboard() {
        var all = applications.findAll();
        Map<ApplicationStatus, Long> byStatus = new EnumMap<>(ApplicationStatus.class);
        for (ApplicationStatus status : ApplicationStatus.values()) {
            byStatus.put(status, applications.findByStatus(status).stream().count());
        }
        double avgScore = all.stream().mapToInt(JobApplication::getMatchScore).average().orElse(0);
        return Map.of(
                "jobs", jobs.count(),
                "candidates", candidates.count(),
                "applications", applications.count(),
                "averageMatchScore", Math.round(avgScore),
                "statusCounts", byStatus,
                "topSkills", candidates.findAll().stream()
                        .flatMap(candidate -> candidate.getSkills().stream())
                        .collect(Collectors.groupingBy(skill -> skill, Collectors.counting()))
        );
    }
}
