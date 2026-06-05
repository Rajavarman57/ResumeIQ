package com.resumeiq;

import com.resumeiq.model.Recruiter;
import com.resumeiq.model.JobRole;
import com.resumeiq.repository.JobRoleRepository;
import com.resumeiq.repository.RecruiterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.LinkedHashSet;
import java.util.List;

@SpringBootApplication
public class ResumeIqApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResumeIqApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDemoData(RecruiterRepository recruiters, JobRoleRepository jobs, PasswordEncoder encoder) {
        return args -> {
            recruiters.findByEmail("admin@resumeiq.local").orElseGet(() -> {
            Recruiter recruiter = new Recruiter();
            recruiter.setName("ResumeIQ Admin");
            recruiter.setEmail("admin@resumeiq.local");
            recruiter.setPasswordHash(encoder.encode("admin123"));
            recruiter.setRole("ADMIN");
            return recruiters.save(recruiter);
            });
            if (jobs.count() == 0) {
                jobs.save(role("Java Spring Boot Developer", "Engineering", 2, "Bachelors",
                        List.of("java", "spring boot", "mysql", "rest api", "git"),
                        List.of("docker", "microservices", "aws")));
                jobs.save(role("Frontend React Developer", "Product Engineering", 1, "Bachelors",
                        List.of("react", "javascript", "html", "css", "bootstrap"),
                        List.of("typescript", "testing", "rest api")));
                jobs.save(role("Data Analyst", "Analytics", 1, "Any",
                        List.of("data analysis", "excel", "power bi", "mysql"),
                        List.of("python", "tableau", "communication")));
            }
        };
    }

    private JobRole role(String title, String department, int experience, String education, List<String> required, List<String> preferred) {
        JobRole role = new JobRole();
        role.setTitle(title);
        role.setDepartment(department);
        role.setMinExperienceYears(experience);
        role.setRequiredEducation(education);
        role.setRequiredSkills(new LinkedHashSet<>(required));
        role.setPreferredSkills(new LinkedHashSet<>(preferred));
        role.setDescription("Seed role for ResumeIQ demo and review.");
        return role;
    }
}
