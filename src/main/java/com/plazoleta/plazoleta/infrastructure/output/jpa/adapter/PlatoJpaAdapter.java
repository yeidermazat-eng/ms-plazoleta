package com.plazoleta.plazoleta.infrastructure.output.jpa.adapter;

import com.plazoleta.plazoleta.domain.model.Categoria;
import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.spi.IPlatoPersistencePort;
import com.plazoleta.plazoleta.infrastructure.output.jpa.entity.CategoriaEntity;
import com.plazoleta.plazoleta.infrastructure.output.jpa.entity.PlatoEntity;
import com.plazoleta.plazoleta.infrastructure.output.jpa.repository.IPlatoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PlatoJpaAdapter implements IPlatoPersistencePort {

    private final IPlatoRepository repository;

    public PlatoJpaAdapter(IPlatoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Plato guardar(Plato p) {
        PlatoEntity e = new PlatoEntity();
        e.setId(p.getId());
        e.setNombre(p.getNombre());
        e.setPrecio(p.getPrecio());
        e.setDescripcion(p.getDescripcion());
        e.setUrlImagen(p.getUrlImagen());
        if (p.getCategoria() != null) {
            e.setCategoria(new CategoriaEntity(p.getCategoria().getId(), p.getCategoria().getNombre(), p.getCategoria().getDescripcion()));
        }
        e.setIdRestaurante(p.getIdRestaurante());
        e.setActivo(p.getActivo());
        return toDomain(repository.save(e));
    }

    @Override
    public Optional<Plato> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public PaginacionResultado<Plato> listarPorRestauranteYCategoria(Long idRestaurante, Long idCategoria, int page, int size) {
        Page<PlatoEntity> p = (idCategoria != null)
                ? repository.findByIdRestauranteAndCategoriaIdAndActivoTrue(idRestaurante, idCategoria, PageRequest.of(page, size))
                : repository.findByIdRestauranteAndActivoTrue(idRestaurante, PageRequest.of(page, size));
        return new PaginacionResultado<>(
                p.getContent().stream().map(this::toDomain).collect(Collectors.toList()),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()
        );
    }

    private Plato toDomain(PlatoEntity e) {
        Plato p = new Plato();
        p.setId(e.getId());
        p.setNombre(e.getNombre());
        p.setPrecio(e.getPrecio());
        p.setDescripcion(e.getDescripcion());
        p.setUrlImagen(e.getUrlImagen());
        if (e.getCategoria() != null) {
            p.setCategoria(new Categoria(e.getCategoria().getId(), e.getCategoria().getNombre(), e.getCategoria().getDescripcion()));
        }
        p.setIdRestaurante(e.getIdRestaurante());
        p.setActivo(e.getActivo());
        return p;
    }
}
