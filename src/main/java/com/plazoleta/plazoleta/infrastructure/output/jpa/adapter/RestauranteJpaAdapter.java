package com.plazoleta.plazoleta.infrastructure.output.jpa.adapter;

import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.spi.IRestaurantePersistencePort;
import com.plazoleta.plazoleta.infrastructure.output.jpa.entity.RestauranteEntity;
import com.plazoleta.plazoleta.infrastructure.output.jpa.repository.IRestauranteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RestauranteJpaAdapter implements IRestaurantePersistencePort {

    private final IRestauranteRepository repository;

    public RestauranteJpaAdapter(IRestauranteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Restaurante guardar(Restaurante r) {
        RestauranteEntity e = new RestauranteEntity();
        e.setId(r.getId());
        e.setNombre(r.getNombre());
        e.setNit(r.getNit());
        e.setDireccion(r.getDireccion());
        e.setTelefono(r.getTelefono());
        e.setUrlLogo(r.getUrlLogo());
        e.setIdPropietario(r.getIdPropietario());
        return toDomain(repository.save(e));
    }

    @Override
    public Optional<Restaurante> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existePorNit(String nit) {
        return repository.existsByNit(nit);
    }

    @Override
    public PaginacionResultado<Restaurante> listarOrdenadosPorNombre(int page, int size) {
        Page<RestauranteEntity> p = repository.findAllByOrderByNombreAsc(PageRequest.of(page, size));
        return new PaginacionResultado<>(
                p.getContent().stream().map(this::toDomain).collect(Collectors.toList()),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()
        );
    }

    private Restaurante toDomain(RestauranteEntity e) {
        Restaurante r = new Restaurante();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setNit(e.getNit());
        r.setDireccion(e.getDireccion());
        r.setTelefono(e.getTelefono());
        r.setUrlLogo(e.getUrlLogo());
        r.setIdPropietario(e.getIdPropietario());
        return r;
    }
}
