package com.resumeiq.service;

import com.resumeiq.model.Candidate;
import com.resumeiq.model.JobApplication;
import com.resumeiq.model.JobRole;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AnalysisService {
    private static final List<String> SKILL_CATALOG = List.of(
            "java", "spring boot", "microservices", "mysql", "postgresql", "mongodb", "react", "javascript",
            "typescript", "html", "css", "bootstrap", "node.js", "express", "python", "django", "flask",
            "machine learning", "nlp", "tensorflow", "pytorch", "aws", "azure", "docker", "kubernetes",
            "git", "rest api", "graphql", "data analysis", "excel", "power bi", "tableau", "testing",
            "selenium", "linux", "agile", "communication", "leadership", "problem solving"
    );
    private static final Pattern EMAIL = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE = Pattern.compile("(\\+?\\d[\\d\\s().-]{8,}\\d)");
    private static final Pattern EXPERIENCE = Pattern.compile("(\\d{1,2})\\+?\\s*(years|yrs|year)", Pattern.CASE_INSENSITIVE);

    public Candidate analyzeCandidate(String resumeText, String fallbackName) {
        Candidate candidate = new Candidate();
        candidate.setResumeText(resumeText);
        candidate.setName(extractName(resumeText, fallbackName));
        candidate.setEmail(findFirst(EMAIL, resumeText));
        candidate.setPhone(findFirst(PHONE, resumeText));
        candidate.setSkills(extractSkills(resumeText));
        candidate.setExperienceYears(extractExperience(resumeText));
        candidate.setHighestEducation(extractEducation(resumeText));
        candidate.setAtsScore(calculateAtsScore(resumeText, candidate.getSkills()));
        candidate.setEmployabilityScore(Math.min(100, candidate.getAtsScore() / 2 + candidate.getSkills().size() * 4 + candidate.getExperienceYears() * 3));
        return candidate;
    }

    public JobApplication analyzeApplication(Candidate candidate, JobRole role, Collection<JobRole> allRoles) {
        Set<String> required = normalize(role.getRequiredSkills());
        Set<String> preferred = normalize(role.getPreferredSkills());
        Set<String> candidateSkills = normalize(candidate.getSkills());
        Set<String> matched = required.stream().filter(candidateSkills::contains).collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> missing = required.stream().filter(skill -> !candidateSkills.contains(skill)).collect(Collectors.toCollection(LinkedHashSet::new));

        int requiredScore = required.isEmpty() ? 40 : (int) Math.round((matched.size() * 40.0) / required.size());
        long preferredHits = preferred.stream().filter(candidateSkills::contains).count();
        int preferredScore = preferred.isEmpty() ? 15 : (int) Math.round((preferredHits * 15.0) / preferred.size());
        int experienceScore = candidate.getExperienceYears() >= role.getMinExperienceYears() ? 20 :
                (int) Math.round((candidate.getExperienceYears() * 20.0) / Math.max(1, role.getMinExperienceYears()));
        int educationScore = educationMatches(candidate.getHighestEducation(), role.getRequiredEducation()) ? 10 : 4;
        int atsBonus = Math.min(15, candidate.getAtsScore() / 7);

        JobApplication application = new JobApplication();
        application.setCandidate(candidate);
        application.setJobRole(role);
        application.setMatchedSkills(matched);
        application.setMissingSkills(missing);
        application.setMatchScore(Math.min(100, requiredScore + preferredScore + experienceScore + educationScore + atsBonus));
        application.setSuggestedRole(suggestRole(candidate, allRoles));
        application.setRecommendations(recommend(candidate, role, missing));
        return application;
    }

    public Set<String> extractSkills(String text) {
        String lower = safe(text).toLowerCase();
        return SKILL_CATALOG.stream()
                .filter(skill -> lower.contains(skill))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<String> keywordRecommendations(JobRole role) {
        Set<String> keywords = new LinkedHashSet<>();
        keywords.addAll(role.getRequiredSkills());
        keywords.addAll(role.getPreferredSkills());
        keywords.add(role.getTitle());
        keywords.add(role.getDepartment());
        return keywords.stream().filter(Objects::nonNull).map(String::trim).filter(s -> !s.isBlank()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String extractName(String text, String fallback) {
        return Arrays.stream(safe(text).split("\\R"))
                .map(String::trim)
                .filter(line -> line.length() >= 3 && line.length() <= 80)
                .filter(line -> !line.contains("@"))
                .findFirst()
                .orElse(fallback.replaceAll("\\.(pdf|docx)$", ""));
    }

    private String findFirst(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(safe(text));
        if (!matcher.find()) return "";
        return matcher.groupCount() >= 1 ? matcher.group(1).trim() : matcher.group().trim();
    }

    private Integer extractExperience(String text) {
        Matcher matcher = EXPERIENCE.matcher(safe(text));
        int max = 0;
        while (matcher.find()) {
            max = Math.max(max, Integer.parseInt(matcher.group(1)));
        }
        return max;
    }

    private String extractEducation(String text) {
        String lower = safe(text).toLowerCase();
        if (lower.contains("phd") || lower.contains("doctorate")) return "PhD";
        if (lower.contains("master") || lower.contains("m.tech") || lower.contains("mba")) return "Masters";
        if (lower.contains("bachelor") || lower.contains("b.tech") || lower.contains("b.e") || lower.contains("degree")) return "Bachelors";
        if (lower.contains("diploma")) return "Diploma";
        return "Not specified";
    }

    private int calculateAtsScore(String text, Set<String> skills) {
        int score = 35;
        String lower = safe(text).toLowerCase();
        if (lower.contains("experience")) score += 10;
        if (lower.contains("education")) score += 10;
        if (lower.contains("projects")) score += 10;
        if (lower.contains("skills")) score += 10;
        if (EMAIL.matcher(text).find()) score += 8;
        if (PHONE.matcher(text).find()) score += 7;
        score += Math.min(10, skills.size());
        return Math.min(100, score);
    }

    private String suggestRole(Candidate candidate, Collection<JobRole> roles) {
        return roles.stream()
                .max(Comparator.comparingInt(role -> skillOverlap(candidate.getSkills(), role.getRequiredSkills()) + skillOverlap(candidate.getSkills(), role.getPreferredSkills())))
                .map(JobRole::getTitle)
                .orElse("General Talent Pool");
    }

    private int skillOverlap(Set<String> a, Set<String> b) {
        Set<String> normalizedA = normalize(a);
        return (int) normalize(b).stream().filter(normalizedA::contains).count();
    }

    private String recommend(Candidate candidate, JobRole role, Set<String> missing) {
        List<String> tips = new ArrayList<>();
        if (!missing.isEmpty()) tips.add("Build or highlight these missing skills: " + String.join(", ", missing) + ".");
        if (candidate.getExperienceYears() < role.getMinExperienceYears()) tips.add("Add measurable achievements that support the required experience level.");
        if (candidate.getAtsScore() < 75) tips.add("Improve ATS compatibility by adding clear section headings, contact details, quantified projects, and role keywords.");
        if (tips.isEmpty()) tips.add("Strong role fit. Prioritize for recruiter review.");
        return String.join(" ", tips);
    }

    private boolean educationMatches(String candidateEducation, String requiredEducation) {
        if (requiredEducation == null || requiredEducation.isBlank()) return true;
        return safe(candidateEducation).toLowerCase().contains(requiredEducation.toLowerCase())
                || safe(requiredEducation).equalsIgnoreCase("any");
    }

    private Set<String> normalize(Collection<String> values) {
        if (values == null) return Set.of();
        return values.stream().filter(Objects::nonNull).map(String::trim).map(String::toLowerCase).filter(s -> !s.isBlank()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }
}
