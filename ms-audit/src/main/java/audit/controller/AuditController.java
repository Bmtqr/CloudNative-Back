package audit.controller;

import audit.model.Audit;
import audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService service;

    @GetMapping
    public ResponseEntity<List<Audit>> getAll() {
        return ResponseEntity.ok(service.getAllEvents());
    }

    @GetMapping("/appointment/{id}")
    public ResponseEntity<List<Audit>> getByAppointmentId(@PathVariable String id) {
        return ResponseEntity.ok(service.getEventsByAppointmentId(id));
    }
}