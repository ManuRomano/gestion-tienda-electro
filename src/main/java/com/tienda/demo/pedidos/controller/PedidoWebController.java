package com.tienda.demo.pedidos.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tienda.demo.catalogo.model.Producto;
import com.tienda.demo.catalogo.service.ProductoService;
import com.tienda.demo.clientes.dto.ClienteRequest;
import com.tienda.demo.clientes.model.Cliente;
import com.tienda.demo.clientes.service.ClienteService;
import com.tienda.demo.pedidos.dto.LineaCarrito;
import com.tienda.demo.pedidos.dto.LineaPedidoRequest;
import com.tienda.demo.pedidos.dto.PedidoRequest;
import com.tienda.demo.pedidos.service.CarritoCompra;
import com.tienda.demo.pedidos.service.PedidoService;
import com.tienda.demo.shared.sesion.EmpleadoSesion;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador web de los pedidos, incluido el asistente de compra en 3 pasos.
 *
 * <p>El flujo imita el mostrador de una tienda:</p>
 * <ol>
 *   <li><b>Productos</b>: se van añadiendo electrodomésticos al carrito.</li>
 *   <li><b>Cliente</b>: se elige uno existente o se da de alta en el momento.</li>
 *   <li><b>Confirmar</b>: se revisa el resumen y se cierra la venta.</li>
 * </ol>
 *
 * <p>Lo acumulado entre pasos vive en {@link CarritoCompra} (sesión) y el
 * empleado que firma la venta sale de {@link EmpleadoSesion}, elegido al entrar
 * al sistema. Nada se guarda en base de datos hasta el último paso: ahí se
 * arma un {@link PedidoRequest} y se delega en {@link PedidoService}, que
 * aplica las reglas de stock dentro de una transacción. El asistente es, por
 * tanto, solo una capa de interfaz: el servicio de negocio no ha cambiado.</p>
 */
@Controller
@RequestMapping("/pedidos")
@RequiredArgsConstructor
public class PedidoWebController {

    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final CarritoCompra carrito;
    private final EmpleadoSesion empleadoSesion;

    /**
     * Muestra el listado de todos los pedidos ya registrados.
     *
     * @param model modelo donde se deja la lista para la vista.
     * @return nombre de la plantilla del listado.
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "pedidos/listarPedidos";
    }

    // ------------------------------------------------------------------
    // Paso 1: elegir los electrodomésticos
    // ------------------------------------------------------------------

    /**
     * Paso 1: catálogo disponible y carrito acumulado hasta ahora.
     *
     * @param model modelo con los productos vendibles y el carrito.
     * @return plantilla del paso de productos.
     */
    @GetMapping("/nuevo")
    public String pasoProductos(Model model) {
        model.addAttribute("productos", productoService.listarDisponibles());
        prepararCarrito(model);
        return "pedidos/compraProductos";
    }

    /**
     * Añade un producto al carrito validando antes que haya stock.
     *
     * <p>La comprobación tiene en cuenta lo que ya estaba reservado en el
     * carrito: si hay 3 unidades y se piden 2 más, se valida contra 5. Si no
     * hay bastante, se vuelve al paso 1 con el mensaje de error.</p>
     *
     * @param productoId producto elegido.
     * @param cantidad   unidades solicitadas.
     * @param flash      para llevar el mensaje de error tras la redirección.
     * @return redirección al paso 1.
     */
    @PostMapping("/nuevo/lineas")
    public String agregarLinea(@RequestParam Long productoId,
                               @RequestParam int cantidad,
                               RedirectAttributes flash) {
        if (cantidad <= 0) {
            flash.addFlashAttribute("error", "La cantidad debe ser mayor que cero");
            return "redirect:/pedidos/nuevo";
        }
        try {
            productoService.buscarParaVenta(productoId, carrito.cantidadDe(productoId) + cantidad);
            carrito.agregar(productoId, cantidad);
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/pedidos/nuevo";
    }

    /**
     * Quita una línea del carrito.
     *
     * @param indice posición de la línea a eliminar.
     * @return redirección al paso 1.
     */
    @PostMapping("/nuevo/lineas/eliminar")
    public String eliminarLinea(@RequestParam int indice) {
        carrito.eliminar(indice);
        return "redirect:/pedidos/nuevo";
    }

    /**
     * Descarta la compra en curso y vacía el carrito.
     *
     * @return redirección al paso 1, ya vacío.
     */
    @PostMapping("/nuevo/vaciar")
    public String vaciarCarrito() {
        carrito.vaciar();
        return "redirect:/pedidos/nuevo";
    }

    // ------------------------------------------------------------------
    // Paso 2: elegir o crear el cliente
    // ------------------------------------------------------------------

    /**
     * Paso 2: elegir cliente existente o darlo de alta.
     *
     * <p>Si el carrito está vacío no tiene sentido continuar, así que devuelve
     * al paso 1.</p>
     *
     * @param model modelo con los clientes activos y un formulario de alta vacío.
     * @return plantilla del paso de cliente, o redirección al paso 1.
     */
    @GetMapping("/nuevo/cliente")
    public String pasoCliente(Model model) {
        if (carrito.estaVacio()) {
            return "redirect:/pedidos/nuevo";
        }
        model.addAttribute("clientes", clienteService.listarActivos());
        model.addAttribute("clienteRequest", new ClienteRequest());
        prepararCarrito(model);
        return "pedidos/compraCliente";
    }

    /**
     * Asocia a la compra un cliente que ya existía.
     *
     * @param clienteId cliente seleccionado en el desplegable.
     * @return redirección al paso de confirmación.
     */
    @PostMapping("/nuevo/cliente")
    public String elegirCliente(@RequestParam Long clienteId) {
        carrito.setClienteId(clienteId);
        return "redirect:/pedidos/nuevo/confirmar";
    }

    /**
     * Da de alta un cliente nuevo y lo asigna a la compra en curso.
     *
     * <p>Reutiliza el mismo {@link ClienteRequest} y las mismas validaciones
     * que el alta de clientes normal, en vez de duplicar reglas.</p>
     *
     * @param request       datos del cliente nuevo.
     * @param bindingResult resultado de la validación.
     * @param model         modelo, para repintar el paso si hay errores.
     * @return redirección al paso de confirmación, o el paso 2 con errores.
     */
    @PostMapping("/nuevo/cliente/crear")
    public String crearClienteYContinuar(@Valid @ModelAttribute("clienteRequest") ClienteRequest request,
                                         BindingResult bindingResult, Model model) {
        if (carrito.estaVacio()) {
            return "redirect:/pedidos/nuevo";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("clientes", clienteService.listarActivos());
            prepararCarrito(model);
            return "pedidos/compraCliente";
        }
        Cliente creado = clienteService.crear(request);
        carrito.setClienteId(creado.getId());
        return "redirect:/pedidos/nuevo/confirmar";
    }

    // ------------------------------------------------------------------
    // Paso 3: revisar y cerrar la venta
    // ------------------------------------------------------------------

    /**
     * Paso 3: resumen de la compra antes de cerrarla.
     *
     * <p>Comprueba que se hayan completado los pasos anteriores; si falta
     * alguno, devuelve al que corresponda.</p>
     *
     * @param model modelo con el cliente, las líneas y el total.
     * @return plantilla de confirmación, o redirección al paso pendiente.
     */
    @GetMapping("/nuevo/confirmar")
    public String pasoConfirmar(Model model) {
        if (carrito.estaVacio()) {
            return "redirect:/pedidos/nuevo";
        }
        if (!carrito.tieneCliente()) {
            return "redirect:/pedidos/nuevo/cliente";
        }
        model.addAttribute("cliente", clienteService.buscarActivo(carrito.getClienteId()));
        prepararCarrito(model);
        return "pedidos/compraConfirmar";
    }

    /**
     * Cierra la venta: crea el pedido y vacía el carrito.
     *
     * <p>Traduce el carrito a un {@link PedidoRequest} y deja que
     * {@link PedidoService} haga el trabajo de verdad (validar stock,
     * congelar precios, calcular el total y descontar existencias, todo en una
     * transacción). Si el servicio rechaza la compra (por ejemplo, otro empleado
     * agotó el stock mientras tanto), se vuelve al resumen con el motivo.</p>
     *
     * @param flash para transportar el mensaje de error tras la redirección.
     * @return redirección al listado de pedidos, o de vuelta al resumen si falla.
     */
    @PostMapping("/nuevo/confirmar")
    public String confirmarCompra(RedirectAttributes flash) {
        if (carrito.estaVacio() || !carrito.tieneCliente()) {
            return "redirect:/pedidos/nuevo";
        }

        PedidoRequest request = new PedidoRequest();
        request.setClienteId(carrito.getClienteId());
        request.setEmpleadoId(empleadoSesion.getId());
        request.setLineas(new ArrayList<>(carrito.getLineas()));

        try {
            pedidoService.crear(request);
        } catch (IllegalArgumentException | IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/pedidos/nuevo/confirmar";
        }

        carrito.vaciar();
        return "redirect:/pedidos";
    }

    // ------------------------------------------------------------------
    // Apoyo
    // ------------------------------------------------------------------

    /**
     * Resuelve el carrito para poder pintarlo y deja líneas y total en el modelo.
     *
     * <p>El carrito solo guarda ids y cantidades; aquí se recuperan los
     * productos para mostrar nombre, precio y subtotal. El total se calcula con
     * {@link BigDecimal} para no arrastrar errores de redondeo.</p>
     *
     * @param model modelo de la vista donde se publican los datos.
     */
    private void prepararCarrito(Model model) {
        List<LineaCarrito> lineas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (LineaPedidoRequest linea : carrito.getLineas()) {
            Producto producto = productoService.buscarPorId(linea.getProductoId());
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(linea.getCantidad()));
            lineas.add(new LineaCarrito(producto, linea.getCantidad(), subtotal));
            total = total.add(subtotal);
        }

        model.addAttribute("lineasCarrito", lineas);
        model.addAttribute("totalCarrito", total);
    }
}
