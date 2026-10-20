package appointments.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    // Exchanges
    public static final String EXCHANGE_DIRECT = "cmd.direct";
    public static final String EXCHANGE_TOPIC = "cmd.topic";
    public static final String DEAD_LETTER_EXCHANGE = "cmd.dead.dlx";

    // 1. Flujo Email
    public static final String QUEUE_EMAIL = "q.cmd.email";
    public static final String QUEUE_EMAIL_DLQ = "q.cmd.email.dlq";
    public static final String ROUTING_KEY_EMAIL = "email.send";
    public static final String ROUTING_KEY_EMAIL_DLQ = "email.dlq";

    // 2. Flujo Admisión
    public static final String QUEUE_ADMISSION = "q.cmd.admission";
    public static final String QUEUE_ADMISSION_DLQ = "q.cmd.admission.dlq";
    public static final String ROUTING_KEY_ADMISSION = "admission.ticket";
    public static final String ROUTING_KEY_ADMISSION_DLQ = "admission.dlq";

    // 3. Flujo PDF/Record
    public static final String QUEUE_RECORD = "q.cmd.record";
    public static final String QUEUE_RECORD_DLQ = "q.cmd.record.dlq";
    public static final String ROUTING_KEY_RECORD = "record.gen";
    public static final String ROUTING_KEY_RECORD_DLQ = "record.dlq";

    // Exchanges Beans
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_TOPIC);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DEAD_LETTER_EXCHANGE);
    }

    // --- 1. Flujo EMAIL ---
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(QUEUE_EMAIL)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(ROUTING_KEY_EMAIL_DLQ)
                .build();
    }

    @Bean
    public Queue emailDeadLetterQueue() {
        return QueueBuilder.durable(QUEUE_EMAIL_DLQ).build();
    }

    @Bean
    public Binding bindingEmail(@Qualifier("emailQueue") Queue emailQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(emailQueue).to(directExchange).with(ROUTING_KEY_EMAIL);
    }

    @Bean
    public Binding bindingEmailDLQ(@Qualifier("emailDeadLetterQueue") Queue dlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dlq).to(deadLetterExchange).with(ROUTING_KEY_EMAIL_DLQ);
    }

    // --- 2. Flujo ADMISIÓN ---
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
    public Binding admissionDirectBinding(@Qualifier("admissionQueue") Queue admissionQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(admissionQueue).to(directExchange).with(ROUTING_KEY_ADMISSION);
    }

    @Bean
    public Binding admissionTopicBinding(@Qualifier("admissionQueue") Queue admissionQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(admissionQueue).to(topicExchange).with("admission.#");
    }

    @Bean
    public Binding admissionDeadLetterBinding(@Qualifier("admissionDeadLetterQueue") Queue dlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dlq).to(deadLetterExchange).with(ROUTING_KEY_ADMISSION_DLQ);
    }

    // --- 3. Flujo RECORD (PDF) ---
    @Bean
    public Queue recordQueue() {
        return QueueBuilder.durable(QUEUE_RECORD)
                .deadLetterExchange(DEAD_LETTER_EXCHANGE)
                .deadLetterRoutingKey(ROUTING_KEY_RECORD_DLQ)
                .build();
    }

    @Bean
    public Queue recordDeadLetterQueue() {
        return QueueBuilder.durable(QUEUE_RECORD_DLQ).build();
    }

    @Bean
    public Binding recordDirectBinding(@Qualifier("recordQueue") Queue recordQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(recordQueue).to(directExchange).with(ROUTING_KEY_RECORD);
    }

    @Bean
    public Binding recordDeadLetterBinding(@Qualifier("recordDeadLetterQueue") Queue dlq, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(dlq).to(deadLetterExchange).with(ROUTING_KEY_RECORD_DLQ);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}