package com.tienda.demo.shared.sesion;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.tienda.demo.empleados.model.Empleado;

import lombok.Getter;

/**
 * Empleado que está usando la aplicación en la sesión actual.
 *
 * <p>Hace de "quién está en el mostrador": se elige al entrar y firma todas las
 * ventas y altas que se hagan después. Es un sustituto sencillo de un login
 * real (no hay contraseñas ni seguridad todavía).</p>
 *
 * <p>{@code @SessionScope} hace que Spring cree una instancia <b>por sesión de
 * navegador</b>, no una única compartida. Spring inyecta en su lugar un proxy,
 * por eso puede usarse dentro de componentes singleton (como el interceptor)
 * sin problemas.</p>
 */
@Component
@SessionScope
@Getter
public class EmpleadoSesion implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String nombreCompleto;

    /**
     * Indica si ya se ha elegido empleado en esta sesión.
     *
     * <p>Lo consulta el interceptor para decidir si deja pasar o redirige a la
     * pantalla de acceso.</p>
     *
     * @return {@code true} si hay empleado seleccionado.
     */
    public boolean hayEmpleado() {
        return id != null;
    }

    /**
     * Guarda en la sesión el empleado elegido.
     *
     * <p>Se queda solo con el id y el nombre (lo que se necesita mostrar y
     * asociar a los pedidos), no con la entidad entera: así no se arrastra un
     * objeto JPA desconectado durante toda la sesión.</p>
     *
     * @param empleado empleado seleccionado en la pantalla de acceso.
     */
    public void iniciar(Empleado empleado) {
        this.id = empleado.getId();
        this.nombreCompleto = empleado.getNombre() + " " + empleado.getApellidos();
    }

    /**
     * Vacía la selección para poder cambiar de empleado.
     */
    public void cerrar() {
        this.id = null;
        this.nombreCompleto = null;
    }
}
