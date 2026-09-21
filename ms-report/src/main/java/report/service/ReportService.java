package report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import report.model.AppointmentStatusMetric;
import report.repository.ReportRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository repository;

    @Transactional
    public void processStatusEvent(Map<String, Object> event) {
        String newStatus = (String) event.get("newStatus");
        if (newStatus == null) {
            return;
        }

        AppointmentStatusMetric metric = repository.findById(newStatus)
                .orElse(new AppointmentStatusMetric(newStatus, 0L, LocalDateTime.now()));

        metric.setTotalCount(metric.getTotalCount() + 1);
        metric.setLastUpdated(LocalDateTime.now());

        repository.save(metric);
    }

    public List<AppointmentStatusMetric> getAllMetrics() {
        return repository.findAll();
    }
}