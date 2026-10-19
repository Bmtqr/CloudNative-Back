package appointments.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    public static final String EXCHANGE = "cmd.direct";
    public static final String QUEUE_EMAIL = "q.cmd.email";
    public static final String ROUTING_KEY_EMAIL = "email.send";

    public static final String DEAD_LETTER_EXCHANGE = "cmd.dead.dlx";

    public static final String QUEUE_ADMISSION = "q.cmd.admission";
    public static final String QUEUE_ADMISSION_DLQ = "q.cmd.admission.dlq";
    public static final String ROUTING_KEY_ADMISSION = "admission.ticket";
    public static final String ROUTING_KEY_ADMISSION_DLQ = "admission.dlq";

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(QUEUE_EMAIL).build();
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingEmail(Queue emailQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(emailQueue).to(directExchange).with(ROUTING_KEY_EMAIL);
    }

    // Cola Admisión
    @Bean
    public Queue admissionQueue() {
        return QueueBuilder.durable(QUEUE_ADMISSION)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(ROUTING_KEY_ADMISSION_DLQ)
                .build();
    }

    @Bean
    public Queue admissionDeadLetterQueue() {
        return QueueBuilder.durable(QUEUE_ADMISSION_DLQ).build();
    }

    @Bean
    public Binding admissionDirectBinding(@Qualifier("admissionQueue") Queue admissionQueue,
                                          DirectExchange commandDirectExchange) {
        return BindingBuilder.bind(admissionQueue)
                .to(commandDirectExchange)
                .with(ROUTING_KEY_ADMISSION);
    }

    @Bean
    public Binding admissionTopicBinding(@Qualifier("admissionQueue") Queue admissionQueue,
                                         TopicExchange commandTopicExchange) {
        return BindingBuilder.bind(admissionQueue)
                .to(commandTopicExchange)
                .with("admission.#");
    }

    @Bean
    public Binding admissionDeadLetterBinding(@Qualifier("admissionDeadLetterQueue") Queue admissionDeadLetterQueue,
                                              DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(admissionDeadLetterQueue)
                .to(deadLetterExchange)
                .with(ROUTING_KEY_ADMISSION_DLQ);
    }

    // Serializador para que RabbitMQ mande JSON legible
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}