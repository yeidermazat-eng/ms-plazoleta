package com.plazoleta.plazoleta.infrastructure.output.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "ms-usuarios", url = "${microservices.usuarios.url}")
public interface IUsuarioFeignClient {
    @GetMapping("/usuarios/{id}")
    Map<String, Object> obtenerUsuarioPorId(@PathVariable("id") Long id);
}
