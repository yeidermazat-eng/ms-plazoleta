package com.plazoleta.plazoleta.domain.api;

import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Plato;

public interface IPlatoServicePort {
    Plato crearPlato(Plato plato, Long idPropietarioAutenticado);
    Plato modificarPlato(Long idPlato, Integer precio, String descripcion, Long idPropietarioAutenticado);
    Plato cambiarEstadoPlato(Long idPlato, Boolean activo, Long idPropietarioAutenticado);
    PaginacionResultado<Plato> listarPlatosPorRestaurante(Long idRestaurante, Long idCategoria, int page, int size);
}
