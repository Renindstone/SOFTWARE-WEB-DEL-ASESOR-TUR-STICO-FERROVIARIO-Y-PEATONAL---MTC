package com.turismo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Prueba de humo del Sprint 0: verifica que el contexto de Spring levanta
 * completo (controladores, servicios, repositorios, seguridad y scheduler) y
 * que el mapeo JPA de las doce entidades es coherente, generando el esquema
 * sobre H2 en modo PostgreSQL (ver src/test/resources/application.properties).
 */
@SpringBootTest
class AsesorTuristicoMtcApplicationTests {

	@Test
	void contextLoads() {
	}

}
