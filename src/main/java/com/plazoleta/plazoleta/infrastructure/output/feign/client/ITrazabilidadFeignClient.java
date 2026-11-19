package com.plazoleta.plazoleta.infrastructure.output.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "ms-trazabilidad", url = "${microservices.trazabilidad.url}")
public interface ITrazabilidadFeignClient {
    @PostMapping("/trazabilidad")
    void registrarTrazabilidad(@RequestBody Map<String, Object> payload);
}
