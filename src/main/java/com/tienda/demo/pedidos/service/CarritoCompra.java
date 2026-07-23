package com.tienda.demo.pedidos.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.tienda.demo.pedidos.dto.LineaPedidoRequest;

import lombok.Getter;
import lombok.Setter;

/**
 * Compra que el empleado está montando ahora mismo (el "carrito").
 *
 * <p>Guarda temporalmente los productos elegidos y el cliente mientras se
 * recorren los pasos del asistente. Es {@code @SessionScope} porque el proceso
 * abarca varias peticiones HTTP y hay que recordar lo acumulado entre ellas;
 * cada navegador tiene su propio carrito.</p>
 *
 * <p>Ojo: esto es estado de <b>interfaz</b>, no de negocio. Nada se persiste
 * hasta que se confirma y {@code PedidoService} crea el pedido de verdad.</p>
 */
@Component
@SessionScope
public class CarritoCompra implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<LineaPedidoRequest> lineas = new ArrayList<>();

    /** Cliente elegido en el paso 2; {@code null} hasta entonces. */
    @Getter
    @Setter
    private Long clienteId;

    /**
     * Añade unidades de un producto al carrito.
     *
     * <p>Si el producto ya estaba, suma la cantidad a la línea existente en
     * lugar de crear una duplicada.</p>
     *
     * @param productoId producto que se añade.
     * @param cantidad   unidades a añadir.
     */
    public void agregar(Long productoId, int cantidad) {
        for (LineaPedidoRequest linea : lineas) {
            if (linea.getProductoId().equals(productoId)) {
                linea.setCantidad(linea.getCantidad() + cantidad);
                return;
            }
        }
        LineaPedidoRequest nueva = new LineaPedidoRequest();
        nueva.setProductoId(productoId);
        nueva.setCantidad(cantidad);
        lineas.add(nueva);
    }

    /**
     * Devuelve cuántas unidades de un producto hay ya en el carrito.
     *
     * <p>Sirve para validar el stock teniendo en cuenta lo ya reservado: si hay
     * 3 unidades en el carrito y se piden 2 más, hay que comprobar que existan
     * 5, no 2.</p>
     *
     * @param productoId producto a consultar.
     * @return unidades acumuladas, o 0 si no está en el carrito.
     */
    public int cantidadDe(Long productoId) {
        return lineas.stream()
                .filter(linea -> linea.getProductoId().equals(productoId))
                .mapToInt(LineaPedidoRequest::getCantidad)
                .sum();
    }

    /**
     * Quita del carrito la línea que ocupa la posición indicada.
     *
     * @param indice posición de la línea (la que muestra la tabla).
     */
    public void eliminar(int indice) {
        if (indice >= 0 && indice < lineas.size()) {
            lineas.remove(indice);
        }
    }

    /**
     * Vacía por completo el carrito y el cliente elegido.
     *
     * <p>Se llama al confirmar la compra (ya está persistida) o si el empleado
     * decide empezar de cero.</p>
     */
    public void vaciar() {
        lineas.clear();
        clienteId = null;
    }

    /**
     * Líneas acumuladas, en solo lectura.
     *
     * @return vista inmodificable de las líneas, para que nadie las altere sin
     *         pasar por los métodos de esta clase.
     */
    public List<LineaPedidoRequest> getLineas() {
        return Collections.unmodifiableList(lineas);
    }

    /**
     * Indica si todavía no se ha añadido ningún producto.
     *
     * @return {@code true} si el carrito está vacío.
     */
    public boolean estaVacio() {
        return lineas.isEmpty();
    }

    /**
     * Indica si ya se ha elegido el cliente de la compra.
     *
     * @return {@code true} si hay cliente seleccionado.
     */
    public boolean tieneCliente() {
        return clienteId != null;
    }
}
