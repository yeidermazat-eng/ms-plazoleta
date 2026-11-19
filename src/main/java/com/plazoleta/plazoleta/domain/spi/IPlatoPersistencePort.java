package com.plazoleta.plazoleta.domain.spi;

import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Plato;
import java.util.Optional;

public interface IPlatoPersistencePort {
    Plato guardar(Plato plato);
    Optional<Plato> buscarPorId(Long id);
    PaginacionResultado<Plato> listarPorRestauranteYCategoria(Long idRestaurante, Long idCategoria, int page, int size);
}
