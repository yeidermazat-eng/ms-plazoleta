package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.exception.AccesoDenegadoException;
import com.plazoleta.plazoleta.domain.model.Categoria;
import com.plazoleta.plazoleta.domain.model.Plato;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.spi.ICategoriaPersistencePort;
import com.plazoleta.plazoleta.domain.spi.IPlatoPersistencePort;
import com.plazoleta.plazoleta.domain.spi.IRestaurantePersistencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlatoUseCaseTest {

    @Mock
    private IPlatoPersistencePort platoPersistencePort;
    @Mock
    private IRestaurantePersistencePort restaurantePersistencePort;
    @Mock
    private ICategoriaPersistencePort categoriaPersistencePort;

    @InjectMocks
    private PlatoUseCase platoUseCase;

    @Test
    void crearPlato_Exitoso_ActivoPorDefecto() {
        Restaurante rest = new Restaurante();
        rest.setId(1L);
        rest.setIdPropietario(10L);

        Plato plato = new Plato();
        plato.setNombre("Bandeja Paisa");
        plato.setPrecio(32000);
        plato.setDescripcion("Plato típico completo");
        plato.setUrlImagen("https://img.com/bandeja.png");
        plato.setCategoria(new Categoria(2L, null, null));
        plato.setIdRestaurante(1L);

        when(restaurantePersistencePort.buscarPorId(1L)).thenReturn(Optional.of(rest));
        when(categoriaPersistencePort.buscarPorId(2L)).thenReturn(Optional.of(new Categoria(2L, "Platos Fuertes", "Desc")));
        when(platoPersistencePort.guardar(any())).thenAnswer(i -> i.getArgument(0));

        Plato creado = platoUseCase.crearPlato(plato, 10L);
        assertTrue(creado.getActivo());
        assertEquals(32000, creado.getPrecio());
    }

    @Test
    void modificarPlato_OtroPropietario_LanzaAccesoDenegado() {
        Restaurante rest = new Restaurante();
        rest.setId(1L);
        rest.setIdPropietario(10L);

        Plato plato = new Plato();
        plato.setId(5L);
        plato.setIdRestaurante(1L);

        when(platoPersistencePort.buscarPorId(5L)).thenReturn(Optional.of(plato));
        when(restaurantePersistencePort.buscarPorId(1L)).thenReturn(Optional.of(rest));

        assertThrows(AccesoDenegadoException.class,
                () -> platoUseCase.modificarPlato(5L, 35000, "Nueva desc", 99L));
    }
}
