package com.plazoleta.plazoleta.infrastructure.output.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

@FeignClient(name = "ms-mensajeria", url = "${microservices.mensajeria.url}")
public interface IMensajeriaFeignClient {
    @PostMapping("/mensajeria/sms")
    void enviarSms(@RequestBody Map<String, String> payload);
}
