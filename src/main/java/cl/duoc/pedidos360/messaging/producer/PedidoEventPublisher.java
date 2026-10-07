package cl.duoc.pedidos360.messaging.producer;

import cl.duoc.pedidos360.config.rabbit.RabbitTopologyProperties;
import cl.duoc.pedidos360.messaging.dto.PedidoCreadoEvent;
import cl.duoc.pedidos360.model.Pedido;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class PedidoEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PedidoEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final RabbitTopologyProperties properties;

    public PedidoEventPublisher(RabbitTemplate rabbitTemplate, RabbitTopologyProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
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

        rabbitTemplate.convertAndSend(
                properties.getExchange(), properties.getRoutingKey(), evento);
        log.info("event=pedido_creado_publicado eventoId={} pedidoId={} exchange={} routingKey={}",
                evento.eventoId(), evento.pedidoId(),
                properties.getExchange(), properties.getRoutingKey());
    }
}
