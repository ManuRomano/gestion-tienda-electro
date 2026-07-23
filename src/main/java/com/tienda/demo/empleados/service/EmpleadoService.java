package com.tienda.demo.empleados.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tienda.demo.empleados.dto.EmpleadoRequest;
import com.tienda.demo.empleados.model.Empleado;
import com.tienda.demo.empleados.repository.EmpleadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpleadoService {

    private final EmpleadoRepository repository;

    /**
     * Devuelve todos los empleados registrados.
     *
     * @return lista completa de empleados.
     */
    public List<Empleado> listarTodos() {
        return repository.findAll();
    }

    /**
     * Da de alta un empleado a partir de los datos del formulario.
     *
     * <p>La fecha de alta la fija el sistema con la fecha actual, no el usuario.</p>
     *
     * @param request datos validados del formulario.
     * @return el empleado persistido, con su id generado.
     */
    public Empleado crear(EmpleadoRequest request) {
        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre());
        empleado.setApellidos(request.getApellidos());
        empleado.setEmail(request.getEmail());
        empleado.setPuesto(request.getPuesto());
        empleado.setFechaAlta(LocalDate.now());
        return repository.save(empleado);
    }

    /**
     * Busca un empleado por su identificador.
     *
     * <p>Lo usa el módulo de pedidos para asociar la venta al empleado que la
     * gestiona, garantizando que exista.</p>
     *
     * @param id identificador del empleado.
     * @return el empleado encontrado.
     * @throws IllegalArgumentException si no existe.
     */
    public Empleado buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe el empleado con id " + id));
    }
}
