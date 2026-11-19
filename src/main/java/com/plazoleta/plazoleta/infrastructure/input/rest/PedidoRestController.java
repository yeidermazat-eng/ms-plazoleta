package com.plazoleta.plazoleta.infrastructure.input.rest;

import com.plazoleta.plazoleta.application.dto.request.CrearPedidoRequestDto;
import com.plazoleta.plazoleta.application.dto.response.PedidoResponseDto;
import com.plazoleta.plazoleta.application.handler.IPlazoletaHandler;
import com.plazoleta.plazoleta.domain.model.EstadoPedido;
import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
@Tag(name = "Pedidos", description = "Flujo completo de pedidos (HU 11, HU 12, HU 13, HU 14, HU 15, HU 16)")
public class PedidoRestController {

    private final IPlazoletaHandler handler;

    public PedidoRestController(IPlazoletaHandler handler) {
        this.handler = handler;
    }

    @Operation(summary = "Realizar pedido (HU 11)", description = "Cliente solicita platos de un mismo restaurante")
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<PedidoResponseDto> realizarPedido(@Valid @RequestBody CrearPedidoRequestDto dto, Authentication auth) {
        Claims claims = (Claims) auth.getCredentials();
        Long idCliente = claims.get("id", Number.class).longValue();
        String correo = claims.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED).body(handler.realizarPedido(dto, idCliente, correo));
    }

    @Operation(summary = "Listar pedidos por estado paginados (HU 12)", description = "Empleado visualiza pedidos de su restaurante")
    @PreAuthorize("hasRole('EMPLEADO')")
    @GetMapping
    public ResponseEntity<PaginacionResultado<PedidoResponseDto>> listarPorEstado(
            @RequestParam EstadoPedido estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication auth) {
        Long idRestaurante = extraerIdRestaurante(auth);
        return ResponseEntity.ok(handler.listarPedidosPorEstado(estado, idRestaurante, page, size));
    }

    @Operation(summary = "Asignarse a pedido y cambiar estado a EN_PREPARACION (HU 13)")
    @PreAuthorize("hasRole('EMPLEADO')")
    @PatchMapping("/{id}/asignar")
    public ResponseEntity<PedidoResponseDto> asignarse(@PathVariable Long id, Authentication auth) {
        Claims claims = (Claims) auth.getCredentials();
        Long idEmpleado = claims.get("id", Number.class).longValue();
        Long idRestaurante = extraerIdRestaurante(auth);
        return ResponseEntity.ok(handler.asignarseAPedido(id, idEmpleado, claims.getSubject(), idRestaurante));
    }

    @Operation(summary = "Notificar pedido LISTO y enviar SMS con PIN al cliente (HU 14)")
    @PreAuthorize("hasRole('EMPLEADO')")
    @PatchMapping("/{id}/listo")
    public ResponseEntity<PedidoResponseDto> marcarListo(@PathVariable Long id, Authentication auth) {
        Claims claims = (Claims) auth.getCredentials();
        Long idEmpleado = claims.get("id", Number.class).longValue();
        Long idRestaurante = extraerIdRestaurante(auth);
        return ResponseEntity.ok(handler.marcarPedidoListo(id, idEmpleado, claims.getSubject(), idRestaurante));
    }

    @Operation(summary = "Entregar pedido validando PIN de seguridad (HU 15)")
    @PreAuthorize("hasRole('EMPLEADO')")
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<PedidoResponseDto> entregar(@PathVariable Long id,
                                                      @RequestParam String pin,
                                                      Authentication auth) {
        Claims claims = (Claims) auth.getCredentials();
        Long idEmpleado = claims.get("id", Number.class).longValue();
        Long idRestaurante = extraerIdRestaurante(auth);
        return ResponseEntity.ok(handler.entregarPedido(id, pin, idEmpleado, claims.getSubject(), idRestaurante));
    }

    @Operation(summary = "Cancelar pedido en estado PENDIENTE (HU 16)")
    @PreAuthorize("hasRole('CLIENTE')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponseDto> cancelar(@PathVariable Long id, Authentication auth) {
        Claims claims = (Claims) auth.getCredentials();
        Long idCliente = claims.get("id", Number.class).longValue();
        return ResponseEntity.ok(handler.cancelarPedido(id, idCliente, claims.getSubject()));
    }

    private Long extraerIdRestaurante(Authentication auth) {
        Claims claims = (Claims) auth.getCredentials();
        Number idRest = claims.get("idRestaurante", Number.class);
        return idRest != null ? idRest.longValue() : null;
    }
}
