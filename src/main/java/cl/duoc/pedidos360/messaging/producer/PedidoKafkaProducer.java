package cl.duoc.pedidos360.messaging.producer;

import cl.duoc.pedidos360.messaging.dto.PedidoCreadoEvent;
import cl.duoc.pedidos360.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Productor Kafka del evento de pedido creado. Es complementario a
 * {@link PedidoEventPublisher} (RabbitMQ); un fallo de Kafka no afecta la creación del pedido.
 */
@Component
public class PedidoKafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(PedidoKafkaProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;

    public PedidoKafkaProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${pedidos360.kafka.topics.pedidos-creados}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publicarPedidoCreado(Pedido pedido) {
        PedidoCreadoEvent evento = new PedidoCreadoEvent(
                UUID.randomUUID(),
                pedido.getId(),
                pedido.getCliente(),
                pedido.getProducto(),
                pedido.getCantidad(),
                pedido.getEstado(),
                Instant.now());

        try {
            kafkaTemplate.send(topic, String.valueOf(evento.pedidoId()), evento)
                    .whenComplete((result, error) -> {
                        if (error != null) {
                            log.error("event=pedido_creado_kafka_error pedidoId={} topic={} error={}",
                                    evento.pedidoId(), topic, error.getMessage());
                        } else {
                            log.info("event=pedido_creado_kafka_publicado pedidoId={} topic={} particion={} offset={}",
                                    evento.pedidoId(), topic,
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("event=pedido_creado_kafka_error pedidoId={} topic={} error={}",
                    evento.pedidoId(), topic, e.getMessage());
        }
    }
}
