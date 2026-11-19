package com.plazoleta.plazoleta.domain.spi;

public interface IUsuarioClientPort {
    String obtenerRolUsuario(Long idUsuario);
    String obtenerCelularUsuario(Long idUsuario);
}
