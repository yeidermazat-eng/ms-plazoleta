package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.exception.EstadoPedidoInvalidoException;
import com.plazoleta.plazoleta.domain.exception.PedidoEnProcesoException;
import com.plazoleta.plazoleta.domain.exception.PinInvalidoException;
import com.plazoleta.plazoleta.domain.model.*;
import com.plazoleta.plazoleta.domain.spi.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoUseCaseTest {

    @Mock private IPedidoPersistencePort pedidoPersistencePort;
    @Mock private IPlatoPersistencePort platoPersistencePort;
    @Mock private IRestaurantePersistencePort restaurantePersistencePort;
    @Mock private IUsuarioClientPort usuarioClientPort;
    @Mock private IMensajeriaClientPort mensajeriaClientPort;
    @Mock private ITrazabilidadClientPort trazabilidadClientPort;

    @InjectMocks
    private PedidoUseCase pedidoUseCase;

    @Test
    void realizarPedido_ClienteConPedidoEnProceso_LanzaExcepcion() {
        Pedido pedido = new Pedido();
        pedido.setIdRestaurante(1L);
        pedido.setPlatos(List.of(new PedidoPlato(10L, 2)));

        when(pedidoPersistencePort.existePedidoActivoPorCliente(eq(50L), any())).thenReturn(true);

        assertThrows(PedidoEnProcesoException.class,
                () -> pedidoUseCase.realizarPedido(pedido, 50L, "cliente@mail.com"));
    }

    @Test
    void flujoCompletoPedido_Pendiente_EnPreparacion_Listo_Entregado() {
        Pedido pedido = new Pedido();
        pedido.setId(100L);
        pedido.setIdCliente(50L);
        pedido.setIdRestaurante(1L);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        when(pedidoPersistencePort.buscarPorId(100L)).thenReturn(Optional.of(pedido));
        when(pedidoPersistencePort.guardar(any())).thenAnswer(i -> i.getArgument(0));
        when(usuarioClientPort.obtenerCelularUsuario(50L)).thenReturn("+573001112233");

        // HU 13: Asignarse
        Pedido enPrep = pedidoUseCase.asignarseAPedido(100L, 7L, "chef@mail.com", 1L);
        assertEquals(EstadoPedido.EN_PREPARACION, enPrep.getEstado());
        assertEquals(7L, enPrep.getIdChef());

        // HU 14: Marcar listo y enviar PIN por SMS
        Pedido listo = pedidoUseCase.marcarPedidoListo(100L, 7L, "chef@mail.com", 1L);
        assertEquals(EstadoPedido.LISTO, listo.getEstado());
        assertNotNull(listo.getPinSeguridad());
        assertEquals(6, listo.getPinSeguridad().length());
        verify(mensajeriaClientPort).enviarSms(eq("+573001112233"), contains(listo.getPinSeguridad()));

        // HU 15: Pin incorrecto falla
        assertThrows(PinInvalidoException.class,
                () -> pedidoUseCase.entregarPedido(100L, "000000", 7L, "chef@mail.com", 1L));

        // HU 15: Pin correcto entrega
        Pedido entregado = pedidoUseCase.entregarPedido(100L, listo.getPinSeguridad(), 7L, "chef@mail.com", 1L);
        assertEquals(EstadoPedido.ENTREGADO, entregado.getEstado());
    }

    @Test
    void cancelarPedido_NoEstaPendiente_RetornaMensajeExactoHU16() {
        Pedido pedido = new Pedido();
        pedido.setId(100L);
        pedido.setIdCliente(50L);
        pedido.setEstado(EstadoPedido.EN_PREPARACION);

        when(pedidoPersistencePort.buscarPorId(100L)).thenReturn(Optional.of(pedido));

        EstadoPedidoInvalidoException ex = assertThrows(EstadoPedidoInvalidoException.class,
                () -> pedidoUseCase.cancelarPedido(100L, 50L, "cliente@mail.com"));
        assertEquals("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse", ex.getMessage());
    }
}
