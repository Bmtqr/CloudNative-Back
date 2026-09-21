package report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import report.model.AppointmentStatusMetric;
import report.service.ReportService;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/status-summary")
    public ResponseEntity<List<AppointmentStatusMetric>> getStatusSummary() {
        return ResponseEntity.ok(reportService.getAllMetrics());
    }
}