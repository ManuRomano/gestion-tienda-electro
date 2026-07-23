package com.tienda.demo.pedidos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tienda.demo.catalogo.model.Producto;
import com.tienda.demo.catalogo.service.ProductoService;
import com.tienda.demo.clientes.model.Cliente;
import com.tienda.demo.clientes.service.ClienteService;
import com.tienda.demo.empleados.model.Empleado;
import com.tienda.demo.empleados.service.EmpleadoService;
import com.tienda.demo.pedidos.dto.LineaPedidoRequest;
import com.tienda.demo.pedidos.dto.PedidoRequest;
import com.tienda.demo.pedidos.model.EstadoPedido;
import com.tienda.demo.pedidos.model.LineaPedido;
import com.tienda.demo.pedidos.model.Pedido;
import com.tienda.demo.pedidos.repository.PedidoRepository;

import lombok.RequiredArgsConstructor;

/**
 * Lógica de negocio de los pedidos: orquesta la compra completa.
 *
 * <p>Es el módulo que coordina a los demás. Para respetar las fronteras entre
 * módulos, se apoya en los <b>servicios</b> de clientes, empleados y catálogo
 * (nunca en sus repositorios), de forma que cada módulo sigue siendo dueño de
 * sus propias reglas.</p>
 */
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository repository;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;
    private final ProductoService productoService;

    /**
     * Devuelve todos los pedidos registrados.
     *
     * @return lista completa de pedidos.
     */
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    /**
     * Registra una compra completa a partir de los datos del formulario.
     *
     * <p>Todo el método es una única transacción ({@code @Transactional}):
     * valida cliente y empleado, y por cada línea comprueba el stock, fija el
     * precio del momento, calcula el total y descuenta existencias. Si algo
     * falla a mitad (por ejemplo, la tercera línea se queda sin stock), se
     * revierte <b>todo</b>: no queda un pedido a medias ni stock descontado sin
     * su venta.</p>
     *
     * @param request datos validados de la compra.
     * @return el pedido creado, con su total y sus líneas ya persistidos.
     */
    @Transactional
    public Pedido crear(PedidoRequest request) {
        Cliente cliente = clienteService.buscarActivo(request.getClienteId());
        Empleado empleado = empleadoService.buscarPorId(request.getEmpleadoId());

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEmpleado(empleado);
        pedido.setFecha(LocalDate.now());
        pedido.setEstado(EstadoPedido.CREADO);

        BigDecimal total = BigDecimal.ZERO;
        for (LineaPedidoRequest lineaRequest : request.getLineas()) {
            Producto producto = productoService.buscarParaVenta(
                    lineaRequest.getProductoId(), lineaRequest.getCantidad());

            LineaPedido linea = new LineaPedido();
            linea.setProducto(producto);
            linea.setCantidad(lineaRequest.getCantidad());
            linea.setPrecioUnitario(producto.getPrecio());
            pedido.addLinea(linea);

            total = total.add(linea.getSubtotal());
            productoService.descontarStock(producto, lineaRequest.getCantidad());
        }
        pedido.setTotal(total);

        clienteService.registrarPrimeraCompra(cliente);
        return repository.save(pedido);
    }
}
