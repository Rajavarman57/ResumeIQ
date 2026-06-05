package com.resumeiq.controller;

import com.resumeiq.model.JobRole;
import com.resumeiq.repository.JobRoleRepository;
import com.resumeiq.service.AnalysisService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/jobs")
public class JobRoleController {
    private final JobRoleRepository jobs;
    private final AnalysisService analysisService;

    public JobRoleController(JobRoleRepository jobs, AnalysisService analysisService) {
        this.jobs = jobs;
        this.analysisService = analysisService;
    }

    @GetMapping
    public List<JobRole> list() {
        return jobs.findAll();
    }

    @PostMapping
    public JobRole create(@RequestBody JobRole jobRole) {
        return jobs.save(jobRole);
    }

    @PutMapping("/{id}")
    public JobRole update(@PathVariable Long id, @RequestBody JobRole payload) {
        JobRole job = jobs.findById(id).orElseThrow();
        job.setTitle(payload.getTitle());
        job.setDescription(payload.getDescription());
        job.setDepartment(payload.getDepartment());
        job.setMinExperienceYears(payload.getMinExperienceYears());
        job.setRequiredEducation(payload.getRequiredEducation());
        job.setRequiredSkills(payload.getRequiredSkills());
        job.setPreferredSkills(payload.getPreferredSkills());
        job.setActive(payload.isActive());
        return jobs.save(job);
    }

    @GetMapping("/{id}/keywords")
    public Set<String> keywords(@PathVariable Long id) {
        return analysisService.keywordRecommendations(jobs.findById(id).orElseThrow());
    }
}
