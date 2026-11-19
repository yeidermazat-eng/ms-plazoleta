package com.plazoleta.plazoleta.domain.spi;

public interface IMensajeriaClientPort {
    void enviarSms(String telefonoDestino, String mensaje);
}
