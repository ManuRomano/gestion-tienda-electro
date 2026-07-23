package com.tienda.demo.pedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.demo.pedidos.model.Pedido;

/**
 * Repositorio de acceso a datos de {@link Pedido}.
 *
 * <p>Al guardar un pedido, gracias a la cascada de la entidad, se guardan
 * también sus líneas en la misma operación.</p>
 */
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
}
