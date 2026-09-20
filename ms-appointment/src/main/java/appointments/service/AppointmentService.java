package appointments.service;
import appointments.model.Appointment;
import appointments.model.AppointmentStatus;
import appointments.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final RestClient catalogClient;

    public AppointmentService(AppointmentRepository repository,
                              RabbitTemplate rabbitTemplate,
                              KafkaTemplate<Object, Object> kafkaTemplate,
                              @Value("${services.catalog.url:http://localhost:8082}") String catalogUrl) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.kafkaTemplate = kafkaTemplate;
        this.catalogClient = RestClient.builder().baseUrl(catalogUrl).build();
    }

    public Appointment createAppointment(Appointment appointment) {
        appointment.setStatus(AppointmentStatus.SOLICITADA);
        return repository.save(appointment);
    }

    public Optional<Appointment> getById(Long id) {
        return repository.findById(id);
    }

    public List<Appointment> getAll(AppointmentStatus status, LocalDate from, LocalDate to) {
        if (status != null && from != null && to != null) {
            return repository.findByStatusAndAppointmentDateBetween(status, from, to);
        } else if (status != null) {
            return repository.findByStatus(status);
        }
        return repository.findAll();
    }

    public Appointment updateStatus(Long id, AppointmentStatus newStatus, String username) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atención no encontrada con ID: " + id));

        // No se puede pasar a EN_ATENCIÓN sin CONFIRMAR
        if (newStatus == AppointmentStatus.EN_ATENCION && appointment.getStatus() != AppointmentStatus.CONFIRMADA) {
            throw new IllegalStateException("No se puede pasar al estado EN_ATENCIÓN si la atención no fue CONFIRMADA previamente.");
        }

        // Descuenta cupo en catalog al confirmar y obtiene el box asignado
        if (newStatus == AppointmentStatus.CONFIRMADA) {
            Map<?, ?> catalogResponse = catalogClient.put()
                    .uri("/api/catalog/services/{id}/decrease-quota", appointment.getServiceId())
                    .retrieve()
                    .body(Map.class);

            if (catalogResponse != null && catalogResponse.containsKey("boxId") && catalogResponse.get("boxId") != null) {
                appointment.setBoxId(Long.valueOf(catalogResponse.get("boxId").toString()));
            }
        }

        appointment.setStatus(newStatus);
        Appointment saved = repository.save(appointment);

        // Envia la notificacion si se confirma la atencion (rabbit)
        if (newStatus == AppointmentStatus.CONFIRMADA && saved.getPatientEmail() != null) {
            Map<String, Object> emailPayload = Map.of(
                "to", saved.getPatientEmail(),
                "appointmentId", saved.getId(),
                "centerId", (saved.getCenterId() != null) ? saved.getCenterId() : "",
                "date", saved.getAppointmentDate().toString()
            );
            rabbitTemplate.convertAndSend("exchange.direct", "rk.cmd.email", emailPayload);
        }

        // Se envia el evento hacia kakfa
        Map<String, Object> kafkaPayload = Map.of(
            "eventId", UUID.randomUUID().toString(),
            "aggregateId", saved.getId().toString(),
            "eventType", "APPOINTMENT_STATUS_CHANGED",
            "newStatus", newStatus.name(),
            "user", username,
            "timestamp", Instant.now().toString()
        );
        kafkaTemplate.send("appointments.events", saved.getId().toString(), kafkaPayload);

        return saved;
    }


    public Appointment updateAppointment(Long id, Appointment details) {
        Appointment existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atención no encontrada con ID: " + id));

        if (details.getPatientId() != null) {
            existing.setPatientId(details.getPatientId());
        }
        if (details.getPatientEmail() != null) {
            existing.setPatientEmail(details.getPatientEmail());
        }
        if (details.getCenterId() != null) {
            existing.setCenterId(details.getCenterId());
        }
        if (details.getServiceId() != null) {
            existing.setServiceId(details.getServiceId());
        }
        if (details.getBoxId() != null) {
            existing.setBoxId(details.getBoxId());
        }
        if (details.getAppointmentDate() != null) {
            existing.setAppointmentDate(details.getAppointmentDate());
        }
        if (details.getStatus() != null) {
            existing.setStatus(details.getStatus());
        }

        return repository.save(existing);
    }

    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Atención no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}