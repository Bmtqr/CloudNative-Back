package notify.listener;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EmailListener {

    @RabbitListener(queues = "q.cmd.email")
    public void processEmailNotification(Map<String, Object> message) {
        System.out.println("=================================================");
        System.out.println("> [MS-NOTIFY] Notificación de Cita Confirmada");
        System.out.println("> Destinatario: " + message.get("to"));
        System.out.println("> Cita ID:      " + message.get("appointmentId"));
        System.out.println("> Centro ID:    " + message.get("centerId"));
        System.out.println("> Fecha/Hora:   " + message.get("date"));
        System.out.println("> Estado: Correo enviado satisfactoriamente.");
        System.out.println("=================================================");
    }
}