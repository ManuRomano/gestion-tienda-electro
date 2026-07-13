package com.tienda.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de arranque de la aplicación.
 *
 * <p>{@code @SpringBootTest} levanta el contexto completo de Spring (igual que al
 * ejecutar la aplicación de verdad, pero sin abrir el puerto HTTP) usando la
 * configuración real: beans, base de datos H2 en memoria, etc.
 */
@SpringBootTest
class GestionTiendaElectroApplicationTests {

	/**
	 * Verifica que el contexto de Spring arranca sin errores.
	 *
	 * <p>El método está vacío a propósito: el "assert" implícito es el propio arranque.
	 * Si alguna configuración o bean estuviera mal definido (una entidad JPA inválida,
	 * una dependencia que no se puede inyectar...), el contexto fallaría al cargarse
	 * y este test fallaría antes de ejecutar el cuerpo del método.
	 */
	@Test
	void contextLoads() {
	}

}
