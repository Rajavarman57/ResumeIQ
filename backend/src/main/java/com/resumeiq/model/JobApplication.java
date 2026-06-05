package com.resumeiq.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Candidate candidate;
    @ManyToOne(optional = false)
    private JobRole jobRole;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.UNDER_REVIEW;
    private Integer matchScore = 0;
    private String suggestedRole;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> matchedSkills = new LinkedHashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> missingSkills = new LinkedHashSet<>();
    @Column(length = 4000)
    private String recommendations;
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }
    public JobRole getJobRole() { return jobRole; }
    public void setJobRole(JobRole jobRole) { this.jobRole = jobRole; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public Integer getMatchScore() { return matchScore; }
    public void setMatchScore(Integer matchScore) { this.matchScore = matchScore; }
    public String getSuggestedRole() { return suggestedRole; }
    public void setSuggestedRole(String suggestedRole) { this.suggestedRole = suggestedRole; }
    public Set<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(Set<String> matchedSkills) { this.matchedSkills = matchedSkills; }
    public Set<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(Set<String> missingSkills) { this.missingSkills = missingSkills; }
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
