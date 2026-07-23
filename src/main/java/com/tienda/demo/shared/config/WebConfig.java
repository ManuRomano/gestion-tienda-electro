package com.tienda.demo.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tienda.demo.shared.sesion.EmpleadoSesion;

import lombok.RequiredArgsConstructor;

/**
 * Configuración web de la aplicación.
 *
 * <p>Registra el {@link EmpleadoSesionInterceptor} para todas las rutas,
 * excepto las que deben seguir siendo accesibles sin empleado elegido: la
 * propia pantalla de acceso (si no, habría redirección infinita), los recursos
 * estáticos, la consola H2 y la página de error.</p>
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final EmpleadoSesion empleadoSesion;

    /**
     * Añade el interceptor de sesión de empleado con sus exclusiones.
     *
     * @param registry registro de interceptores de Spring MVC.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new EmpleadoSesionInterceptor(empleadoSesion))
                .excludePathPatterns("/acceso", "/acceso/**", "/css/**", "/h2-console/**", "/error");
    }
}
