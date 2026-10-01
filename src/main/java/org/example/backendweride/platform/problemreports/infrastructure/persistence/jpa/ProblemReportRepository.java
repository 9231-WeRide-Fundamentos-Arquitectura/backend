package org.example.backendweride.platform.problemreports.infrastructure.persistence.jpa;

import org.example.backendweride.platform.problemreports.domain.model.aggregates.ProblemReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProblemReportRepository extends JpaRepository<ProblemReport, String> {
    List<ProblemReport> findAllByUserId(String userId);
}
