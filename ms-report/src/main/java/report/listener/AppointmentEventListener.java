package report.listener;

import report.model.AppointmentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventListener {

    @KafkaListener(topics = "appointments.events", groupId = "report-group-v2")
    public void handleAppointmentEvent(AppointmentEvent event) {
        System.out.println("Evento recibido en ms-report: " + event.getStatus() + " | ID: " + event.getAppointmentId());
        // Aquí conectas la lógica con tu Service para procesar los KPIs
    }
}