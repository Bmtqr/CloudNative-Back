package report.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import report.service.ReportService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportListener {

    private final ReportService reportService;

    @KafkaListener(topics = "appointments.events", groupId = "report-group")
    public void consumeEvent(Map<String, Object> event) {
        reportService.processStatusEvent(event);
        System.out.println("> [MS-REPORT] Métrica actualizada para estado: " + event.get("newStatus"));
    }
}