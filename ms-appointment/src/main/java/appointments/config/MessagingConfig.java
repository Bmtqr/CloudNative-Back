package appointments.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessagingConfig {

    // RabbitMQ: Declaración de cola, exchange y binding
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable("q.cmd.email").build();
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("exchange.direct");
    }

    @Bean
    public Binding bindingEmail(Queue emailQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(emailQueue).to(directExchange).with("rk.cmd.email");
    }

    // Serializador para que RabbitMQ mande JSON legible
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
