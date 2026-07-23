package com.tienda.demo.pedidos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * Una línea del formulario de compra: qué producto y cuántas unidades.
 *
 * <p>Solo viaja el {@code productoId}, no el producto entero ni su precio: el
 * servidor recupera el producto real y fija el precio, para que el cliente no
 * pueda manipularlo desde el formulario.</p>
 */
@Getter
@Setter
public class LineaPedidoRequest {

    @NotNull(message = "El producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor que cero")
    private Integer cantidad;
}
