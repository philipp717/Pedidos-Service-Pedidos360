package cl.duoc.pedidos360.service;

import cl.duoc.pedidos360.model.Pedido;
import cl.duoc.pedidos360.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido crear(Pedido pedido) {
        pedido.setId(null);
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> actualizar(Long id, Pedido pedido) {
        if (!pedidoRepository.existsById(id)) {
            return Optional.empty();
        }

        pedido.setId(id);
        return Optional.of(pedidoRepository.save(pedido));
    }

    public boolean eliminar(Long id) {
        if (!pedidoRepository.existsById(id)) {
            return false;
        }

        pedidoRepository.deleteById(id);
        return true;
    }
}
