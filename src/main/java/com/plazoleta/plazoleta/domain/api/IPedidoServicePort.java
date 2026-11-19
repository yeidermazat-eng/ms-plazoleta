package com.plazoleta.plazoleta.domain.api;

import com.plazoleta.plazoleta.domain.model.EstadoPedido;
import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Pedido;

public interface IPedidoServicePort {
    Pedido realizarPedido(Pedido pedido, Long idClienteAutenticado, String correoCliente);
    PaginacionResultado<Pedido> listarPedidosPorEstado(EstadoPedido estado, Long idRestauranteEmpleado, int page, int size);
    Pedido asignarseAPedido(Long idPedido, Long idEmpleadoAutenticado, String correoEmpleado, Long idRestauranteEmpleado);
    Pedido marcarPedidoListo(Long idPedido, Long idEmpleadoAutenticado, String correoEmpleado, Long idRestauranteEmpleado);
    Pedido entregarPedido(Long idPedido, String pinSeguridad, Long idEmpleadoAutenticado, String correoEmpleado, Long idRestauranteEmpleado);
    Pedido cancelarPedido(Long idPedido, Long idClienteAutenticado, String correoCliente);
}
