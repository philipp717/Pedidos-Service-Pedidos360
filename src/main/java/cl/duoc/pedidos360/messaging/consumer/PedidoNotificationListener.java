package cl.duoc.pedidos360.messaging.consumer;

import cl.duoc.pedidos360.config.rabbit.RabbitTopologyProperties;
import cl.duoc.pedidos360.messaging.dto.PedidoCreadoEvent;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PedidoNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(PedidoNotificationListener.class);

    private final RabbitTopologyProperties properties;

    public PedidoNotificationListener(RabbitTopologyProperties properties) {
        this.properties = properties;
    }

    @RabbitListener(queues = "${pedidos360.rabbitmq.queue}")
    public void recibir(PedidoCreadoEvent evento, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        log.info("event=pedido_creado_recibido eventoId={} pedidoId={} deliveryTag={}",
                evento.eventoId(), evento.pedidoId(), deliveryTag);

        long backoff = properties.getRetry().getInitialInterval();
        boolean procesado = false;
        for (int intento = 1; intento <= properties.getRetry().getMaxAttempts(); intento++) {
            try {
                procesarNotificacion(evento);
                procesado = true;
                break;
            } catch (Exception error) {
                log.error("event=error_procesando_notificacion eventoId={} pedidoId={} intento={} maxIntentos={} error={}",
                        evento.eventoId(), evento.pedidoId(), intento,
                        properties.getRetry().getMaxAttempts(), error.toString(), error);

                if (intento == properties.getRetry().getMaxAttempts()) {
                    channel.basicNack(deliveryTag, false, false);
                    log.error("event=mensaje_enviado_dlq eventoId={} pedidoId={} deadLetterQueue={}",
                            evento.eventoId(), evento.pedidoId(), properties.getDeadLetterQueue());
                    return;
                }

                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    channel.basicNack(deliveryTag, false, false);
                    log.error("event=mensaje_enviado_dlq_por_interrupcion eventoId={} pedidoId={} deadLetterQueue={}",
                            evento.eventoId(), evento.pedidoId(), properties.getDeadLetterQueue(), interrupted);
                    return;
                }
                backoff = Math.min(
                        (long) (backoff * properties.getRetry().getMultiplier()),
                        properties.getRetry().getMaxInterval());
            }
        }

        if (procesado) {
            channel.basicAck(deliveryTag, false);
            log.info("event=notificacion_pedido_procesada eventoId={} pedidoId={} ack=true",
                    evento.eventoId(), evento.pedidoId());
        }
    }

    private void procesarNotificacion(PedidoCreadoEvent evento) {
        if (evento.eventoId() == null || evento.pedidoId() == null
                || evento.cliente() == null || evento.cliente().isBlank()) {
            throw new IllegalArgumentException("Evento de pedido inválido");
        }

        log.info("event=confirmacion_pedido_simulada pedidoId={} eventoId={}",
                evento.pedidoId(), evento.eventoId());
    }
}
