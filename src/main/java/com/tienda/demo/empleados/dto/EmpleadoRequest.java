package com.tienda.demo.empleados.dto;

import com.tienda.demo.empleados.model.PuestoEmpleado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpleadoRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotNull(message = "El puesto es obligatorio")
    private PuestoEmpleado puesto;
}