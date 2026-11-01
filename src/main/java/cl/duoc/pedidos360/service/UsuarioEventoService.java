package cl.duoc.pedidos360.service;

import cl.duoc.pedidos360.messaging.dto.UsuarioRegistradoEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Lógica de dominio asociada a eventos de usuarios registrados, independiente de Kafka.
 */
@Service
public class UsuarioEventoService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioEventoService.class);

    public void registrarUsuarioDisponible(UsuarioRegistradoEvent evento) {
        // Punto de extensión: p. ej. cachear usuarios habilitados para crear pedidos
        log.info("event=usuario_disponible_para_pedidos usuarioId={} email={} activo={}",
                evento.usuarioId(), evento.email(), evento.activo());
    }
}
