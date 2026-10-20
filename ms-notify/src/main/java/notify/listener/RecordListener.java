package notify.listener;

import com.rabbitmq.client.Channel;
import notify.config.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class RecordListener {

    @RabbitListener(queues = RabbitConfig.RECORD_QUEUE)
    public void processRecordGeneration(Map<String, Object> message,
                                        Message rawMessage,
                                        Channel channel) throws IOException {
        long deliveryTag = rawMessage.getMessageProperties().getDeliveryTag();

        try {
            Object appointmentId = message.get("appointmentId");
            if (appointmentId == null) {
                throw new IllegalArgumentException("Falta appointmentId para la generación documental.");
            }

            System.out.println("=================================================");
            System.out.println("> [MS-NOTIFY] Comprobante PDF generado");
            System.out.println("> Atención ID: " + appointmentId);
            System.out.println("=================================================");

            channel.basicAck(deliveryTag, false);

        } catch (Exception ex) {
            System.err.println("[MS-NOTIFY] Error generando PDF. Desviando a DLQ: " + ex.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}