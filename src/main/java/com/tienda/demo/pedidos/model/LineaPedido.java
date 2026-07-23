package com.tienda.demo.pedidos.model;

import java.math.BigDecimal;

import com.tienda.demo.catalogo.model.Producto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Línea de detalle de un {@link Pedido}: un producto y su cantidad.
 *
 * <p>Guarda el {@code precioUnitario} <b>copiado</b> del producto en el momento
 * de la compra, en lugar de leerlo del catálogo. Así, si mañana cambia el
 * precio del producto, los pedidos antiguos conservan el precio al que
 * realmente se vendieron.</p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Pedido pedido;

    @ManyToOne(optional = false)
    private Producto producto;

    private int cantidad;

    private BigDecimal precioUnitario;

    /**
     * Calcula el importe de la línea: precio unitario por cantidad.
     *
     * <p>Es un valor derivado, por eso no se almacena: se recalcula siempre a
     * partir de los otros dos campos y no puede quedar desincronizado.</p>
     *
     * @return subtotal de la línea.
     */
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
