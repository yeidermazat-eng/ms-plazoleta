package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.request.*;
import com.plazoleta.plazoleta.application.dto.response.*;
import com.plazoleta.plazoleta.domain.model.EstadoPedido;
import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Restaurante;

public interface IPlazoletaHandler {
    Restaurante crearRestaurante(CrearRestauranteRequestDto dto);
    PaginacionResultado<RestauranteResumenResponseDto> listarRestaurantes(int page, int size);

    PlatoResponseDto crearPlato(CrearPlatoRequestDto dto, Long idPropietario);
    PlatoResponseDto modificarPlato(Long idPlato, ModificarPlatoRequestDto dto, Long idPropietario);
    PlatoResponseDto cambiarEstadoPlato(Long idPlato, Boolean activo, Long idPropietario);
    PaginacionResultado<PlatoResponseDto> listarPlatos(Long idRestaurante, Long idCategoria, int page, int size);

    PedidoResponseDto realizarPedido(CrearPedidoRequestDto dto, Long idCliente, String correoCliente);
    PaginacionResultado<PedidoResponseDto> listarPedidosPorEstado(EstadoPedido estado, Long idRestauranteEmpleado, int page, int size);
    PedidoResponseDto asignarseAPedido(Long idPedido, Long idEmpleado, String correoEmpleado, Long idRestauranteEmpleado);
    PedidoResponseDto marcarPedidoListo(Long idPedido, Long idEmpleado, String correoEmpleado, Long idRestauranteEmpleado);
    PedidoResponseDto entregarPedido(Long idPedido, String pin, Long idEmpleado, String correoEmpleado, Long idRestauranteEmpleado);
    PedidoResponseDto cancelarPedido(Long idPedido, Long idCliente, String correoCliente);
}
