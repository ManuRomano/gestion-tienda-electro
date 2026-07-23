package com.tienda.demo.pedidos.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO con los datos de una compra: quién compra, quién la gestiona y qué lleva.
 *
 * <p>{@code @Valid} sobre la lista propaga la validación a cada
 * {@link LineaPedidoRequest}, de modo que las reglas de cada línea (producto y
 * cantidad) también se comprueban.</p>
 */
@Getter
@Setter
public class PedidoRequest {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El empleado es obligatorio")
    private Long empleadoId;

    @NotEmpty(message = "El pedido debe tener al menos una línea")
    @Valid
    private List<LineaPedidoRequest> lineas = new ArrayList<>();
}
