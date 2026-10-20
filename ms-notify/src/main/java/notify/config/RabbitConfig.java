package notify.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Exchanges
    public static final String EXCHANGE_DIRECT = "cmd.direct";
    public static final String EXCHANGE_TOPIC = "cmd.topic";
    public static final String DEAD_LETTER_EXCHANGE = "cmd.dead.dlx";

    // Email
    public static final String EMAIL_QUEUE = "q.cmd.email";
    public static final String EMAIL_DLQ = "q.cmd.email.dlq";
    public static final String EMAIL_ROUTING_KEY = "email.send";
    public static final String EMAIL_DLQ_ROUTING_KEY = "email.dlq";

    // Admision
    public static final String ADMISSION_QUEUE = "q.cmd.admission";
    public static final String ADMISSION_DLQ = "q.cmd.admission.dlq";
    public static final String ADMISSION_ROUTING_KEY = "admission.ticket";
    public static final String ADMISSION_DLQ_ROUTING_KEY = "admission.dlq";

    // Record PDF
    public static final String RECORD_QUEUE = "q.cmd.record";
    public static final String RECORD_DLQ = "q.cmd.record.dlq";
    public static final String RECORD_ROUTING_KEY = "record.gen";
    public static final String RECORD_DLQ_ROUTING_KEY = "record.dlq";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Declaracion de exchanges
    @Bean
    public DirectExchange commandDirectExchange() {
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    @Bean
    public TopicExchange commandTopicExchange() {
        return new TopicExchange(EXCHANGE_TOPIC);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    // Colas y bindings email
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(EMAIL_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue emailDeadLetterQueue() {
        return QueueBuilder.durable(EMAIL_DLQ).build();
    }

    @Bean
    public Binding emailDirectBinding(@Qualifier("emailQueue") Queue queue, DirectExchange commandDirectExchange) {
        return BindingBuilder.bind(queue).to(commandDirectExchange).with(EMAIL_ROUTING_KEY);
    }

    @Bean
    public Binding emailDeadLetterBinding(@Qualifier("emailDeadLetterQueue") Queue dlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dlq).to(deadLetterExchange).with(EMAIL_DLQ_ROUTING_KEY);
    }

    // Colas y bindings admision
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
    public Binding admissionDirectBinding(@Qualifier("admissionQueue") Queue queue, DirectExchange commandDirectExchange) {
        return BindingBuilder.bind(queue).to(commandDirectExchange).with(ADMISSION_ROUTING_KEY);
    }

    @Bean
    public Binding admissionTopicBinding(@Qualifier("admissionQueue") Queue queue, TopicExchange commandTopicExchange) {
        return BindingBuilder.bind(queue).to(commandTopicExchange).with("admission.#");
    }

    @Bean
    public Binding admissionDeadLetterBinding(@Qualifier("admissionDeadLetterQueue") Queue dlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dlq).to(deadLetterExchange).with(ADMISSION_DLQ_ROUTING_KEY);
    }

    // Colas y bindings record
    @Bean
    public Queue recordQueue() {
        return QueueBuilder.durable(RECORD_QUEUE)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(RECORD_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue recordDeadLetterQueue() {
        return QueueBuilder.durable(RECORD_DLQ).build();
    }

    @Bean
    public Binding recordDirectBinding(@Qualifier("recordQueue") Queue queue, DirectExchange commandDirectExchange) {
        return BindingBuilder.bind(queue).to(commandDirectExchange).with(RECORD_ROUTING_KEY);
    }

    @Bean
    public Binding recordDeadLetterBinding(@Qualifier("recordDeadLetterQueue") Queue dlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dlq).to(deadLetterExchange).with(RECORD_DLQ_ROUTING_KEY);
    }
}