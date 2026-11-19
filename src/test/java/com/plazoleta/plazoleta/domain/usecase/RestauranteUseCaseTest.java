package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.exception.DatosInvalidosException;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import com.plazoleta.plazoleta.domain.spi.IRestaurantePersistencePort;
import com.plazoleta.plazoleta.domain.spi.IUsuarioClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestauranteUseCaseTest {

    @Mock
    private IRestaurantePersistencePort restaurantePersistencePort;
    @Mock
    private IUsuarioClientPort usuarioClientPort;

    @InjectMocks
    private RestauranteUseCase restauranteUseCase;

    private Restaurante restaurante;

    @BeforeEach
    void setUp() {
        restaurante = new Restaurante();
        restaurante.setNombre("Asados 123");
        restaurante.setNit("900123456");
        restaurante.setDireccion("Calle 10 # 20-30");
        restaurante.setTelefono("+573001234567");
        restaurante.setUrlLogo("https://logo.com/asados.png");
        restaurante.setIdPropietario(2L);
    }

    @Test
    void crearRestaurante_Exitoso() {
        when(restaurantePersistencePort.existePorNit("900123456")).thenReturn(false);
        when(usuarioClientPort.obtenerRolUsuario(2L)).thenReturn("PROPIETARIO");
        when(restaurantePersistencePort.guardar(any())).thenReturn(restaurante);

        Restaurante res = restauranteUseCase.crearRestaurante(restaurante);
        assertEquals("Asados 123", res.getNombre());
    }

    @Test
    void crearRestaurante_NombreSoloNumeros_LanzaExcepcion() {
        restaurante.setNombre("123456");
        assertThrows(DatosInvalidosException.class, () -> restauranteUseCase.crearRestaurante(restaurante));
    }

    @Test
    void crearRestaurante_UsuarioNoEsPropietario_LanzaExcepcion() {
        when(restaurantePersistencePort.existePorNit("900123456")).thenReturn(false);
        when(usuarioClientPort.obtenerRolUsuario(2L)).thenReturn("CLIENTE");

        assertThrows(DatosInvalidosException.class, () -> restauranteUseCase.crearRestaurante(restaurante));
    }
}
