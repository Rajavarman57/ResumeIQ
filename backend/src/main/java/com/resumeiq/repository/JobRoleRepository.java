package com.resumeiq.repository;

import com.resumeiq.model.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRoleRepository extends JpaRepository<JobRole, Long> {
    List<JobRole> findByActiveTrue();
}
