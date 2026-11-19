package com.plazoleta.plazoleta.application.dto.response;

import com.plazoleta.plazoleta.domain.model.PedidoPlato;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponseDto {
    private Long id;
    private Long idCliente;
    private LocalDateTime fecha;
    private String estado;
    private Long idChef;
    private Long idRestaurante;
    private List<PedidoPlato> platos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getIdChef() { return idChef; }
    public void setIdChef(Long idChef) { this.idChef = idChef; }
    public Long getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Long idRestaurante) { this.idRestaurante = idRestaurante; }
    public List<PedidoPlato> getPlatos() { return platos; }
    public void setPlatos(List<PedidoPlato> platos) { this.platos = platos; }
}
