package com.plazoleta.plazoleta.infrastructure.input.rest;

import com.plazoleta.plazoleta.application.dto.request.CrearRestauranteRequestDto;
import com.plazoleta.plazoleta.application.dto.response.RestauranteResumenResponseDto;
import com.plazoleta.plazoleta.application.handler.IPlazoletaHandler;
import com.plazoleta.plazoleta.domain.model.PaginacionResultado;
import com.plazoleta.plazoleta.domain.model.Restaurante;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/restaurantes")
@Tag(name = "Restaurantes", description = "Creación (HU 2) y listado paginado (HU 9) de restaurantes")
public class RestauranteRestController {

    private final IPlazoletaHandler handler;

    public RestauranteRestController(IPlazoletaHandler handler) {
        this.handler = handler;
    }

    @Operation(summary = "Crear restaurante (HU 2)", description = "Solo ejecutable por ADMINISTRADOR")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<Restaurante> crear(@Valid @RequestBody CrearRestauranteRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(handler.crearRestaurante(dto));
    }

    @Operation(summary = "Listar restaurantes alfabéticamente y paginados (HU 9)", description = "Disponible para CLIENTE")
    @PreAuthorize("hasRole('CLIENTE')")
    @GetMapping
    public ResponseEntity<PaginacionResultado<RestauranteResumenResponseDto>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(handler.listarRestaurantes(page, size));
    }
}
