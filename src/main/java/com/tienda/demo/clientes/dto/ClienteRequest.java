package com.tienda.demo.clientes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO con los datos que el usuario introduce al dar de alta un cliente.
 *
 * <p>Se separa de la entidad {@link com.tienda.demo.clientes.model.Cliente}
 * a propósito: aquí solo viajan los campos que rellena el formulario. Los
 * campos que decide el sistema ({@code activo} y {@code fechaPrimeraCompra})
 * no se exponen para que el cliente no pueda falsearlos desde la web.</p>
 */
@Getter
@Setter
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El NIF es obligatorio")
    private String nif;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    private String telefono;

    private String direccion;
}
