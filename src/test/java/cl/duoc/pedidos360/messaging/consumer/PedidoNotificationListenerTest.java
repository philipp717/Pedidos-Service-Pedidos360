
package cl.duoc.pedidos360.messaging.consumer;

import cl.duoc.pedidos360.config.rabbit.RabbitTopologyProperties;
import cl.duoc.pedidos360.messaging.dto.PedidoCreadoEvent;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;

class PedidoNotificationListenerTest {

    @Test
    void debeConfirmarMensajeValidoConAck() throws Exception {
        RabbitTopologyProperties properties =
                mock(RabbitTopologyProperties.class);
        RabbitTopologyProperties.Retry retry =
                mock(RabbitTopologyProperties.Retry.class);

        when(properties.getRetry()).thenReturn(retry);
        when(retry.getInitialInterval()).thenReturn(1L);
        when(retry.getMaxAttempts()).thenReturn(3);

        PedidoNotificationListener listener =
                new PedidoNotificationListener(properties);

        PedidoCreadoEvent evento = new PedidoCreadoEvent(
                UUID.randomUUID(),
                1L,
                "Cliente prueba",
                "Producto prueba",
                2,
                "NUEVO",
                Instant.now()
        );

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setDeliveryTag(1L);
        Message message = new Message(new byte[0], messageProperties);
        Channel channel = mock(Channel.class);

        listener.recibir(evento, message, channel);

        verify(channel).basicAck(1L, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void debeEnviarMensajeInvalidoADlq() throws Exception {
        RabbitTopologyProperties properties =
                mock(RabbitTopologyProperties.class);
        RabbitTopologyProperties.Retry retry =
                mock(RabbitTopologyProperties.Retry.class);

        when(properties.getRetry()).thenReturn(retry);
        when(retry.getInitialInterval()).thenReturn(1L);
        when(retry.getMaxAttempts()).thenReturn(3);
        when(retry.getMultiplier()).thenReturn(2.0);
        when(retry.getMaxInterval()).thenReturn(5L);
        when(properties.getDeadLetterQueue())
                .thenReturn("pedidos.pedido-creado.dlq");

        PedidoNotificationListener listener =
                new PedidoNotificationListener(properties);

        PedidoCreadoEvent evento = new PedidoCreadoEvent(
                UUID.randomUUID(),
                2L,
                null,
                "Producto prueba",
                1,
                "NUEVO",
                Instant.now()
        );

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setDeliveryTag(2L);
        Message message = new Message(new byte[0], messageProperties);
        Channel channel = mock(Channel.class);

        listener.recibir(evento, message, channel);

        verify(channel).basicNack(2L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}
