package com.plazoleta.plazoleta.application.dto.response;

public class RestauranteResumenResponseDto {
    private String nombre;
    private String urlLogo;

    public RestauranteResumenResponseDto(String nombre, String urlLogo) {
        this.nombre = nombre;
        this.urlLogo = urlLogo;
    }

    public String getNombre() { return nombre; }
    public String getUrlLogo() { return urlLogo; }
}
