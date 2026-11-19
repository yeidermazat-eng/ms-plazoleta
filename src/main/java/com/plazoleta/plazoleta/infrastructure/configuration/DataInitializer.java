package com.plazoleta.plazoleta.infrastructure.configuration;

import com.plazoleta.plazoleta.infrastructure.output.jpa.entity.CategoriaEntity;
import com.plazoleta.plazoleta.infrastructure.output.jpa.repository.ICategoriaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ICategoriaRepository categoriaRepository;

    public DataInitializer(ICategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public void run(String... args) {
        List<String> categorias = List.of("Entradas", "Platos Fuertes", "Bebidas", "Postres");
        for (String c : categorias) {
            categoriaRepository.findByNombre(c).orElseGet(() ->
                    categoriaRepository.save(new CategoriaEntity(null, c, "Categoría " + c)));
        }
    }
}
