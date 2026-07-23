package com.tienda.demo.clientes.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.demo.clientes.model.Cliente;

/**
 * Repositorio de acceso a datos de {@link Cliente}.
 *
 * <p>Al extender {@link JpaRepository} hereda automáticamente las operaciones
 * CRUD (guardar, buscar por id, listar, borrar) sin escribir SQL: Spring Data
 * genera la implementación en tiempo de arranque. El segundo parámetro
 * ({@code Long}) es el tipo de la clave primaria de la entidad.</p>
 */
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
