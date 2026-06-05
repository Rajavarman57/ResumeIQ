package com.resumeiq.controller;

import com.resumeiq.dto.StatusRequest;
import com.resumeiq.model.JobApplication;
import com.resumeiq.service.ApplicationService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public List<JobApplication> list(@RequestParam(required = false) Long jobRoleId,
                                     @RequestParam(required = false) String sort,
                                     @RequestParam(required = false) String skills) {
        return applicationService.list(jobRoleId, sort, skills);
    }

    @PostMapping("/upload")
    public JobApplication upload(@RequestParam Long jobRoleId, @RequestParam MultipartFile file) throws IOException {
        return applicationService.upload(jobRoleId, file);
    }

    @PatchMapping("/{id}/status")
    public JobApplication updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        return applicationService.updateStatus(id, request.status());
    }

    @PostMapping("/{id}/reanalyze")
    public JobApplication reanalyze(@PathVariable Long id) {
        return applicationService.reanalyze(id);
    }

    @GetMapping("/suggested/{jobRoleId}")
    public List<JobApplication> suggested(@PathVariable Long jobRoleId) {
        return applicationService.suggestedApplicants(jobRoleId);
    }
}
