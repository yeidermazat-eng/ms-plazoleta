package com.plazoleta.plazoleta.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CrearPedidoRequestDto {
    @NotNull(message = "El idRestaurante es obligatorio")
    private Long idRestaurante;

    @NotEmpty(message = "El pedido debe incluir al menos un plato")
    @Valid
    private List<ItemPedidoRequestDto> platos;

    public Long getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Long idRestaurante) { this.idRestaurante = idRestaurante; }
    public List<ItemPedidoRequestDto> getPlatos() { return platos; }
    public void setPlatos(List<ItemPedidoRequestDto> platos) { this.platos = platos; }
}
