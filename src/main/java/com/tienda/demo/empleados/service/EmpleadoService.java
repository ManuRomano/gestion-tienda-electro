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

    public List<Empleado> listarTodos() {
        return repository.findAll();
    }

    public Empleado crear(EmpleadoRequest request) {
        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre());
        empleado.setApellidos(request.getApellidos());
        empleado.setEmail(request.getEmail());
        empleado.setPuesto(request.getPuesto());
        empleado.setFechaAlta(LocalDate.now());
        return repository.save(empleado);
    }
}
