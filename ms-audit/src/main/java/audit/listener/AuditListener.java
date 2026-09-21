package audit.listener;

import audit.service.AuditService;
import lombok.RequiredArgsConstructor;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor

public class AuditListener {

    private final AuditService auditService;

    @KafkaListener(topics = "appointments.events", groupId = "audit-group")
    public void consumeEvent(Map<String, Object> event) {
        auditService.registerEvent(event);
        System.out.println("> [MS-AUDIT] Registrado evento para cita ID: " + event.get("aggregateId"));
    }
}