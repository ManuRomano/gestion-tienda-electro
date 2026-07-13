# GestionTiendaElectro — Documentación del desarrollo

Documento vivo que registra **qué se ha hecho, en qué orden y por qué**. Se irá
ampliando con cada paso del desarrollo (nueva sección al final por cada avance).

---

## 1. Qué es el proyecto

Aplicación web de gestión de una tienda de electrodomésticos, construida con
**Spring Boot 4.1** y **Java 21**. Los dominios funcionales previstos son:
catálogo de productos, clientes, empleados y pedidos.

## 2. Base tecnológica (pom.xml)

El proyecto se generó con Spring Initializr (Maven). Cada dependencia y su motivo:

| Dependencia | Para qué sirve |
|---|---|
| `spring-boot-starter-webmvc` | Aplicación web: controladores REST/MVC y servidor Tomcat embebido. |
| `spring-boot-starter-data-jpa` | Persistencia con JPA/Hibernate: entidades y repositorios sin SQL manual. |
| `com.h2database:h2` | Base de datos en memoria para desarrollo: no hay que instalar nada. |
| `spring-boot-h2console` | Consola web para ver/consultar la BD H2 desde el navegador. |
| `spring-boot-starter-validation` | Validación de datos de entrada con anotaciones (`@NotNull`, `@Size`...). |
| `spring-boot-starter-thymeleaf` | Motor de plantillas HTML para las vistas del lado servidor. |
| `spring-boot-devtools` | Reinicio automático al cambiar código durante el desarrollo. |
| `lombok` | Genera getters/setters/constructores con anotaciones; menos código repetitivo. |
| Starters `-test` (jpa, validation, webmvc) | Utilidades de testing de cada capa (JUnit 5, MockMvc, etc.). |

**Por qué H2 en memoria:** permite desarrollar sin instalar un servidor de BD; la
BD se crea al arrancar y se pierde al parar. Cuando el proyecto madure se puede
cambiar a MySQL/PostgreSQL tocando solo `application.properties`.

## 3. Configuración (application.properties)

- `spring.datasource.url=jdbc:h2:mem:tiendadb` → BD H2 en RAM llamada `tiendadb`.
- `spring.jpa.hibernate.ddl-auto=update` → Hibernate crea/actualiza las tablas
  automáticamente a partir de las entidades `@Entity`; no escribimos DDL a mano.
- `spring.jpa.show-sql=true` → cada SQL ejecutado se imprime en consola (aprendizaje/depuración).
- `spring.h2.console.enabled=true` → consola de BD en `http://localhost:8080/h2-console`
  (conectar con la misma URL JDBC de arriba, usuario `sa`, sin contraseña).
- `server.port=8080` → puerto HTTP de la aplicación.

## 4. Punto de entrada

`src/main/java/com/tienda/demo/GestionTiendaElectroApplication.java`

Clase con `@SpringBootApplication` y el método `main`, que arranca el contexto de
Spring: crea los beans, levanta Tomcat en el 8080 e inicializa H2. El escaneo de
componentes parte del paquete `com.tienda.demo`, **por eso todos los módulos
cuelgan de él**: cualquier `@Controller`/`@Service`/`@Repository` que creemos ahí
se detecta sin configuración adicional.

El test `GestionTiendaElectroApplicationTests.contextLoads()` comprueba que ese
arranque no falla (si una entidad o bean estuviera mal definido, el test cae).

## 5. Estructura de paquetes (13/07/2026)

Se decidió una organización **por módulos de dominio** (package-by-feature) en
lugar de por capas globales, para que todo lo relativo a un dominio (por ejemplo
pedidos) viva junto y el proyecto escale mejor:

```
com.tienda.demo
├── catalogo/      → productos de la tienda
├── clientes/      → gestión de clientes
├── empleados/     → gestión de empleados
├── pedidos/       → pedidos y sus líneas
└── shared/        → código transversal (utilidades, excepciones, config...)
```

Dentro de cada módulo de dominio, cinco subpaquetes con un rol cada uno:

| Subpaquete | Rol | Ejemplo futuro |
|---|---|---|
| `model` | Entidades JPA: clases que se mapean a tablas de la BD. | `Producto` |
| `repository` | Acceso a datos: interfaces que extienden `JpaRepository`. | `ProductoRepository` |
| `service` | Lógica de negocio: reglas, validaciones, transacciones. | `ProductoService` |
| `controller` | Capa web: recibe peticiones HTTP y devuelve respuestas/vistas. | `ProductoController` |
| `dto` | Objetos de transferencia: lo que entra/sale por la API, separado de la entidad. | `ProductoDTO` |

**Flujo de una petición:** `Controller → Service → Repository → BD`, con los DTO
como formato de entrada/salida en el controller y las entidades (`model`) solo
de `service` hacia abajo. Esta separación evita exponer las entidades JPA
directamente en la API.

**Excepción — `shared`:** no lleva esas cinco capas porque no es un dominio, sino
código de apoyo común; sus subpaquetes (p. ej. `exception`, `config`, `util`) se
crearán cuando haga falta.

## 6. Comentarios en el código (13/07/2026)

Se documentaron con Javadoc todos los métodos existentes:

- `GestionTiendaElectroApplication.main` — qué hace `SpringApplication.run` y
  qué implica `@SpringBootApplication`.
- `GestionTiendaElectroApplicationTests.contextLoads` — por qué un test vacío
  tiene valor (el assert implícito es que el contexto arranca).

También se comentó `application.properties` línea a línea.

**Criterio a partir de ahora:** todo método nuevo se escribe con su Javadoc
explicando qué hace y por qué existe, y cada avance añade una sección a este
documento.

## 7. Estilos de la interfaz (14/07/2026)

Con las primeras vistas Thymeleaf ya creadas (`index.html` y las de empleados:
`listarEmpleados.html` y `crearEmpleados.html`), se añadió la capa visual:

**`src/main/resources/static/css/styles.css`** — hoja de estilos global única.
Todo lo que se coloca en `static/` lo sirve Spring Boot tal cual, por eso las
plantillas la enlazan como `/css/styles.css` (con `th:href="@{...}"` para que la
URL se resuelva bien ante cambios de contexto).

Decisiones tomadas y su porqué:

- **CSS propio en vez de un framework (Bootstrap...):** el proyecto es pequeño y
  así se entiende cada regla; sin dependencias externas ni CDN.
- **Variables CSS en `:root`** (colores, radio de esquinas, sombra): el aspecto
  de toda la app se cambia tocando un solo bloque.
- **Clases reutilizables** pensadas para todos los módulos futuros, no solo
  empleados:
  | Clase | Uso |
  |---|---|
  | `.contenedor` | Centra el contenido y limita el ancho de página. |
  | `.btn` / `.btn-secundario` | Botones de acción / de navegación (volver, cancelar). Sirven para `<button>` y `<a>`. |
  | `.acciones` | Fila de botones con separación uniforme. |
  | `.tarjeta` | Panel blanco con sombra que envuelve formularios. |
  | `.campo` | Grupo etiqueta + input + mensaje de error en columna. |
  | `.error` | Mensaje de validación en rojo (sustituye a los `style="color:red"` inline). |
- **Tablas**: estilo aplicado directamente al elemento `table` (cebra en filas
  pares, resaltado al pasar el cursor) para que cualquier listado futuro quede
  estilado sin añadir clases; se eliminó el atributo obsoleto `border="1"`.

**Plantillas adaptadas:** las tres vistas se actualizaron para enlazar la hoja,
envolver el contenido en `.contenedor`, usar las clases anteriores y asociar
cada `label` a su campo con `for`/`id` (accesibilidad). La lógica Thymeleaf
(`th:field`, `th:errors`, `th:each`...) no se tocó.

---

*Próximos pasos previstos: completar el CRUD de empleados (editar/eliminar) y
primera entidad del módulo `catalogo`.*
