package com.plazoleta.plazoleta.infrastructure.output.jpa.adapter;

import com.plazoleta.plazoleta.domain.model.Categoria;
import com.plazoleta.plazoleta.domain.spi.ICategoriaPersistencePort;
import com.plazoleta.plazoleta.infrastructure.output.jpa.repository.ICategoriaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CategoriaJpaAdapter implements ICategoriaPersistencePort {

    private final ICategoriaRepository repository;

    public CategoriaJpaAdapter(ICategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Categoria> buscarPorId(Long id) {
        return repository.findById(id).map(e -> new Categoria(e.getId(), e.getNombre(), e.getDescripcion()));
    }
}
