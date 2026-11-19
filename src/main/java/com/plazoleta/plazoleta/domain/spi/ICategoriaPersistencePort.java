package com.plazoleta.plazoleta.domain.spi;

import com.plazoleta.plazoleta.domain.model.Categoria;
import java.util.Optional;

public interface ICategoriaPersistencePort {
    Optional<Categoria> buscarPorId(Long id);
}
