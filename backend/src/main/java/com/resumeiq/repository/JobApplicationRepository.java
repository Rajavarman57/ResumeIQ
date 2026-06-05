package com.resumeiq.repository;

import com.resumeiq.model.ApplicationStatus;
import com.resumeiq.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobRoleId(Long jobRoleId);
    List<JobApplication> findByStatus(ApplicationStatus status);
    boolean existsByCandidateIdAndJobRoleId(Long candidateId, Long jobRoleId);
}
