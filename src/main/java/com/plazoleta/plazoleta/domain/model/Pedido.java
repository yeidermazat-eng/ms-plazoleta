package com.plazoleta.plazoleta.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Pedido {
    private Long id;
    private Long idCliente;
    private LocalDateTime fecha;
    private EstadoPedido estado;
    private Long idChef;
    private Long idRestaurante;
    private String pinSeguridad;
    private List<PedidoPlato> platos;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdCliente() { return idCliente; }
    public void setIdCliente(Long idCliente) { this.idCliente = idCliente; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public EstadoPedido getEstado() { return estado; }
    public void setEstado(EstadoPedido estado) { this.estado = estado; }
    public Long getIdChef() { return idChef; }
    public void setIdChef(Long idChef) { this.idChef = idChef; }
    public Long getIdRestaurante() { return idRestaurante; }
    public void setIdRestaurante(Long idRestaurante) { this.idRestaurante = idRestaurante; }
    public String getPinSeguridad() { return pinSeguridad; }
    public void setPinSeguridad(String pinSeguridad) { this.pinSeguridad = pinSeguridad; }
    public List<PedidoPlato> getPlatos() { return platos; }
    public void setPlatos(List<PedidoPlato> platos) { this.platos = platos; }
}
