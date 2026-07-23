package com.tienda.demo.shared.sesion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tienda.demo.empleados.service.EmpleadoService;

import lombok.RequiredArgsConstructor;

/**
 * Pantalla de entrada al sistema: elegir qué empleado va a operar.
 *
 * <p>Es la única zona accesible sin haber elegido empleado (el interceptor la
 * excluye), porque si no habría una redirección infinita.</p>
 */
@Controller
@RequestMapping("/acceso")
@RequiredArgsConstructor
public class AccesoController {

    private final EmpleadoService empleadoService;
    private final EmpleadoSesion empleadoSesion;

    /**
     * Muestra la lista de empleados para elegir quién opera.
     *
     * @param model modelo con los empleados disponibles.
     * @return plantilla de la pantalla de acceso.
     */
    @GetMapping
    public String formulario(Model model) {
        model.addAttribute("empleados", empleadoService.listarTodos());
        return "acceso";
    }

    /**
     * Guarda el empleado elegido en la sesión y entra a la aplicación.
     *
     * @param empleadoId identificador del empleado seleccionado.
     * @return redirección al panel principal.
     */
    @PostMapping
    public String entrar(@RequestParam Long empleadoId) {
        empleadoSesion.iniciar(empleadoService.buscarPorId(empleadoId));
        return "redirect:/";
    }

    /**
     * Cierra la sesión del empleado para poder cambiar de turno.
     *
     * @return redirección a la pantalla de acceso.
     */
    @PostMapping("/salir")
    public String salir() {
        empleadoSesion.cerrar();
        return "redirect:/acceso";
    }
}
