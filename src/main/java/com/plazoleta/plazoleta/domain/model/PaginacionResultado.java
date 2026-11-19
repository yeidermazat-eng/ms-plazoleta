package com.plazoleta.plazoleta.domain.model;

import java.util.List;

public class PaginacionResultado<T> {
    private List<T> contenido;
    private int paginaActual;
    private int tamanoPagina;
    private long totalElementos;
    private int totalPaginas;

    public PaginacionResultado(List<T> contenido, int paginaActual, int tamanoPagina, long totalElementos, int totalPaginas) {
        this.contenido = contenido;
        this.paginaActual = paginaActual;
        this.tamanoPagina = tamanoPagina;
        this.totalElementos = totalElementos;
        this.totalPaginas = totalPaginas;
    }

    public List<T> getContenido() { return contenido; }
    public int getPaginaActual() { return paginaActual; }
    public int getTamanoPagina() { return tamanoPagina; }
    public long getTotalElementos() { return totalElementos; }
    public int getTotalPaginas() { return totalPaginas; }
}
