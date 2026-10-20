package notify.listener;

import com.rabbitmq.client.Channel;
import notify.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class EmailListener {

    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
    public void processEmailNotification(Map<String, Object> message,
                                         Message rawMessage,
                                         Channel channel) throws IOException {
        long deliveryTag = rawMessage.getMessageProperties().getDeliveryTag();

        try {
            Object destination = message.get("to") != null ? message.get("to") : message.get("patientEmail");
            if (destination == null || destination.toString().isBlank()) {
                throw new IllegalArgumentException("El mensaje de correo no contiene destinatario válido ('to').");
            }

            System.out.println("=================================================");
            System.out.println("> [MS-NOTIFY] Notificación de Cita Confirmada");
            System.out.println("> Destinatario: " + destination);
            System.out.println("> Cita ID:      " + message.get("appointmentId"));
            System.out.println("> Centro ID:    " + message.get("centerId"));
            System.out.println("> Fecha/Hora:   " + message.get("date"));
            System.out.println("> Estado: Correo enviado satisfactoriamente.");
            System.out.println("=================================================");

            // Confirmacion
            channel.basicAck(deliveryTag, false);

        } catch (IllegalArgumentException ex) {
            System.err.println("[MS-NOTIFY] Datos de email incompletos. Desviando a DLQ: " + ex.getMessage());
            channel.basicNack(deliveryTag, false, false);
        } catch (Exception ex) {
            System.err.println("[MS-NOTIFY] Error en servicio de email. Desviando a DLQ: " + ex.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}