package com.tienda.demo.shared.config;

import org.springframework.web.servlet.HandlerInterceptor;

import com.tienda.demo.shared.sesion.EmpleadoSesion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * Impide usar la aplicación sin haber elegido antes un empleado.
 *
 * <p>Se ejecuta antes de cada petición: si la sesión no tiene empleado,
 * redirige a la pantalla de acceso y corta el flujo devolviendo
 * {@code false}.</p>
 */
@RequiredArgsConstructor
public class EmpleadoSesionInterceptor implements HandlerInterceptor {

    private final EmpleadoSesion empleadoSesion;

    /**
     * Deja pasar la petición solo si hay empleado en sesión.
     *
     * @param request  petición entrante.
     * @param response respuesta, usada para redirigir si no hay empleado.
     * @param handler  controlador destino (no se usa aquí).
     * @return {@code true} para continuar; {@code false} si se ha redirigido.
     * @throws java.io.IOException si falla el envío de la redirección.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws java.io.IOException {
        if (empleadoSesion.hayEmpleado()) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/acceso");
        return false;
    }
}
