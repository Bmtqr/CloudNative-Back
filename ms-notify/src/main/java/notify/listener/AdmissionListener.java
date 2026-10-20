package notify.listener;

import java.io.IOException;
import com.rabbitmq.client.Channel;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import notify.config.RabbitConfig;

@Component 
public class AdmissionListener {

    @RabbitListener (queues = RabbitConfig.ADMISSION_QUEUE)
    public void processAdmissionEvent(Map<String, Object> event,
                                      Message rawMessage,
                                      Channel channel) throws IOException {
        long deliveryTag = rawMessage.getMessageProperties().getDeliveryTag();

        try {
            String type = requiredText(event, "type");
            String eventId = requiredText(event, "eventId");
            Object appointmentId = event.get("appointmentId");

            requiredText(event, "timestamp");
            requiredText(event, "correlationId");

            switch (type) {
                case "ADMISSION_TICKET_REQUESTED" ->
                        createAdmissionTicket(eventId, appointmentId, event);
                case "CLINICAL_BOX_CALLED" ->
                        processClinicalBoxCall(eventId, appointmentId, event);
                default -> throw new IllegalArgumentException(
                        "Tipo de evento de admisión no soportado: " + type);
            }

            channel.basicAck(deliveryTag, false);
        } catch (IllegalArgumentException ex) {
            System.err.println("[MS-NOTIFY] Evento de admisión inválido: " + ex.getMessage());
            channel.basicNack(deliveryTag, false, false);
        } catch (Exception ex) {
            System.err.println("[MS-NOTIFY] Error procesando admisión: " + ex.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    private String requiredText(Map<String, Object> event, String field) {
        Object value = event.get(field);
        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException("Falta el campo obligatorio '" + field + "'");
        }
        return value.toString();
    }

    private void createAdmissionTicket(String eventId, Object appointmentId,
                                       Map<String, Object> event) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Falta appointmentId para crear el ticket");
        }
        System.out.println("=================================================");
        System.out.println("[MS-NOTIFY] Solicitud de ticket de admisión");
        System.out.println("eventId:       " + eventId);
        System.out.println("appointmentId: " + appointmentId);
        System.out.println("patientId:     " + event.get("patientId"));
        System.out.println("centerId:      " + event.get("centerId"));
        System.out.println("boxId:         " + event.get("boxId"));
        System.out.println("status:        " + event.get("status"));
        System.out.println("Acción: procesar/generar ticket de admisión.");
        System.out.println("=================================================");
    }

    private void processClinicalBoxCall(String eventId, Object appointmentId,
                                        Map<String, Object> event) {
        if (appointmentId == null) {
            throw new IllegalArgumentException("Falta appointmentId para el llamado a box");
        }
        System.out.println("=================================================");
        System.out.println("[MS-NOTIFY] Llamado a box clínico");
        System.out.println("eventId:       " + eventId);
        System.out.println("appointmentId: " + appointmentId);
        System.out.println("patientId:     " + event.get("patientId"));
        System.out.println("centerId:      " + event.get("centerId"));
        System.out.println("boxId:         " + event.get("boxId"));
        System.out.println("Acción: notificar el llamado al paciente/operador.");
        System.out.println("=================================================");
    }
}