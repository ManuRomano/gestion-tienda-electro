package com.tienda.demo.catalogo.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Electrodoméstico del catálogo que se puede vender.
 *
 * <p>El {@code precio} se modela con {@link BigDecimal} y no con {@code double}
 * porque los tipos en coma flotante arrastran errores de redondeo inaceptables
 * en dinero. El {@code stock} es la existencia disponible, y {@code activo}
 * permite retirar un producto de la venta sin borrarlo (baja lógica), de modo
 * que los pedidos históricos que lo referencian siguen siendo válidos.</p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    private BigDecimal precio;

    private int stock;

    private boolean activo;
}
