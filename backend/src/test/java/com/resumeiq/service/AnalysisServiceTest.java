package com.resumeiq.service;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.resumeiq.model.Candidate;
import com.resumeiq.model.JobApplication;
import com.resumeiq.model.JobRole;

class AnalysisServiceTest {
    private final AnalysisService analysisService = new AnalysisService();

    @Test
    void analyzeCandidateExtractsContactInfoSkillsAndEducation() {
        String resumeText = "Jane Doe\njane.doe@example.com\n+1 555 123 4567\nExperience: 5 years\nMaster of Science\nSkills: Java, Spring Boot, AWS";

        Candidate candidate = analysisService.analyzeCandidate(resumeText, "resume.docx");

        assertEquals("Jane Doe", candidate.getName());
        assertEquals("jane.doe@example.com", candidate.getEmail());
        assertEquals("+1 555 123 4567", candidate.getPhone());
        assertTrue(candidate.getSkills().contains("java"));
        assertTrue(candidate.getSkills().contains("spring boot"));
        assertEquals("Masters", candidate.getHighestEducation());
        assertEquals(5, candidate.getExperienceYears());
        assertTrue(candidate.getAtsScore() >= 0);
        assertTrue(candidate.getEmployabilityScore() >= 0);
    }

    @Test
    void analyzeApplicationMatchesRequiredSkillsAndSuggestsRole() {
        Candidate candidate = new Candidate();
        candidate.setSkills(Set.of("Java", "Spring Boot", "Docker"));
        candidate.setExperienceYears(4);
        candidate.setHighestEducation("Masters");
        candidate.setAtsScore(60);

        JobRole role = new JobRole();
        role.setTitle("Senior Developer");
        role.setDepartment("Engineering");
        role.setMinExperienceYears(3);
        role.setRequiredEducation("Masters");
        role.setRequiredSkills(Set.of("Java", "Spring Boot"));
        role.setPreferredSkills(Set.of("Docker", "Kubernetes"));

        JobApplication application = analysisService.analyzeApplication(candidate, role, List.of(role));

        assertSame(candidate, application.getCandidate());
        assertSame(role, application.getJobRole());
        assertTrue(application.getMatchedSkills().contains("java"));
        assertTrue(application.getMatchedSkills().contains("spring boot"));
        assertTrue(application.getMissingSkills().isEmpty());
        assertTrue(application.getMatchScore() >= 0 && application.getMatchScore() <= 100);
        assertEquals("Senior Developer", application.getSuggestedRole());
        assertNotNull(application.getRecommendations());
        assertFalse(application.getRecommendations().isBlank());
    }

    @Test
    void keywordRecommendationsIncludesRoleMetadata() {
        JobRole role = new JobRole();
        role.setTitle("Data Analyst");
        role.setDepartment("Analytics");
        role.setRequiredSkills(Set.of("Excel"));
        role.setPreferredSkills(Set.of("Python"));

        Set<String> keywords = analysisService.keywordRecommendations(role);

        assertTrue(keywords.contains("Data Analyst"));
        assertTrue(keywords.contains("Analytics"));
        assertTrue(keywords.contains("Excel"));
        assertTrue(keywords.contains("Python"));
    }
}
