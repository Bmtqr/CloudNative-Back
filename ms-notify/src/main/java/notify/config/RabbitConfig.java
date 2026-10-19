package notify.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EMAIL_QUEUE = "q.cmd.email";

    public static final String DEAD_LETTER_EXCHANGE = "cmd.dead.dlx";

    // Admisión
    public static final String ADMISSION_QUEUE = "q.cmd.admission";
    public static final String ADMISSION_DLQ = "q.cmd.admission.dlq";
    public static final String ADMISSION_ROUTING_KEY = "admission.ticket";
    public static final String ADMISSION_DLQ_ROUTING_KEY = "admission.dlq";

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public Queue admissionQueue() {
        return QueueBuilder.durable(ADMISSION_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(ADMISSION_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue admissionDeadLetterQueue() {
        return QueueBuilder.durable(ADMISSION_DLQ).build();
    }

    @Bean
    public Binding admissionDirectBinding(@Qualifier("admissionQueue") Queue admissionQueue,
                                          DirectExchange commandDirectExchange) {
        return BindingBuilder.bind(admissionQueue)
                .to(commandDirectExchange)
                .with(ADMISSION_ROUTING_KEY);
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
                .with(ADMISSION_DLQ_ROUTING_KEY);
    }
}