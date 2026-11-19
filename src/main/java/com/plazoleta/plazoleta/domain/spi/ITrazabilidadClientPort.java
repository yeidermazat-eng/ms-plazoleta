package com.plazoleta.plazoleta.domain.spi;

import java.time.LocalDateTime;

public interface ITrazabilidadClientPort {
    void registrarCambioEstado(Long idPedido, Long idCliente, String correoCliente,
                               Long idRestaurante, Long idEmpleado, String correoEmpleado,
                               String estadoAnterior, String estadoNuevo, LocalDateTime fecha);
}
