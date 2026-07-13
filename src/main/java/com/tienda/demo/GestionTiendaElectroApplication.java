package com.tienda.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación GestionTiendaElectro.
 *
 * <p>La anotación {@code @SpringBootApplication} combina tres anotaciones:
 * <ul>
 *   <li>{@code @Configuration}: marca esta clase como fuente de definiciones de beans.</li>
 *   <li>{@code @EnableAutoConfiguration}: Spring Boot configura automáticamente la aplicación
 *       según las dependencias del classpath (aquí: web MVC, JPA/Hibernate, H2, Thymeleaf...).</li>
 *   <li>{@code @ComponentScan}: escanea este paquete ({@code com.tienda.demo}) y todos sus
 *       subpaquetes en busca de componentes ({@code @Controller}, {@code @Service},
 *       {@code @Repository}...). Por eso los módulos (catalogo, clientes, empleados, pedidos,
 *       shared) deben colgar de este paquete: así se detectan sin configuración extra.</li>
 * </ul>
 */
@SpringBootApplication
public class GestionTiendaElectroApplication {

	/**
	 * Punto de entrada de la aplicación.
	 *
	 * <p>Delega en {@link SpringApplication#run}, que arranca el contexto de Spring:
	 * crea todos los beans, levanta el servidor web embebido (Tomcat, puerto 8080 según
	 * application.properties) e inicializa la base de datos H2 en memoria.
	 *
	 * @param args argumentos de línea de comandos; permiten sobrescribir propiedades
	 *             (p. ej. {@code --server.port=9090})
	 */
	public static void main(String[] args) {
		SpringApplication.run(GestionTiendaElectroApplication.class, args);
	}

}
