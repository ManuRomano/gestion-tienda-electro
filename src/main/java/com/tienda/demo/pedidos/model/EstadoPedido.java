package com.tienda.demo.pedidos.model;

/**
 * Estados por los que pasa un pedido a lo largo de su ciclo de vida.
 *
 * <p>Flujo normal: {@code CREADO -> PAGADO -> ENVIADO -> ENTREGADO}. En
 * cualquier momento previo a la entrega puede pasar a {@code CANCELADO}.</p>
 */
public enum EstadoPedido {
    CREADO,
    PAGADO,
    ENVIADO,
    ENTREGADO,
    CANCELADO
}
