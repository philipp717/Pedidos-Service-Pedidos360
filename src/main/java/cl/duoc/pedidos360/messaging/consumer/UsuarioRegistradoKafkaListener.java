package cl.duoc.pedidos360.messaging.consumer;

import cl.duoc.pedidos360.messaging.dto.UsuarioRegistradoEvent;
import cl.duoc.pedidos360.service.UsuarioEventoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Listener Kafka del tópico de usuarios registrados. Solo valida y delega al servicio de dominio.
 * Las excepciones se relanzan para que el DefaultErrorHandler aplique reintentos y DLT.
 */
@Component
public class UsuarioRegistradoKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(UsuarioRegistradoKafkaListener.class);

    private final UsuarioEventoService usuarioEventoService;

    public UsuarioRegistradoKafkaListener(UsuarioEventoService usuarioEventoService) {
        this.usuarioEventoService = usuarioEventoService;
    }

    @KafkaListener(
            topics = "${pedidos360.kafka.topics.usuarios-registrados}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void recibir(
            @Payload(required = false) UsuarioRegistradoEvent evento,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            validar(evento);
            usuarioEventoService.registrarUsuarioDisponible(evento);
            log.info("event=usuario_registrado_recibido eventoId={} usuarioId={} topic={} particion={} offset={}",
                    evento.eventoId(), evento.usuarioId(), topic, partition, offset);
        } catch (Exception error) {
            log.error("event=error_usuario_registrado topic={} particion={} offset={} usuarioId={} error={}",
                    topic, partition, offset,
                    evento != null ? evento.usuarioId() : null,
                    error.getMessage(), error);
            throw error;
        }
    }

    private void validar(UsuarioRegistradoEvent evento) {
        if (evento == null
                || evento.eventoId() == null
                || evento.usuarioId() == null
                || evento.email() == null
                || evento.email().isBlank()) {
            throw new IllegalArgumentException("Evento UsuarioRegistradoEvent nulo, vacío o inválido");
        }
    }
}
