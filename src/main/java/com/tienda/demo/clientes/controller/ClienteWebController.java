package com.tienda.demo.clientes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tienda.demo.clientes.dto.ClienteRequest;
import com.tienda.demo.clientes.service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador web (MVC) del módulo de clientes.
 *
 * <p>Traduce peticiones HTTP en llamadas al {@link ClienteService} y elige la
 * plantilla Thymeleaf a mostrar. Aún no existen las vistas
 * ({@code clientes/listarClientes} y {@code clientes/crearClientes}); se
 * crearán en el siguiente paso. Hasta entonces estos endpoints compilan pero
 * no renderizan.</p>
 */
@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteWebController {

    private final ClienteService service;

    /**
     * Muestra el listado de todos los clientes.
     *
     * @param model modelo donde se deja la lista para la vista.
     * @return nombre de la plantilla del listado.
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", service.listarTodos());
        return "clientes/listarClientes";
    }

    /**
     * Muestra el formulario vacío de alta de cliente.
     *
     * @param model modelo con un {@link ClienteRequest} en blanco al que se
     *              enlazarán los campos del formulario.
     * @return nombre de la plantilla del formulario.
     */
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("clienteRequest", new ClienteRequest());
        return "clientes/crearClientes";
    }

    /**
     * Procesa el envío del formulario de alta.
     *
     * <p>Si la validación falla, vuelve al formulario mostrando los errores;
     * si es correcta, crea el cliente y redirige al listado (patrón
     * Post-Redirect-Get para evitar reenvíos al recargar).</p>
     *
     * @param request       datos del formulario, validados con {@code @Valid}.
     * @param bindingResult resultado de la validación.
     * @return la vista del formulario (si hay errores) o la redirección al listado.
     */
    @PostMapping
    public String crear(@Valid @ModelAttribute("clienteRequest") ClienteRequest request,
                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "clientes/crearClientes";
        }
        service.crear(request);
        return "redirect:/clientes";
    }
}
