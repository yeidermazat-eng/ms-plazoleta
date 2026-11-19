package com.plazoleta.plazoleta.infrastructure.output.jpa.adapter;

import com.plazoleta.plazoleta.domain.model.*;
import com.plazoleta.plazoleta.domain.spi.IPedidoPersistencePort;
import com.plazoleta.plazoleta.infrastructure.output.jpa.entity.PedidoEntity;
import com.plazoleta.plazoleta.infrastructure.output.jpa.entity.PedidoPlatoEntity;
import com.plazoleta.plazoleta.infrastructure.output.jpa.repository.IPedidoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class PedidoJpaAdapter implements IPedidoPersistencePort {

    private final IPedidoRepository repository;

    public PedidoJpaAdapter(IPedidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pedido guardar(Pedido p) {
        PedidoEntity e = new PedidoEntity();
        e.setId(p.getId());
        e.setIdCliente(p.getIdCliente());
        e.setFecha(p.getFecha());
        e.setEstado(p.getEstado());
        e.setIdChef(p.getIdChef());
        e.setIdRestaurante(p.getIdRestaurante());
        e.setPinSeguridad(p.getPinSeguridad());

        if (p.getPlatos() != null) {
            List<PedidoPlatoEntity> items = p.getPlatos().stream().map(item -> {
                PedidoPlatoEntity pe = new PedidoPlatoEntity();
                pe.setPedido(e);
                pe.setIdPlato(item.getIdPlato());
                pe.setNombrePlato(item.getNombrePlato());
                pe.setCantidad(item.getCantidad());
                pe.setPrecioUnitario(item.getPrecioUnitario());
                return pe;
            }).collect(Collectors.toList());
            e.setPlatos(items);
        }
        return toDomain(repository.save(e));
    }

    @Override
    public Optional<Pedido> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existePedidoActivoPorCliente(Long idCliente, List<EstadoPedido> estadosActivos) {
        return repository.existsByIdClienteAndEstadoIn(idCliente, estadosActivos);
    }

    @Override
    public PaginacionResultado<Pedido> listarPorRestauranteYEstado(Long idRestaurante, EstadoPedido estado, int page, int size) {
        Page<PedidoEntity> p = repository.findByIdRestauranteAndEstado(idRestaurante, estado, PageRequest.of(page, size));
        return new PaginacionResultado<>(
                p.getContent().stream().map(this::toDomain).collect(Collectors.toList()),
                p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()
        );
    }

    private Pedido toDomain(PedidoEntity e) {
        Pedido p = new Pedido();
        p.setId(e.getId());
        p.setIdCliente(e.getIdCliente());
        p.setFecha(e.getFecha());
        p.setEstado(e.getEstado());
        p.setIdChef(e.getIdChef());
        p.setIdRestaurante(e.getIdRestaurante());
        p.setPinSeguridad(e.getPinSeguridad());
        if (e.getPlatos() != null) {
            p.setPlatos(e.getPlatos().stream().map(pe -> {
                PedidoPlato pp = new PedidoPlato(pe.getIdPlato(), pe.getCantidad());
                pp.setNombrePlato(pe.getNombrePlato());
                pp.setPrecioUnitario(pe.getPrecioUnitario());
                return pp;
            }).collect(Collectors.toList()));
        }
        return p;
    }
}
