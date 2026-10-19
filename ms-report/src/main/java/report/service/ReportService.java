package report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import report.model.AppointmentEvent;
import report.model.AppointmentStatusMetric;
import report.repository.ReportRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository repository;

    @Transactional
    public void processStatusEvent(AppointmentEvent event) {
        if (event == null || event.getStatus() == null) {
            return;
        }

        String status = event.getStatus();

        AppointmentStatusMetric metric = repository.findById(status)
                .orElse(new AppointmentStatusMetric(status, 0L, LocalDateTime.now()));

        metric.setTotalCount(metric.getTotalCount() + 1);
        metric.setLastUpdated(LocalDateTime.now());

        repository.save(metric);
    }

    public List<AppointmentStatusMetric> getAllMetrics() {
        return repository.findAll();
    }
}