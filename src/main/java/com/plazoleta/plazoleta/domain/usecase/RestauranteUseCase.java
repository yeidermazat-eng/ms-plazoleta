package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.api.IRestauranteServicePort;
import com.plazoleta.plazoleta.domain.exception.DatosInvalidosException;
import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.spi.IRestaurantePersistencePort;
import com.plazoleta.plazoleta.domain.spi.IUsuarioClientPort;

import java.util.regex.Pattern;

public class RestauranteUseCase implements IRestauranteServicePort {

    private static final Pattern SOLO_NUMEROS = Pattern.compile("^[0-9]+$");
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\+?[0-9]{1,12}$");

    private final IRestaurantePersistencePort restaurantePersistencePort;
    private final IUsuarioClientPort usuarioClientPort;

    public RestauranteUseCase(IRestaurantePersistencePort restaurantePersistencePort,
                              IUsuarioClientPort usuarioClientPort) {
        this.restaurantePersistencePort = restaurantePersistencePort;
        this.usuarioClientPort = usuarioClientPort;
    }

    @Override
    public Restaurante crearRestaurante(Restaurante r) {
        if (r == null || esVacio(r.getNombre()) || esVacio(r.getNit()) || esVacio(r.getDireccion())
                || esVacio(r.getTelefono()) || esVacio(r.getUrlLogo()) || r.getIdPropietario() == null) {
            throw new DatosInvalidosException("Todos los campos del restaurante son obligatorios.");
        }
        if (SOLO_NUMEROS.matcher(r.getNombre().trim()).matches()) {
            throw new DatosInvalidosException("El nombre del restaurante no puede contener únicamente números.");
        }
        if (!SOLO_NUMEROS.matcher(r.getNit()).matches()) {
            throw new DatosInvalidosException("El NIT debe ser únicamente numérico.");
        }
        if (r.getTelefono().length() > 13 || !TELEFONO_PATTERN.matcher(r.getTelefono()).matches()) {
            throw new DatosInvalidosException("El teléfono debe tener máximo 13 caracteres numéricos y opcionalmente + al inicio.");
        }
        if (restaurantePersistencePort.existePorNit(r.getNit())) {
            throw new DatosInvalidosException("Ya existe un restaurante registrado con el NIT: " + r.getNit());
        }
        String rolUsuario = usuarioClientPort.obtenerRolUsuario(r.getIdPropietario());
        if (!"PROPIETARIO".equals(rolUsuario)) {
            throw new DatosInvalidosException("El idPropietario proporcionado no corresponde a un usuario con rol PROPIETARIO.");
        }
        return restaurantePersistencePort.guardar(r);
    }

    @Override
    public PaginacionResultado<Restaurante> listarRestaurantes(int page, int size) {
        return restaurantePersistencePort.listarOrdenadosPorNombre(page, size);
    }

    private boolean esVacio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
