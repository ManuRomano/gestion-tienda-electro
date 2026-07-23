package com.tienda.demo.catalogo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tienda.demo.catalogo.dto.ProductoRequest;
import com.tienda.demo.catalogo.model.Producto;
import com.tienda.demo.catalogo.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio del catálogo de productos.
 *
 * <p>Concentra las reglas sobre el stock. El módulo {@code pedidos} las usa a
 * través de este servicio (nunca del repositorio) para validar y descontar
 * existencias al vender.</p>
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository repository;

    /**
     * Devuelve todos los productos del catálogo, disponibles o no.
     *
     * @return lista completa de productos.
     */
    public List<Producto> listarTodos() {
        return repository.findAll();
    }

    /**
     * Devuelve los productos que se pueden ofrecer en una venta:
     * activos y con existencias.
     *
     * @return productos activos con stock mayor que cero.
     */
    public List<Producto> listarDisponibles() {
        return repository.findAll().stream()
                .filter(Producto::isActivo)
                .filter(producto -> producto.getStock() > 0)
                .toList();
    }

    /**
     * Da de alta un producto en el catálogo.
     *
     * <p>Nace activo para que esté disponible de inmediato.</p>
     *
     * @param request datos validados del formulario.
     * @return el producto persistido, con su id generado.
     */
    public Producto crear(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setActivo(true);
        return repository.save(producto);
    }

    /**
     * Recupera un producto por su id, sin comprobar disponibilidad.
     *
     * <p>Se usa para mostrar datos (nombre, precio) de productos ya elegidos,
     * donde no procede validar stock. Para vender, usar
     * {@link #buscarParaVenta(Long, int)}.</p>
     *
     * @param id identificador del producto.
     * @return el producto encontrado.
     * @throws IllegalArgumentException si no existe.
     */
    public Producto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el producto con id " + id));
    }

    /**
     * Recupera un producto apto para vender la cantidad pedida.
     *
     * <p>Comprueba que exista, esté activo y tenga stock suficiente. Se llama
     * dentro de la transacción del pedido, antes de descontar existencias.</p>
     *
     * @param id       identificador del producto.
     * @param cantidad unidades que se quieren vender.
     * @return el producto listo para la venta.
     * @throws IllegalArgumentException si el producto no existe.
     * @throws IllegalStateException    si está inactivo o no hay stock suficiente.
     */
    public Producto buscarParaVenta(Long id, int cantidad) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el producto con id " + id));
        if (!producto.isActivo()) {
            throw new IllegalStateException("El producto " + producto.getNombre() + " no está disponible");
        }
        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente de " + producto.getNombre()
                    + " (disponibles: " + producto.getStock() + ", solicitados: " + cantidad + ")");
        }
        return producto;
    }

    /**
     * Resta unidades al stock de un producto ya validado.
     *
     * <p>Recibe la entidad (no el id) porque quien llama ya la obtuvo con
     * {@link #buscarParaVenta}; así se evita una segunda consulta. Al estar
     * dentro de una transacción, el cambio se persiste al confirmarse esta.</p>
     *
     * @param producto producto cuyo stock se reduce.
     * @param cantidad unidades a descontar.
     */
    public void descontarStock(Producto producto, int cantidad) {
        producto.setStock(producto.getStock() - cantidad);
        repository.save(producto);
    }
}
