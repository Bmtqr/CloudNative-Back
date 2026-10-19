package report.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import report.model.AppointmentEvent;
import report.service.ReportService;

@Component
@RequiredArgsConstructor
public class ReportListener {

    private final ReportService reportService;

    @KafkaListener(topics = "appointments.events", groupId = "report-group-v2")
    public void consumeEvent(AppointmentEvent event) {
        reportService.processStatusEvent(event);
        System.out.println("Métrica actualizada en MySQL para estado: " + event.getStatus() + " | ID: " + event.getAppointmentId());
    }
}