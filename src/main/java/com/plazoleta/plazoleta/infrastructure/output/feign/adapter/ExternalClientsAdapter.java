package com.plazoleta.plazoleta.infrastructure.output.feign.adapter;

import com.plazoleta.plazoleta.domain.exception.RecursoNoEncontradoException;
import com.plazoleta.plazoleta.domain.spi.IMensajeriaClientPort;
import com.plazoleta.plazoleta.domain.spi.ITrazabilidadClientPort;
import com.plazoleta.plazoleta.domain.spi.IUsuarioClientPort;
import com.plazoleta.plazoleta.infrastructure.output.feign.client.IMensajeriaFeignClient;
import com.plazoleta.plazoleta.infrastructure.output.feign.client.ITrazabilidadFeignClient;
import com.plazoleta.plazoleta.infrastructure.output.feign.client.IUsuarioFeignClient;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExternalClientsAdapter implements IUsuarioClientPort, IMensajeriaClientPort, ITrazabilidadClientPort {

    private final IUsuarioFeignClient usuarioFeignClient;
    private final IMensajeriaFeignClient mensajeriaFeignClient;
    private final ITrazabilidadFeignClient trazabilidadFeignClient;

    public ExternalClientsAdapter(IUsuarioFeignClient usuarioFeignClient,
                                  IMensajeriaFeignClient mensajeriaFeignClient,
                                  ITrazabilidadFeignClient trazabilidadFeignClient) {
        this.usuarioFeignClient = usuarioFeignClient;
        this.mensajeriaFeignClient = mensajeriaFeignClient;
        this.trazabilidadFeignClient = trazabilidadFeignClient;
    }

    @Override
    public String obtenerRolUsuario(Long idUsuario) {
        try {
            Map<String, Object> user = usuarioFeignClient.obtenerUsuarioPorId(idUsuario);
            return user != null ? String.valueOf(user.get("rol")) : null;
        } catch (Exception ex) {
            throw new RecursoNoEncontradoException("No se encontró el usuario con id: " + idUsuario);
        }
    }

    @Override
    public String obtenerCelularUsuario(Long idUsuario) {
        try {
            Map<String, Object> user = usuarioFeignClient.obtenerUsuarioPorId(idUsuario);
            return user != null ? String.valueOf(user.get("celular")) : null;
        } catch (Exception ex) {
            throw new RecursoNoEncontradoException("No se encontró el celular del cliente con id: " + idUsuario);
        }
    }

    @Override
    public void enviarSms(String telefonoDestino, String mensaje) {
        mensajeriaFeignClient.enviarSms(Map.of(
                "telefonoDestino", telefonoDestino,
                "mensaje", mensaje
        ));
    }

    @Override
    public void registrarCambioEstado(Long idPedido, Long idCliente, String correoCliente,
                                      Long idRestaurante, Long idEmpleado, String correoEmpleado,
                                      String estadoAnterior, String estadoNuevo, LocalDateTime fecha) {
        Map<String, Object> body = new HashMap<>();
        body.put("idPedido", idPedido);
        body.put("idCliente", idCliente);
        body.put("correoCliente", correoCliente);
        body.put("idRestaurante", idRestaurante);
        body.put("idEmpleado", idEmpleado);
        body.put("correoEmpleado", correoEmpleado);
        body.put("estadoAnterior", estadoAnterior);
        body.put("estadoNuevo", estadoNuevo);
        body.put("fecha", fecha);
        trazabilidadFeignClient.registrarTrazabilidad(body);
    }
}
