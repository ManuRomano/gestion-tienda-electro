package com.tienda.demo.pedidos.dto;

import java.math.BigDecimal;

import com.tienda.demo.catalogo.model.Producto;

/**
 * Línea del carrito ya resuelta para poder pintarla en pantalla.
 *
 * <p>El carrito solo guarda {@code productoId} y cantidad; para mostrar la
 * tabla hace falta además el producto completo (nombre, precio) y el importe.
 * Este record junta esas tres cosas.</p>
 *
 * <p>Es un objeto de <b>solo lectura para la vista</b>: no se persiste ni
 * sustituye a {@code LineaPedido}, que es la entidad real.</p>
 *
 * @param producto producto elegido, con su nombre y precio actuales.
 * @param cantidad unidades solicitadas.
 * @param subtotal importe de la línea (precio × cantidad).
 */
public record LineaCarrito(Producto producto, int cantidad, BigDecimal subtotal) {
}
