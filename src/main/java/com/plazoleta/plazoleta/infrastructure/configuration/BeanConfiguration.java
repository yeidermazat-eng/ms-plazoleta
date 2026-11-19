package com.plazoleta.plazoleta.infrastructure.configuration;

import com.plazoleta.plazoleta.domain.api.*;
import com.plazoleta.plazoleta.domain.spi.*;
import com.plazoleta.plazoleta.domain.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public IRestauranteServicePort restauranteServicePort(IRestaurantePersistencePort restaurantePersistencePort,
                                                          IUsuarioClientPort usuarioClientPort) {
        return new RestauranteUseCase(restaurantePersistencePort, usuarioClientPort);
    }

    @Bean
    public IPlatoServicePort platoServicePort(IPlatoPersistencePort platoPersistencePort,
                                              IRestaurantePersistencePort restaurantePersistencePort,
                                              ICategoriaPersistencePort categoriaPersistencePort) {
        return new PlatoUseCase(platoPersistencePort, restaurantePersistencePort, categoriaPersistencePort);
    }

    @Bean
    public IPedidoServicePort pedidoServicePort(IPedidoPersistencePort pedidoPersistencePort,
                                                IPlatoPersistencePort platoPersistencePort,
                                                IRestaurantePersistencePort restaurantePersistencePort,
                                                IUsuarioClientPort usuarioClientPort,
                                                IMensajeriaClientPort mensajeriaClientPort,
                                                ITrazabilidadClientPort trazabilidadClientPort) {
        return new PedidoUseCase(pedidoPersistencePort, platoPersistencePort, restaurantePersistencePort,
                usuarioClientPort, mensajeriaClientPort, trazabilidadClientPort);
    }
}
