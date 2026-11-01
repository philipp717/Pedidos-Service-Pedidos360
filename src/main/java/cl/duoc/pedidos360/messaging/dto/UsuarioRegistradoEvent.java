package cl.duoc.pedidos360.messaging.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento publicado por Usuarios-Service al registrar un usuario (tópico de usuarios registrados).
 */
public record UsuarioRegistradoEvent(
        UUID eventoId,
        Long usuarioId,
        String entraObjectId,
        String nombre,
        String email,
        Boolean activo,
        Instant fechaRegistro) {
}
