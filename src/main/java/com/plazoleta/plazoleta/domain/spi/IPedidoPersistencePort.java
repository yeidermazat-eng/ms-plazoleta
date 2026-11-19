package com.plazoleta.plazoleta.domain.spi;

import com.plazoleta.plazoleta.domain.model.EstadoPedido;
import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Pedido;
import java.util.List;
import java.util.Optional;

public interface IPedidoPersistencePort {
    Pedido guardar(Pedido pedido);
    Optional<Pedido> buscarPorId(Long id);
    boolean existePedidoActivoPorCliente(Long idCliente, List<EstadoPedido> estadosActivos);
    PaginacionResultado<Pedido> listarPorRestauranteYEstado(Long idRestaurante, EstadoPedido estado, int page, int size);
}
