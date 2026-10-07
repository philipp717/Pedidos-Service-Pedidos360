package cl.duoc.pedidos360.config.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;

@Configuration
@EnableConfigurationProperties(RabbitTopologyProperties.class)
public class RabbitTopologyConfig {

    @Bean
    public TopicExchange pedidosEventsExchange(RabbitTopologyProperties properties) {
        return new TopicExchange(properties.getExchange(), true, false);
    }

    @Bean
    public Queue pedidoCreadoQueue(RabbitTopologyProperties properties) {
        return QueueBuilder.durable(properties.getQueue())
                .deadLetterExchange(properties.getDeadLetterExchange())
                .deadLetterRoutingKey(properties.getDeadLetterRoutingKey())
                .build();
    }

    @Bean
    public Binding pedidoCreadoBinding(
            @Qualifier("pedidoCreadoQueue") Queue pedidoCreadoQueue,
            TopicExchange pedidosEventsExchange,
            RabbitTopologyProperties properties) {
        return BindingBuilder.bind(pedidoCreadoQueue)
                .to(pedidosEventsExchange)
                .with(properties.getRoutingKey());
    }

    @Bean
    public DirectExchange pedidosDeadLetterExchange(RabbitTopologyProperties properties) {
        return new DirectExchange(properties.getDeadLetterExchange(), true, false);
    }

    @Bean
    public Queue pedidoCreadoDeadLetterQueue(RabbitTopologyProperties properties) {
        return QueueBuilder.durable(properties.getDeadLetterQueue()).build();
    }

    @Bean
    public Binding pedidoCreadoDeadLetterBinding(
            @Qualifier("pedidoCreadoDeadLetterQueue") Queue pedidoCreadoDeadLetterQueue,
            DirectExchange pedidosDeadLetterExchange,
            RabbitTopologyProperties properties) {
        return BindingBuilder.bind(pedidoCreadoDeadLetterQueue)
                .to(pedidosDeadLetterExchange)
                .with(properties.getDeadLetterRoutingKey());
    }

    @Bean
    public JacksonJsonMessageConverter rabbitMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(rabbitMessageConverter());
        return factory;
    }
}
