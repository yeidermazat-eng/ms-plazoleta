package com.plazoleta.plazoleta.infrastructure.input.rest;

import com.plazoleta.plazoleta.application.dto.request.CrearPlatoRequestDto;
import com.plazoleta.plazoleta.application.dto.request.ModificarPlatoRequestDto;
import com.plazoleta.plazoleta.application.dto.response.PlatoResponseDto;
import com.plazoleta.plazoleta.application.handler.IPlazoletaHandler;
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
@RequestMapping("/platos")
@Tag(name = "Platos", description = "Gestión de menú por Propietario (HU 3, HU 4, HU 7) y consulta por Cliente (HU 10)")
public class PlatoRestController {

    private final IPlazoletaHandler handler;

    public PlatoRestController(IPlazoletaHandler handler) {
        this.handler = handler;
    }

    @Operation(summary = "Crear plato en el menú (HU 3)", description = "Solo ejecutable por el PROPIETARIO del restaurante")
    @PreAuthorize("hasRole('PROPIETARIO')")
    @PostMapping
    public ResponseEntity<PlatoResponseDto> crear(@Valid @RequestBody CrearPlatoRequestDto dto, Authentication auth) {
        Long idPropietario = extraerIdUsuario(auth);
        return ResponseEntity.status(HttpStatus.CREATED).body(handler.crearPlato(dto, idPropietario));
    }

    @Operation(summary = "Modificar precio y descripción del plato (HU 4)", description = "Solo ejecutable por el PROPIETARIO del restaurante")
    @PreAuthorize("hasRole('PROPIETARIO')")
    @PutMapping("/{id}")
    public ResponseEntity<PlatoResponseDto> modificar(@PathVariable Long id,
                                                      @Valid @RequestBody ModificarPlatoRequestDto dto,
                                                      Authentication auth) {
        Long idPropietario = extraerIdUsuario(auth);
        return ResponseEntity.ok(handler.modificarPlato(id, dto, idPropietario));
    }

    @Operation(summary = "Habilitar o deshabilitar plato (HU 7)", description = "Solo ejecutable por el PROPIETARIO del restaurante")
    @PreAuthorize("hasRole('PROPIETARIO')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<PlatoResponseDto> cambiarEstado(@PathVariable Long id,
                                                          @RequestParam Boolean activo,
                                                          Authentication auth) {
        Long idPropietario = extraerIdUsuario(auth);
        return ResponseEntity.ok(handler.cambiarEstadoPlato(id, activo, idPropietario));
    }

    @Operation(summary = "Listar platos de un restaurante con paginación y filtro opcional por categoría (HU 10)")
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping("/restaurante/{idRestaurante}")
    public ResponseEntity<PaginacionResultado<PlatoResponseDto>> listarPorRestaurante(
            @PathVariable Long idRestaurante,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(handler.listarPlatos(idRestaurante, idCategoria, page, size));
    }

    private Long extraerIdUsuario(Authentication auth) {
        Claims claims = (Claims) auth.getCredentials();
        return claims.get("id", Number.class).longValue();
    }
}
