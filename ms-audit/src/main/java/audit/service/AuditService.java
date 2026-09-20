package audit.service;

import audit.model.Audit;
import audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository repository;

    public Audit registerEvent(Map<String, Object> event) {
        Audit audit = new Audit();
        audit.setEventId((String) event.get("eventId"));
        audit.setAggregateId(String.valueOf(event.get("aggregateId")));
        audit.setEventType((String) event.get("eventType"));
        audit.setNewStatus((String) event.get("newStatus"));
        audit.setExecutedBy((String) event.get("user"));
        audit.setEventTimestamp((String) event.get("timestamp"));

        return repository.save(audit);
    }

    public List<Audit> getAllEvents() {
        return repository.findAll();
    }

    public List<Audit> getEventsByAppointmentId(String id) {
        return repository.findByAggregateId(id);
    }
}