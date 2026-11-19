package com.plazoleta.plazoleta.application.handler;

import com.plazoleta.plazoleta.application.dto.request.*;
import com.plazoleta.plazoleta.application.dto.response.*;
import com.plazoleta.plazoleta.application.mapper.PlazoletaMapper;
import com.plazoleta.plazoleta.domain.api.*;
import com.plazoleta.plazoleta.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class PlazoletaHandlerImpl implements IPlazoletaHandler {

    private final IRestauranteServicePort restauranteServicePort;
    private final IPlatoServicePort platoServicePort;
    private final IPedidoServicePort pedidoServicePort;
    private final PlazoletaMapper mapper;

    public PlazoletaHandlerImpl(IRestauranteServicePort restauranteServicePort,
                                IPlatoServicePort platoServicePort,
                                IPedidoServicePort pedidoServicePort,
                                PlazoletaMapper mapper) {
        this.restauranteServicePort = restauranteServicePort;
        this.platoServicePort = platoServicePort;
        this.pedidoServicePort = pedidoServicePort;
        this.mapper = mapper;
    }

    @Override
    public Restaurante crearRestaurante(CrearRestauranteRequestDto dto) {
        return restauranteServicePort.crearRestaurante(mapper.toDomain(dto));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginacionResultado<RestauranteResumenResponseDto> listarRestaurantes(int page, int size) {
        var res = restauranteServicePort.listarRestaurantes(page, size);
        return new PaginacionResultado<>(
                res.getContenido().stream().map(mapper::toResumenDto).collect(Collectors.toList()),
                res.getPaginaActual(), res.getTamanoPagina(), res.getTotalElementos(), res.getTotalPaginas()
        );
    }

    @Override
    public PlatoResponseDto crearPlato(CrearPlatoRequestDto dto, Long idPropietario) {
        return mapper.toResponseDto(platoServicePort.crearPlato(mapper.toDomain(dto), idPropietario));
    }

    @Override
    public PlatoResponseDto modificarPlato(Long idPlato, ModificarPlatoRequestDto dto, Long idPropietario) {
        return mapper.toResponseDto(platoServicePort.modificarPlato(idPlato, dto.getPrecio(), dto.getDescripcion(), idPropietario));
    }

    @Override
    public PlatoResponseDto cambiarEstadoPlato(Long idPlato, Boolean activo, Long idPropietario) {
        return mapper.toResponseDto(platoServicePort.cambiarEstadoPlato(idPlato, activo, idPropietario));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginacionResultado<PlatoResponseDto> listarPlatos(Long idRestaurante, Long idCategoria, int page, int size) {
        var res = platoServicePort.listarPlatosPorRestaurante(idRestaurante, idCategoria, page, size);
        return new PaginacionResultado<>(
                res.getContenido().stream().map(mapper::toResponseDto).collect(Collectors.toList()),
                res.getPaginaActual(), res.getTamanoPagina(), res.getTotalElementos(), res.getTotalPaginas()
        );
    }

    @Override
    public PedidoResponseDto realizarPedido(CrearPedidoRequestDto dto, Long idCliente, String correoCliente) {
        return mapper.toResponseDto(pedidoServicePort.realizarPedido(mapper.toDomain(dto), idCliente, correoCliente));
    }

    @Override
    @Transactional(readOnly = true)
    public PaginacionResultado<PedidoResponseDto> listarPedidosPorEstado(EstadoPedido estado, Long idRestauranteEmpleado, int page, int size) {
        var res = pedidoServicePort.listarPedidosPorEstado(estado, idRestauranteEmpleado, page, size);
        return new PaginacionResultado<>(
                res.getContenido().stream().map(mapper::toResponseDto).collect(Collectors.toList()),
                res.getPaginaActual(), res.getTamanoPagina(), res.getTotalElementos(), res.getTotalPaginas()
        );
    }

    @Override
    public PedidoResponseDto asignarseAPedido(Long idPedido, Long idEmpleado, String correoEmpleado, Long idRestauranteEmpleado) {
        return mapper.toResponseDto(pedidoServicePort.asignarseAPedido(idPedido, idEmpleado, correoEmpleado, idRestauranteEmpleado));
    }

    @Override
    public PedidoResponseDto marcarPedidoListo(Long idPedido, Long idEmpleado, String correoEmpleado, Long idRestauranteEmpleado) {
        return mapper.toResponseDto(pedidoServicePort.marcarPedidoListo(idPedido, idEmpleado, correoEmpleado, idRestauranteEmpleado));
    }

    @Override
    public PedidoResponseDto entregarPedido(Long idPedido, String pin, Long idEmpleado, String correoEmpleado, Long idRestauranteEmpleado) {
        return mapper.toResponseDto(pedidoServicePort.entregarPedido(idPedido, pin, idEmpleado, correoEmpleado, idRestauranteEmpleado));
    }

    @Override
    public PedidoResponseDto cancelarPedido(Long idPedido, Long idCliente, String correoCliente) {
        return mapper.toResponseDto(pedidoServicePort.cancelarPedido(idPedido, idCliente, correoCliente));
    }
}
