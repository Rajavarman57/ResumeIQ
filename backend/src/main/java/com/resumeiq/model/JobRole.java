package com.resumeiq.model;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class JobRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(length = 4000)
    private String description;
    private String department;
    private Integer minExperienceYears = 0;
    private String requiredEducation;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> requiredSkills = new LinkedHashSet<>();
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> preferredSkills = new LinkedHashSet<>();
    private boolean active = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Integer getMinExperienceYears() { return minExperienceYears; }
    public void setMinExperienceYears(Integer minExperienceYears) { this.minExperienceYears = minExperienceYears; }
    public String getRequiredEducation() { return requiredEducation; }
    public void setRequiredEducation(String requiredEducation) { this.requiredEducation = requiredEducation; }
    public Set<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(Set<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    public Set<String> getPreferredSkills() { return preferredSkills; }
    public void setPreferredSkills(Set<String> preferredSkills) { this.preferredSkills = preferredSkills; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
