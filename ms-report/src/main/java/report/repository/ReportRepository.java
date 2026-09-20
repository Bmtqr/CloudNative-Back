package report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import report.model.AppointmentStatusMetric;

@Repository
public interface ReportRepository extends JpaRepository<AppointmentStatusMetric, String> {
}