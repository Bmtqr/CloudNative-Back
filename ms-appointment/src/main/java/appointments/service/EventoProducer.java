package appointments.service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import appointments.config.MessagingConfig;
import appointments.model.Appointment;

@Service 
public class EventoProducer {
    @Autowired 
    private RabbitTemplate rabbitTemplate;

    public void publish(Appointment appointment, String eventType) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("type", eventType);
        event.put("eventId", UUID.randomUUID().toString());
        event.put("timestamp", Instant.now().toString());
        event.put("traceId", UUID.randomUUID().toString());
        event.put("correlationId", String.valueOf(appointment.getId()));

        event.put("appointmentId", appointment.getId());
        event.put("patientId", appointment.getPatientId());
        event.put("centerId", appointment.getCenterId());
        event.put("boxId", appointment.getBoxId());
        event.put("status", appointment.getStatus() != null
                ? appointment.getStatus().name() : null);
        event.put("appointmentDate", appointment.getAppointmentDate() != null
                ? appointment.getAppointmentDate().toString() : null);

        rabbitTemplate.convertAndSend(
                MessagingConfig.EXCHANGE_DIRECT,
                MessagingConfig.ROUTING_KEY_ADMISSION,
                event
        );
    }
}