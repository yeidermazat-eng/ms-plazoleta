package com.plazoleta.plazoleta.domain.spi;

import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import java.util.Optional;

public interface IRestaurantePersistencePort {
    Restaurante guardar(Restaurante restaurante);
    Optional<Restaurante> buscarPorId(Long id);
    boolean existePorNit(String nit);
    PaginacionResultado<Restaurante> listarOrdenadosPorNombre(int page, int size);
}
