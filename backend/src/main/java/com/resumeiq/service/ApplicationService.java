package com.resumeiq.service;

import com.resumeiq.model.*;
import com.resumeiq.repository.CandidateRepository;
import com.resumeiq.repository.JobApplicationRepository;
import com.resumeiq.repository.JobRoleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {
    private final ResumeParserService parser;
    private final AnalysisService analysis;
    private final CandidateRepository candidates;
    private final JobRoleRepository jobRoles;
    private final JobApplicationRepository applications;
    private final Path uploadDir;

    public ApplicationService(ResumeParserService parser, AnalysisService analysis, CandidateRepository candidates,
                              JobRoleRepository jobRoles, JobApplicationRepository applications,
                              @Value("${resumeiq.upload-dir}") String uploadDir) {
        this.parser = parser;
        this.analysis = analysis;
        this.candidates = candidates;
        this.jobRoles = jobRoles;
        this.applications = applications;
        this.uploadDir = Path.of(uploadDir);
    }

    public JobApplication upload(Long jobRoleId, MultipartFile file) throws IOException {
        JobRole role = jobRoles.findById(jobRoleId).orElseThrow();
        Files.createDirectories(uploadDir);
        String original = file.getOriginalFilename() == null ? "resume" : file.getOriginalFilename();
        Path destination = uploadDir.resolve(UUID.randomUUID() + "-" + original);
        String resumeText = parser.parse(file);
        file.transferTo(destination);

        Candidate candidate = analysis.analyzeCandidate(resumeText, original);
        candidate.setResumeFileName(original);
        candidate.setResumePath(destination.toString());
        Candidate saved = candidates.save(candidate);

        JobApplication application = analysis.analyzeApplication(saved, role, jobRoles.findByActiveTrue());
        return applications.save(application);
    }

    public List<JobApplication> list(Long jobRoleId, String sort, String skills) {
        List<JobApplication> list = jobRoleId == null ? applications.findAll() : applications.findByJobRoleId(jobRoleId);
        if (skills != null && !skills.isBlank()) {
            List<String> required = List.of(skills.toLowerCase().split(","));
            list = list.stream().filter(app -> required.stream().allMatch(skill ->
                    app.getCandidate().getSkills().stream().anyMatch(candidateSkill -> candidateSkill.equalsIgnoreCase(skill.trim())))).toList();
        }
        Comparator<JobApplication> comparator = switch (sort == null ? "best" : sort) {
            case "least" -> Comparator.comparing(JobApplication::getMatchScore);
            case "experience" -> Comparator.comparing(app -> app.getCandidate().getExperienceYears(), Comparator.reverseOrder());
            case "education" -> Comparator.comparing(app -> app.getCandidate().getHighestEducation(), Comparator.nullsLast(String::compareToIgnoreCase));
            default -> Comparator.comparing(JobApplication::getMatchScore, Comparator.reverseOrder());
        };
        return list.stream().sorted(comparator).toList();
    }

    public JobApplication updateStatus(Long id, ApplicationStatus status) {
        JobApplication application = applications.findById(id).orElseThrow();
        application.setStatus(status);
        application.setUpdatedAt(LocalDateTime.now());
        return applications.save(application);
    }

    public List<JobApplication> suggestedApplicants(Long jobRoleId) {
        JobRole target = jobRoles.findById(jobRoleId).orElseThrow();
        return candidates.findAll().stream()
                .filter(candidate -> !applications.existsByCandidateIdAndJobRoleId(candidate.getId(), jobRoleId))
                .map(candidate -> analysis.analyzeApplication(candidate, target, jobRoles.findByActiveTrue()))
                .filter(application -> application.getMatchScore() >= 50)
                .sorted(Comparator.comparing(JobApplication::getMatchScore, Comparator.reverseOrder()))
                .toList();
    }

    public JobApplication reanalyze(Long id) {
        JobApplication current = applications.findById(id).orElseThrow();
        JobApplication refreshed = analysis.analyzeApplication(current.getCandidate(), current.getJobRole(), jobRoles.findByActiveTrue());
        current.setMatchScore(refreshed.getMatchScore());
        current.setMatchedSkills(refreshed.getMatchedSkills());
        current.setMissingSkills(refreshed.getMissingSkills());
        current.setSuggestedRole(refreshed.getSuggestedRole());
        current.setRecommendations(refreshed.getRecommendations());
        current.setUpdatedAt(LocalDateTime.now());
        return applications.save(current);
    }
}
