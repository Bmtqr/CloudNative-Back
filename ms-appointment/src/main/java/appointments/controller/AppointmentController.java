package appointments.controller;

import appointments.model.Appointment;
import appointments.model.AppointmentStatus;
import appointments.service.AppointmentService;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor

public class AppointmentController {

    private final AppointmentService service;

    // POST /api/appointments
    @PostMapping
    public ResponseEntity<Appointment> create(@RequestBody Appointment appointment) {
        Appointment created = service.createAppointment(appointment);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // GET /api/appointments/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/appointments?status=...&from=...&to=...
    @GetMapping
    public ResponseEntity<List<Appointment>> getAll(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(service.getAll(status, from, to));
    }

    // PUT /api/appointments/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> body,
            java.security.Principal principal) {

        String statusStr = body.get("status");
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El campo 'status' es obligatorio.");
        }

        // Si hay token toma el usuario; si estás probando en local sin token usa "ANONYMOUS"
        String username = (principal != null) ? principal.getName() : body.getOrDefault("user", "ANONYMOUS");
        try {
            AppointmentStatus newStatus = AppointmentStatus.valueOf(statusStr.toUpperCase());
            Appointment updated = service.updateStatus(id, newStatus, username);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Estado inválido.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}