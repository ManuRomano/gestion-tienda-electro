package com.tienda.demo.clientes.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cliente {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nombre;

    private String apellidos;
    
    private String nif;
    
    private String email;
    
    private String telefono;
    
    private String direccion;
    
    private boolean activo;
    
    private LocalDate fechaPrimeraCompra;

}
