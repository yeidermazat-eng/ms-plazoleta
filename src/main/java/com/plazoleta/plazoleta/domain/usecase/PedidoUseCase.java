package com.plazoleta.plazoleta.domain.usecase;

import com.plazoleta.plazoleta.domain.api.IPedidoServicePort;
import com.plazoleta.plazoleta.domain.exception.*;
import com.plazoleta.plazoleta.domain.model.*;
import com.plazoleta.plazoleta.domain.spi.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoUseCase implements IPedidoServicePort {

    private static final List<EstadoPedido> ESTADOS_ACTIVOS = List.of(
            EstadoPedido.PENDIENTE,
            EstadoPedido.EN_PREPARACION,
            EstadoPedido.LISTO
    );

    private final IPedidoPersistencePort pedidoPersistencePort;
    private final IPlatoPersistencePort platoPersistencePort;
    private final IRestaurantePersistencePort restaurantePersistencePort;
    private final IUsuarioClientPort usuarioClientPort;
    private final IMensajeriaClientPort mensajeriaClientPort;
    private final ITrazabilidadClientPort trazabilidadClientPort;
    private final SecureRandom secureRandom = new SecureRandom();

    public PedidoUseCase(IPedidoPersistencePort pedidoPersistencePort,
                         IPlatoPersistencePort platoPersistencePort,
                         IRestaurantePersistencePort restaurantePersistencePort,
                         IUsuarioClientPort usuarioClientPort,
                         IMensajeriaClientPort mensajeriaClientPort,
                         ITrazabilidadClientPort trazabilidadClientPort) {
        this.pedidoPersistencePort = pedidoPersistencePort;
        this.platoPersistencePort = platoPersistencePort;
        this.restaurantePersistencePort = restaurantePersistencePort;
        this.usuarioClientPort = usuarioClientPort;
        this.mensajeriaClientPort = mensajeriaClientPort;
        this.trazabilidadClientPort = trazabilidadClientPort;
    }

    @Override
    public Pedido realizarPedido(Pedido pedido, Long idClienteAutenticado, String correoCliente) {
        if (pedido.getPlatos() == null || pedido.getPlatos().isEmpty()) {
            throw new DatosInvalidosException("El pedido debe contener al menos un plato.");
        }
        if (pedidoPersistencePort.existePedidoActivoPorCliente(idClienteAutenticado, ESTADOS_ACTIVOS)) {
            throw new PedidoEnProcesoException("Ya tienes un pedido en proceso. Debes esperar a que sea entregado o cancelado.");
        }
        restaurantePersistencePort.buscarPorId(pedido.getIdRestaurante())
                .orElseThrow(() -> new RecursoNoEncontradoException("Restaurante no encontrado."));

        for (PedidoPlato item : pedido.getPlatos()) {
            if (item.getCantidad() == null || item.getCantidad() <= 0) {
                throw new DatosInvalidosException("La cantidad de cada plato debe ser mayor a 0.");
            }
            Plato plato = platoPersistencePort.buscarPorId(item.getIdPlato())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Plato no encontrado: " + item.getIdPlato()));
            if (!plato.getIdRestaurante().equals(pedido.getIdRestaurante())) {
                throw new DatosInvalidosException("Todos los platos del pedido deben pertenecer al mismo restaurante.");
            }
            if (!Boolean.TRUE.equals(plato.getActivo())) {
                throw new DatosInvalidosException("El plato '" + plato.getNombre() + "' no se encuentra disponible.");
            }
            item.setNombrePlato(plato.getNombre());
            item.setPrecioUnitario(plato.getPrecio());
        }

        LocalDateTime ahora = LocalDateTime.now();
        pedido.setIdCliente(idClienteAutenticado);
        pedido.setFecha(ahora);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        Pedido guardado = pedidoPersistencePort.guardar(pedido);

        trazabilidadClientPort.registrarCambioEstado(
                guardado.getId(), idClienteAutenticado, correoCliente,
                guardado.getIdRestaurante(), null, null,
                null, EstadoPedido.PENDIENTE.name(), ahora
        );
        return guardado;
    }

    @Override
    public PaginacionResultado<Pedido> listarPedidosPorEstado(EstadoPedido estado, Long idRestauranteEmpleado, int page, int size) {
        if (idRestauranteEmpleado == null) {
            throw new AccesoDenegadoException("El empleado no está asociado a ningún restaurante.");
        }
        return pedidoPersistencePort.listarPorRestauranteYEstado(idRestauranteEmpleado, estado, page, size);
    }

    @Override
    public Pedido asignarseAPedido(Long idPedido, Long idEmpleadoAutenticado, String correoEmpleado, Long idRestauranteEmpleado) {
        Pedido pedido = obtenerYValidarRestauranteEmpleado(idPedido, idRestauranteEmpleado);
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new EstadoPedidoInvalidoException("Solo se pueden aceptar pedidos en estado PENDIENTE.");
        }
        String estadoAnterior = pedido.getEstado().name();
        pedido.setIdChef(idEmpleadoAutenticado);
        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        Pedido actualizado = pedidoPersistencePort.guardar(pedido);

        trazabilidadClientPort.registrarCambioEstado(
                actualizado.getId(), actualizado.getIdCliente(), null,
                actualizado.getIdRestaurante(), idEmpleadoAutenticado, correoEmpleado,
                estadoAnterior, EstadoPedido.EN_PREPARACION.name(), LocalDateTime.now()
        );
        return actualizado;
    }

    @Override
    public Pedido marcarPedidoListo(Long idPedido, Long idEmpleadoAutenticado, String correoEmpleado, Long idRestauranteEmpleado) {
        Pedido pedido = obtenerYValidarRestauranteEmpleado(idPedido, idRestauranteEmpleado);
        if (pedido.getEstado() != EstadoPedido.EN_PREPARACION) {
            throw new EstadoPedidoInvalidoException("Solo los pedidos EN_PREPARACION pueden marcarse como LISTO.");
        }
        String estadoAnterior = pedido.getEstado().name();
        String pin = String.format("%06d", secureRandom.nextInt(1_000_000));
        pedido.setEstado(EstadoPedido.LISTO);
        pedido.setPinSeguridad(pin);
        Pedido actualizado = pedidoPersistencePort.guardar(pedido);

        String celularCliente = usuarioClientPort.obtenerCelularUsuario(pedido.getIdCliente());
        mensajeriaClientPort.enviarSms(
                celularCliente,
                "¡Tu pedido #" + pedido.getId() + " está LISTO! Reclámalo con el PIN de seguridad: " + pin
        );

        trazabilidadClientPort.registrarCambioEstado(
                actualizado.getId(), actualizado.getIdCliente(), null,
                actualizado.getIdRestaurante(), idEmpleadoAutenticado, correoEmpleado,
                estadoAnterior, EstadoPedido.LISTO.name(), LocalDateTime.now()
        );
        return actualizado;
    }

    @Override
    public Pedido entregarPedido(Long idPedido, String pinSeguridad, Long idEmpleadoAutenticado, String correoEmpleado, Long idRestauranteEmpleado) {
        Pedido pedido = obtenerYValidarRestauranteEmpleado(idPedido, idRestauranteEmpleado);
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new EstadoPedidoInvalidoException("Un pedido en estado ENTREGADO no puede cambiar de estado.");
        }
        if (pedido.getEstado() != EstadoPedido.LISTO) {
            throw new EstadoPedidoInvalidoException("Solo los pedidos en estado LISTO pueden pasar a ENTREGADO.");
        }
        if (pinSeguridad == null || !pinSeguridad.equals(pedido.getPinSeguridad())) {
            throw new PinInvalidoException("El PIN de seguridad ingresado es incorrecto.");
        }
        String estadoAnterior = pedido.getEstado().name();
        pedido.setEstado(EstadoPedido.ENTREGADO);
        Pedido actualizado = pedidoPersistencePort.guardar(pedido);

        trazabilidadClientPort.registrarCambioEstado(
                actualizado.getId(), actualizado.getIdCliente(), null,
                actualizado.getIdRestaurante(), idEmpleadoAutenticado, correoEmpleado,
                estadoAnterior, EstadoPedido.ENTREGADO.name(), LocalDateTime.now()
        );
        return actualizado;
    }

    @Override
    public Pedido cancelarPedido(Long idPedido, Long idClienteAutenticado, String correoCliente) {
        Pedido pedido = pedidoPersistencePort.buscarPorId(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado."));
        if (!pedido.getIdCliente().equals(idClienteAutenticado)) {
            throw new AccesoDenegadoException("No puedes cancelar un pedido que no te pertenece.");
        }
        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new EstadoPedidoInvalidoException("Lo sentimos, tu pedido ya está en preparación y no puede cancelarse");
        }
        pedido.setEstado(EstadoPedido.CANCELADO);
        Pedido actualizado = pedidoPersistencePort.guardar(pedido);

        trazabilidadClientPort.registrarCambioEstado(
                actualizado.getId(), idClienteAutenticado, correoCliente,
                actualizado.getIdRestaurante(), null, null,
                EstadoPedido.PENDIENTE.name(), EstadoPedido.CANCELADO.name(), LocalDateTime.now()
        );
        return actualizado;
    }

    private Pedido obtenerYValidarRestauranteEmpleado(Long idPedido, Long idRestauranteEmpleado) {
        Pedido pedido = pedidoPersistencePort.buscarPorId(idPedido)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado."));
        if (idRestauranteEmpleado == null || !pedido.getIdRestaurante().equals(idRestauranteEmpleado)) {
            throw new AccesoDenegadoException("No puedes gestionar pedidos de otro restaurante.");
        }
        return pedido;
    }
}
