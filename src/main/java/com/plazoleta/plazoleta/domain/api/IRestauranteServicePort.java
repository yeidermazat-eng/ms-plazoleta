package com.plazoleta.plazoleta.domain.api;

import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Restaurante;

public interface IRestauranteServicePort {
    Restaurante crearRestaurante(Restaurante restaurante);
    PaginacionResultado<Restaurante> listarRestaurantes(int page, int size);
}
