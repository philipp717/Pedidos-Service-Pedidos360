package cl.duoc.pedidos360.service;

import cl.duoc.pedidos360.model.Pedido;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final List<Pedido> pedidos = new ArrayList<>();
    private long siguienteId = 1L;

    public PedidoService() {
        pedidos.add(new Pedido(siguienteId++, "Ana Torres", "Notebook", 1,
                "PENDIENTE", "Av. Providencia 123"));
        pedidos.add(new Pedido(siguienteId++, "Carlos Soto", "Teclado Mecánico", 2,
                "PREPARANDO", "Av. Vicuña Mackenna 456"));
        pedidos.add(new Pedido(siguienteId++, "María González", "Monitor", 1,
                "ENVIADO", "Av. Apoquindo 789"));
    }

    public synchronized List<Pedido> listarTodos() {
        return List.copyOf(pedidos);
    }

    public synchronized Optional<Pedido> buscarPorId(Long id) {
        return pedidos.stream()
                .filter(pedido -> pedido.getId().equals(id))
                .findFirst();
    }

    public synchronized Pedido crear(Pedido pedido) {
        pedido.setId(siguienteId++);
        pedidos.add(pedido);
        return pedido;
    }

    public synchronized Optional<Pedido> actualizar(Long id, Pedido pedido) {
        for (int indice = 0; indice < pedidos.size(); indice++) {
            if (pedidos.get(indice).getId().equals(id)) {
                pedido.setId(id);
                pedidos.set(indice, pedido);
                return Optional.of(pedido);
            }
        }

        return Optional.empty();
    }

    public synchronized boolean eliminar(Long id) {
        return pedidos.removeIf(pedido -> pedido.getId().equals(id));
    }
}
