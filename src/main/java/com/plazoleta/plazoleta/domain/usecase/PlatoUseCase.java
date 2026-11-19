package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.api.IPlatoServicePort;
import com.plazoleta.plazoleta.domain.exception.AccesoDenegadoException;
import com.plazoleta.plazoleta.domain.exception.DatosInvalidosException;
import com.plazoleta.plazoleta.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.plazoleta.domain.model.Categoria;
import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.spi.ICategoriaPersistencePort;
import com.plazoleta.plazoleta.domain.spi.IPlatoPersistencePort;
import com.plazoleta.plazoleta.domain.spi.IRestaurantePersistencePort;

public class PlatoUseCase implements IPlatoServicePort {

    private final IPlatoPersistencePort platoPersistencePort;
    private final IRestaurantePersistencePort restaurantePersistencePort;
    private final ICategoriaPersistencePort categoriaPersistencePort;

    public PlatoUseCase(IPlatoPersistencePort platoPersistencePort,
                        IRestaurantePersistencePort restaurantePersistencePort,
                        ICategoriaPersistencePort categoriaPersistencePort) {
        this.platoPersistencePort = platoPersistencePort;
        this.restaurantePersistencePort = restaurantePersistencePort;
        this.categoriaPersistencePort = categoriaPersistencePort;
    }

    @Override
    public Plato crearPlato(Plato plato, Long idPropietarioAutenticado) {
        if (plato.getPrecio() == null || plato.getPrecio() <= 0) {
            throw new DatosInvalidosException("El precio del plato debe ser un entero positivo mayor a 0.");
        }
        Restaurante restaurante = restaurantePersistencePort.buscarPorId(plato.getIdRestaurante())
                .orElseThrow(() -> new RecursoNoEncontradoException("Restaurante no encontrado."));
        if (!restaurante.getIdPropietario().equals(idPropietarioAutenticado)) {
            throw new AccesoDenegadoException("Solo el propietario del restaurante puede crear platos en este establecimiento.");
        }
        Categoria categoria = categoriaPersistencePort.buscarPorId(plato.getCategoria().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Categoría no encontrada."));
        plato.setCategoria(categoria);
        plato.setActivo(true);
        return platoPersistencePort.guardar(plato);
    }

    @Override
    public Plato modificarPlato(Long idPlato, Integer precio, String descripcion, Long idPropietarioAutenticado) {
        if (precio == null || precio <= 0) {
            throw new DatosInvalidosException("El precio del plato debe ser un entero positivo mayor a 0.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new DatosInvalidosException("La descripción del plato es obligatoria.");
        }
        Plato plato = platoPersistencePort.buscarPorId(idPlato)
                .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado."));
        validarPropiedadRestaurante(plato.getIdRestaurante(), idPropietarioAutenticado);
        plato.setPrecio(precio);
        plato.setDescripcion(descripcion);
        return platoPersistencePort.guardar(plato);
    }

    @Override
    public Plato cambiarEstadoPlato(Long idPlato, Boolean activo, Long idPropietarioAutenticado) {
        if (activo == null) {
            throw new DatosInvalidosException("El estado activo es obligatorio.");
        }
        Plato plato = platoPersistencePort.buscarPorId(idPlato)
                .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado."));
        validarPropiedadRestaurante(plato.getIdRestaurante(), idPropietarioAutenticado);
        plato.setActivo(activo);
        return platoPersistencePort.guardar(plato);
    }

    @Override
    public PaginacionResultado<Plato> listarPlatosPorRestaurante(Long idRestaurante, Long idCategoria, int page, int size) {
        return platoPersistencePort.listarPorRestauranteYCategoria(idRestaurante, idCategoria, page, size);
    }

    private void validarPropiedadRestaurante(Long idRestaurante, Long idPropietarioAutenticado) {
        Restaurante restaurante = restaurantePersistencePort.buscarPorId(idRestaurante)
                .orElseThrow(() -> new RecursoNoEncontradoException("Restaurante no encontrado."));
        if (!restaurante.getIdPropietario().equals(idPropietarioAutenticado)) {
            throw new AccesoDenegadoException("No puedes modificar platos de un restaurante que no te pertenece.");
        }
    }
}
