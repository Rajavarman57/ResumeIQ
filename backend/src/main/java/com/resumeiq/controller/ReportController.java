package com.resumeiq.controller;

import com.resumeiq.model.JobApplication;
import com.resumeiq.repository.JobApplicationRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {
    private final JobApplicationRepository applications;

    public ReportController(JobApplicationRepository applications) {
        this.applications = applications;
    }

    @GetMapping("/hiring.csv")
    public ResponseEntity<String> hiringReport() {
        StringBuilder csv = new StringBuilder("Candidate,Email,Job Role,Match Score,Status,Suggested Role,Missing Skills\n");
        for (JobApplication app : applications.findAll()) {
            csv.append(escape(app.getCandidate().getName())).append(',')
                    .append(escape(app.getCandidate().getEmail())).append(',')
                    .append(escape(app.getJobRole().getTitle())).append(',')
                    .append(app.getMatchScore()).append(',')
                    .append(app.getStatus()).append(',')
                    .append(escape(app.getSuggestedRole())).append(',')
                    .append(escape(String.join("; ", app.getMissingSkills()))).append('\n');
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=resumeiq-hiring-report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.toString());
    }

    private String escape(String value) {
        String safe = value == null ? "" : value;
        return "\"" + safe.replace("\"", "\"\"") + "\"";
    }
}
