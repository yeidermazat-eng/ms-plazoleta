package com.plazoleta.plazoleta.application.dto.request;

import jakarta.validation.constraints.*;

public class ModificarPlatoRequestDto {
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser un entero positivo mayor a 0")
    private Integer precio;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    public Integer getPrecio() { return precio; }
    public void setPrecio(Integer precio) { this.precio = precio; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
