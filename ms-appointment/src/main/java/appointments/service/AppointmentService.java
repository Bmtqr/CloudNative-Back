package appointments.service;

import appointments.config.MessagingConfig;
import appointments.model.Appointment;
import appointments.model.AppointmentStatus;
import appointments.repository.AppointmentRepository;
import org.springframework.transaction.annotation.Transactional;
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
    private final EventoProducer eventProducer;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final RestClient catalogClient;

    public AppointmentService(AppointmentRepository repository,
                              RabbitTemplate rabbitTemplate,
                              EventoProducer eventProducer,
                              KafkaTemplate<Object, Object> kafkaTemplate,
                              @Value("${services.catalog.url:http://localhost:8082}") String catalogUrl) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
        this.eventProducer = eventProducer;
        this.kafkaTemplate = kafkaTemplate;
        this.catalogClient = RestClient.builder().baseUrl(catalogUrl).build();
    }

    @Transactional
    public Appointment createAppointment(Appointment appointment) {
        appointment.setStatus(AppointmentStatus.SOLICITADA);
        return repository.save(appointment);
    }

    @Transactional(readOnly = true)
    public Optional<Appointment> getById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Appointment> getAll(AppointmentStatus status, LocalDate from, LocalDate to) {
        if (status != null && from != null && to != null) {
            return repository.findByStatusAndAppointmentDateBetween(status, from, to);
        } else if (status != null) {
            return repository.findByStatus(status);
        }
        return repository.findAll();
    }

    @Transactional
    public Appointment updateStatus(Long id, AppointmentStatus newStatus, String username) {
        Appointment appointment = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atención no encontrada con ID: " + id));

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

        // Notificacion de email
        if (newStatus == AppointmentStatus.CONFIRMADA && saved.getPatientEmail() != null) {
            Map<String, Object> emailPayload = Map.of(
                "to", saved.getPatientEmail(),
                "appointmentId", saved.getId(),
                "centerId", (saved.getCenterId() != null) ? saved.getCenterId() : "",
                "date", (saved.getAppointmentDate() != null) ? saved.getAppointmentDate().toString() : ""
            );
            rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE_DIRECT, MessagingConfig.ROUTING_KEY_EMAIL, emailPayload);
        }

        // Ticket de admission y llamado a box
        if (newStatus == AppointmentStatus.EN_ESPERA) {
            eventProducer.publish(saved, "ADMISSION_TICKET_REQUESTED");
        } else if (newStatus == AppointmentStatus.EN_ATENCION) {
            eventProducer.publish(saved, "CLINICAL_BOX_CALLED");
        } 
        // PDF
        else if (newStatus == AppointmentStatus.CERRADA) {
            Map<String, Object> recordPayload = Map.of(
                "appointmentId", saved.getId(),
                "patientId", (saved.getPatientId() != null) ? saved.getPatientId() : "",
                "status", newStatus.name(),
                "timestamp", Instant.now().toString()
            );
            rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE_DIRECT, MessagingConfig.ROUTING_KEY_RECORD, recordPayload);
        }

        // Kafka
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

    @Transactional
    public void deleteById(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Atención no encontrada con ID: " + id);
        }
        repository.deleteById(id);
    }
}