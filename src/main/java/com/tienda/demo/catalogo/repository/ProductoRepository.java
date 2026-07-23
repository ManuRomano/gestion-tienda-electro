package com.tienda.demo.catalogo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.demo.catalogo.model.Producto;

/**
 * Repositorio de acceso a datos de {@link Producto}.
 *
 * <p>Hereda de {@link JpaRepository} las operaciones CRUD básicas. Cuando se
 * necesiten consultas propias (por ejemplo, productos disponibles) se añadirán
 * aquí como métodos derivados del nombre o con {@code @Query}.</p>
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
