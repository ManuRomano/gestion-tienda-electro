package com.tienda.demo.shared.sesion;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;

/**
 * Publica el empleado de la sesión en el modelo de <b>todas</b> las vistas.
 *
 * <p>Gracias a {@code @ControllerAdvice}, cualquier plantilla puede usar
 * {@code ${empleadoActual}} sin que cada controlador tenga que añadirlo a mano
 * a su modelo.</p>
 */
@ControllerAdvice
@RequiredArgsConstructor
public class EmpleadoSesionAdvice {

    private final EmpleadoSesion empleadoSesion;

    /**
     * Nombre del empleado que está operando, para mostrarlo en la cabecera.
     *
     * @return nombre completo, o {@code null} si aún no se ha elegido.
     */
    @ModelAttribute("empleadoActual")
    public String empleadoActual() {
        return empleadoSesion.getNombreCompleto();
    }
}
