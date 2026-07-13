package com.tienda.demo.empleados.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tienda.demo.empleados.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}
