package audit.service;

import audit.model.Audit;
import audit.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository repository;

    @Transactional
    public Audit registerEvent(Map<String, Object> event) {
        Audit audit = new Audit();
        audit.setEventId(Objects.toString(event.get("eventId"), null));
        audit.setAggregateId(Objects.toString(event.get("aggregateId"), null));
        audit.setEventType(Objects.toString(event.get("eventType"), null));
        audit.setNewStatus(Objects.toString(event.get("newStatus"), null));
        audit.setExecutedBy(Objects.toString(event.get("user"), "ANONYMOUS"));
        audit.setEventTimestamp(Objects.toString(event.get("timestamp"), null));

        return repository.save(audit);
    }

    @Transactional(readOnly = true)
    public List<Audit> getAllEvents() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Audit> getEventsByAppointmentId(String id) {
        return repository.findByAggregateId(id);
    }
}