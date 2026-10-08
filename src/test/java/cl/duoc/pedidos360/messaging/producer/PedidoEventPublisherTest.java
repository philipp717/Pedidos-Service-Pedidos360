package cl.duoc.pedidos360.messaging.producer;

import cl.duoc.pedidos360.config.rabbit.RabbitTopologyProperties;
import cl.duoc.pedidos360.messaging.dto.PedidoCreadoEvent;
import cl.duoc.pedidos360.model.Pedido;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoEventPublisherTest {

    @Test
    void debePublicarPedidoCreadoEnRabbitMQ() {

        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        RabbitTopologyProperties properties = mock(RabbitTopologyProperties.class);

        when(properties.getExchange()).thenReturn("pedidos360.events");
        when(properties.getRoutingKey()).thenReturn("pedido.creado");

        PedidoEventPublisher publisher =
                new PedidoEventPublisher(rabbitTemplate, properties);

        Pedido pedido = new Pedido(
                1L,
                "Cliente prueba",
                "Producto prueba",
                2,
                "NUEVO",
                "Santiago"
        );

        publisher.publicarPedidoCreado(pedido);

        ArgumentCaptor<PedidoCreadoEvent> captor =
                ArgumentCaptor.forClass(PedidoCreadoEvent.class);

        verify(rabbitTemplate).convertAndSend(
                eq("pedidos360.events"),
                eq("pedido.creado"),
                captor.capture()
        );

        PedidoCreadoEvent evento = captor.getValue();

        assertNotNull(evento.eventoId());
        assertEquals(1L, evento.pedidoId());
        assertEquals("Cliente prueba", evento.cliente());
        assertEquals("Producto prueba", evento.producto());
        assertEquals(2, evento.cantidad());
        assertEquals("NUEVO", evento.estado());
        assertNotNull(evento.fechaCreacion());
    }
}
