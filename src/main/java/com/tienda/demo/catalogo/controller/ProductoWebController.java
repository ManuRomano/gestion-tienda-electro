package com.tienda.demo.catalogo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.demo.catalogo.dto.ProductoRequest;
import com.tienda.demo.catalogo.service.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador web (MVC) del catálogo de productos.
 *
 * <p>Mismo patrón que el resto de módulos. Las plantillas
 * ({@code catalogo/listarProductos} y {@code catalogo/crearProductos}) se
 * crearán en el paso de vistas; de momento estos endpoints compilan pero no
 * renderizan.</p>
 */
@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoWebController {

    private final ProductoService service;

    /**
     * Muestra el listado de todos los productos del catálogo.
     *
     * @param model modelo donde se deja la lista para la vista.
     * @return nombre de la plantilla del listado.
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", service.listarTodos());
        return "catalogo/listarProductos";
    }

    /**
     * Muestra el formulario vacío de alta de producto.
     *
     * @param model modelo con un {@link ProductoRequest} en blanco.
     * @return nombre de la plantilla del formulario.
     */
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("productoRequest", new ProductoRequest());
        return "catalogo/crearProductos";
    }

    /**
     * Procesa el alta de un producto.
     *
     * @param request       datos del formulario, validados con {@code @Valid}.
     * @param bindingResult resultado de la validación.
     * @return el formulario (si hay errores) o la redirección al listado.
     */
    @PostMapping
    public String crear(@Valid @ModelAttribute("productoRequest") ProductoRequest request,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "catalogo/crearProductos";
        }
        service.crear(request);
        return "redirect:/productos";
    }
}
