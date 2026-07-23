package com.tienda.demo.pedidos.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tienda.demo.clientes.model.Cliente;
import com.tienda.demo.empleados.model.Empleado;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Pedido (compra) realizado por un cliente y gestionado por un empleado.
 *
 * <p>Es la cabecera de la venta: el detalle (qué productos y cuántos) vive en
 * sus {@link LineaPedido}. Referencia al {@link Cliente} y al {@link Empleado}
 * con {@code @ManyToOne} (muchos pedidos por cliente y por empleado). El
 * {@code total} se guarda calculado para no tener que recomputarlo cada vez que
 * se consulta el pedido.</p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    private Empleado empleado;

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoPedido estado;

    private BigDecimal total;

    /**
     * Líneas de detalle del pedido.
     *
     * <p>{@code cascade = ALL} y {@code orphanRemoval = true} hacen que las
     * líneas se guarden y borren junto con el pedido: no tienen sentido por sí
     * solas. {@code mappedBy = "pedido"} indica que la columna de la relación
     * (la clave foránea) la gestiona el campo {@code pedido} de
     * {@link LineaPedido}.</p>
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LineaPedido> lineas = new ArrayList<>();

    /**
     * Añade una línea al pedido manteniendo la relación bidireccional.
     *
     * <p>Además de agregarla a la lista, fija en la línea su pedido padre. Si
     * solo se hiciera una de las dos cosas, JPA no guardaría bien la clave
     * foránea.</p>
     *
     * @param linea línea de detalle a incorporar.
     */
    public void addLinea(LineaPedido linea) {
        lineas.add(linea);
        linea.setPedido(this);
    }
}
