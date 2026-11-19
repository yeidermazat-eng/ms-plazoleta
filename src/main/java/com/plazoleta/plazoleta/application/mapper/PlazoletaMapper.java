package com.plazoleta.plazoleta.application.mapper;

import com.plazoleta.plazoleta.application.dto.request.*;
import com.plazoleta.plazoleta.application.dto.response.*;
import com.plazoleta.plazoleta.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PlazoletaMapper {

    public Restaurante toDomain(CrearRestauranteRequestDto dto) {
        Restaurante r = new Restaurante();
        r.setNombre(dto.getNombre());
        r.setNit(dto.getNit());
        r.setDireccion(dto.getDireccion());
        r.setTelefono(dto.getTelefono());
        r.setUrlLogo(dto.getUrlLogo());
        r.setIdPropietario(dto.getIdPropietario());
        return r;
    }

    public RestauranteResumenResponseDto toResumenDto(Restaurante r) {
        return new RestauranteResumenResponseDto(r.getNombre(), r.getUrlLogo());
    }

    public Plato toDomain(CrearPlatoRequestDto dto) {
        Plato p = new Plato();
        p.setNombre(dto.getNombre());
        p.setPrecio(dto.getPrecio());
        p.setDescripcion(dto.getDescripcion());
        p.setUrlImagen(dto.getUrlImagen());
        p.setCategoria(new Categoria(dto.getIdCategoria(), null, null));
        p.setIdRestaurante(dto.getIdRestaurante());
        return p;
    }

    public PlatoResponseDto toResponseDto(Plato p) {
        PlatoResponseDto dto = new PlatoResponseDto();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setPrecio(p.getPrecio());
        dto.setDescripcion(p.getDescripcion());
        dto.setUrlImagen(p.getUrlImagen());
        dto.setCategoria(p.getCategoria() != null ? p.getCategoria().getNombre() : null);
        dto.setIdRestaurante(p.getIdRestaurante());
        dto.setActivo(p.getActivo());
        return dto;
    }

    public Pedido toDomain(CrearPedidoRequestDto dto) {
        Pedido p = new Pedido();
        p.setIdRestaurante(dto.getIdRestaurante());
        p.setPlatos(dto.getPlatos().stream()
                .map(i -> new PedidoPlato(i.getIdPlato(), i.getCantidad()))
                .collect(Collectors.toList()));
        return p;
    }

    public PedidoResponseDto toResponseDto(Pedido p) {
        PedidoResponseDto dto = new PedidoResponseDto();
        dto.setId(p.getId());
        dto.setIdCliente(p.getIdCliente());
        dto.setFecha(p.getFecha());
        dto.setEstado(p.getEstado() != null ? p.getEstado().name() : null);
        dto.setIdChef(p.getIdChef());
        dto.setIdRestaurante(p.getIdRestaurante());
        dto.setPlatos(p.getPlatos());
        return dto;
    }
}
