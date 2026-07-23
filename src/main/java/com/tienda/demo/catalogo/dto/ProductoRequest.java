package com.tienda.demo.catalogo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO con los datos para dar de alta un producto del catálogo.
 *
 * <p>No incluye {@code activo}: un producto nuevo siempre entra activo, y esa
 * decisión la toma el servicio, no el formulario.</p>
 */
@Getter
@Setter
public class ProductoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El precio es obligatorio")
    @PositiveOrZero(message = "El precio no puede ser negativo")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
}
