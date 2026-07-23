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

## 8. Backend del flujo de compra: clientes, catálogo y pedidos (20/07/2026)

Se construyó la estructura Java completa (sin vistas todavía) de los tres
módulos que faltaban para poder registrar una compra de principio a fin. El
recorrido de datos es: un **empleado** atiende a un **cliente**, que compra
uno o varios **productos** del catálogo, generando un **pedido** con sus
**líneas**.

### 8.1. Módulo `clientes`

La entidad `Cliente` ya existía. Se añadió el resto de capas:

- `ClienteRepository` — CRUD heredado de `JpaRepository`.
- `ClienteRequest` (DTO) — solo los campos que rellena el formulario (nombre,
  apellidos, NIF, email, teléfono, dirección). **No** expone `activo` ni
  `fechaPrimeraCompra` para que no puedan falsearse desde la web.
- `ClienteService` con reglas propias del dominio:
  - `crear` → el cliente nace **activo** y **sin** fecha de primera compra.
  - `buscarActivo` → recupera un cliente que puede comprar; lanza excepción si
    no existe o está de baja. Lo usa el pedido.
  - `registrarPrimeraCompra` → fija `fechaPrimeraCompra` solo si estaba vacía,
    conservando siempre la fecha de la compra más antigua.
- `ClienteWebController` — listado, formulario y alta (patrón
  Post-Redirect-Get), igual que empleados.

### 8.2. Módulo `catalogo`

- `Producto` (entidad) — `nombre`, `precio`, `stock`, `activo`. El precio es
  **`BigDecimal`** y no `double`: en dinero, la coma flotante arrastra errores
  de redondeo. `activo` permite la **baja lógica** (retirar de venta sin
  borrar, para no romper pedidos históricos).
- `ProductoRepository`, `ProductoRequest` (DTO).
- `ProductoService` — concentra las reglas de stock, que el pedido consume:
  - `listarDisponibles` → activos y con stock, para ofrecer en la compra.
  - `buscarParaVenta` → valida existencia, que esté activo y que haya stock
    suficiente antes de vender.
  - `descontarStock` → resta unidades al producto ya validado.
- `ProductoWebController` — mismo patrón de listado/alta.

### 8.3. Módulo `pedidos`

- `EstadoPedido` (enum) — `CREADO → PAGADO → ENVIADO → ENTREGADO`, más
  `CANCELADO`.
- `Pedido` (entidad) — cabecera de la venta: `@ManyToOne` a `Cliente` y a
  `Empleado`, `fecha`, `estado`, `total` (guardado calculado) y `@OneToMany`
  de líneas con `cascade = ALL` + `orphanRemoval` (las líneas se guardan y
  borran con el pedido). El helper `addLinea` mantiene la relación bidireccional.
- `LineaPedido` (entidad) — producto + cantidad + `precioUnitario`
  **copiado** del producto en el momento de la compra (si el precio del
  catálogo cambia luego, los pedidos antiguos no se alteran). `getSubtotal` es
  un valor **derivado** (no se almacena).
- `PedidoRequest` / `LineaPedidoRequest` (DTOs) — con `@Valid` en la lista para
  validar también cada línea; en las líneas solo viaja el `productoId`, nunca
  el precio.
- `PedidoService` — el corazón del flujo. El método `crear` es **una única
  transacción** (`@Transactional`): valida cliente y empleado, y por cada línea
  comprueba stock, fija el precio, suma el total y descuenta existencias. Si
  algo falla a mitad, se **revierte todo** (no queda pedido a medias ni stock
  descontado sin venta). Se apoya en los **servicios** de los otros módulos,
  nunca en sus repositorios, para mantener las fronteras.
- `PedidoWebController` — el formulario carga clientes, empleados y productos
  disponibles como opciones seleccionables.

### 8.4. Cambio en `empleados`

Se añadió `EmpleadoService.buscarPorId`, necesario para que el pedido asocie la
venta al empleado que la gestiona.

### 8.5. Decisiones transversales y verificación

- **Comunicación entre módulos solo vía servicios.** `pedidos` depende de
  `clientes`, `catalogo` y `empleados`, pero siempre a través de sus `Service`.
  Las dependencias son unidireccionales (nadie depende de `pedidos`), lo que
  facilitará extraer módulos a microservicios.
- **Errores con excepciones estándar** (`IllegalArgumentException` /
  `IllegalStateException`) por ahora; más adelante se centralizarán en
  `shared/exception` con un manejador global.
- **Verificado:** el proyecto compila y el contexto de Spring arranca
  (`contextLoads`), creando las 5 tablas y sus claves foráneas correctamente.
  Las columnas de dinero quedan como `numeric(38,2)`.

---

*Próximos pasos previstos: crear las vistas Thymeleaf de clientes, catálogo y
pedidos (el formulario de pedido con líneas dinámicas es el más interesante), y
más adelante la entidad `Factura` para cerrar la compra.*

## 9. Portada tipo kanban y listados de clientes/empleados (20/07/2026)

### 9.1. Portada (`index.html`)

Se rehízo la pantalla de inicio como un **tablero tipo kanban**: una columna por
módulo (Empleados, Clientes, Catálogo, Pedidos) y, dentro de cada una, dos
tarjetas-acción ("tickets") que enlazan a **Ver listado** (borde azul) y
**+ Nuevo** (borde verde). Nuevas clases reutilizables en `styles.css`:
`.tablero` (rejilla responsiva con `auto-fit`), `.columna`, `.columna-titulo`,
`.kanban-card` y su variante `.kanban-card--crear`. Se añadió la variable
`--color-exito` (verde) para las acciones de alta.

### 9.2. Listados de clientes y empleados

- **`clientes/listarClientes.html`** (nuevo): tabla con id, nombre, apellidos,
  NIF, email, teléfono, estado y fecha de primera compra.
- **`empleados/listarEmpleados.html`**: se alineó al mismo patrón (enlaces con
  `th:href` en lugar de rutas fijas y fila de "sin datos").

Dos utilidades nuevas en `styles.css`, pensadas para reutilizar en cualquier
listado (incluidos los estados de pedido a futuro):

- `.badge` + `.badge--activo` / `.badge--inactivo`: píldora de estado. En
  clientes muestra **Activo/Baja** según el campo `activo`.
- `.vacio`: fila centrada de "no hay registros", que aparece solo cuando la
  lista está vacía (`th:if="${#lists.isEmpty(...)}"`).

La fecha de primera compra nula se muestra como `—`.

### 9.3. Datos de ejemplo (`data.sql`)

Se añadió `src/main/resources/data.sql` con 3 empleados y 3 clientes de prueba
(uno de baja y otro sin primera compra, para ver el badge y el `—`). Para que
se cargue **después** de que Hibernate cree las tablas, se activó en
`application.properties`:

```
spring.jpa.defer-datasource-initialization=true
```

Como la BD H2 es en memoria y se recrea en cada arranque, el script se ejecuta
limpio cada vez, sin duplicados.

**Verificado:** la app arranca sin errores y ambos listados renderizan los datos
de ejemplo (badges de estado y fecha nula como `—` incluidos).

**Nota de entorno:** en esta máquina el puerto 8080 está reservado por Windows,
así que para las pruebas se lanzó en 8090 (`--server.port=8090`); la
configuración del proyecto sigue en 8080.

---

*Próximos pasos previstos: formularios de alta de clientes y productos, y las
vistas de catálogo y pedidos.*

## 10. Listados de catálogo y pedidos (20/07/2026)

Con esto, los cuatro módulos tienen ya su pantalla de listado y las tarjetas
azules del tablero kanban funcionan todas.

### 10.1. `catalogo/listarProductos.html`

Columnas: id, nombre, precio, stock y estado. Detalles:

- **Importes formateados a la española** con
  `#numbers.formatDecimal(precio, 1, 'POINT', 2, 'COMMA')` → `1.199,00 €`
  (punto de millares, coma decimal).
- **Aviso de "Sin stock"**: badge junto a la cifra cuando `stock == 0`, para
  detectar de un vistazo lo que no se puede vender.
- **Estado Activo/Retirado** según el campo `activo` (baja lógica).

### 10.2. `pedidos/listarPedidos.html`

Es la vista que cruza los cuatro módulos. Columnas: id, fecha, cliente,
empleado que atendió, nº de artículos, estado y total.

- **Nombre de cliente y empleado**: se navegan las relaciones `@ManyToOne`
  directamente desde la plantilla (`pedido.cliente.nombre`).
- **Nº de artículos**: `#lists.size(pedido.lineas)`. La colección es *lazy*,
  pero funciona porque Spring Boot trae activado `open-in-view` (la sesión JPA
  sigue abierta al renderizar). Genera una consulta por pedido (N+1); cuando el
  volumen crezca convendrá una consulta con `JOIN FETCH`.
- **Badge de estado con color por valor**: la clase se compone en la plantilla
  a partir del propio enum en minúsculas:

  ```html
  <span th:class="'badge badge--' + ${#strings.toLowerCase(pedido.estado)}"
        th:text="${pedido.estado}"></span>
  ```

  Así, añadir un estado nuevo al enum solo exige añadir su regla en
  `styles.css` (`.badge--creado`, `.badge--pagado`, `.badge--enviado`,
  `.badge--entregado`, `.badge--cancelado`). Se añadió también la utilidad
  `.num` para alinear a la derecha las columnas numéricas.

### 10.3. Ampliación de `data.sql`

Se añadieron 5 productos (uno sin stock y otro retirado) y 3 pedidos con sus
líneas, en estados distintos (`ENTREGADO`, `CANCELADO`, `CREADO`), para poder
comprobar todos los casos visuales.

**Verificado:** la aplicación arranca sin errores y ambos listados muestran los
datos correctamente: importes con formato español, badges de "Sin stock" y
"Retirado", nombres de cliente/empleado resueltos, recuento de líneas correcto
(2 artículos en el primer pedido) y los colores de cada estado aplicados.

---

*Próximos pasos previstos: los formularios de alta que faltan (cliente,
producto y el de compra con líneas dinámicas), y más adelante la entidad
`Factura` para cerrar el ciclo.*

## 11. Formulario de alta de producto (20/07/2026)

`catalogo/crearProductos.html`, siguiendo el mismo patrón que el de empleados:
`<form class="tarjeta">` con un `.campo` por dato (etiqueta + input + mensaje
de error) y la fila de `.acciones` con Guardar/Cancelar.

Particularidades frente al formulario de empleados:

- **Campos numéricos**: `type="number"` con `step="0.01"` en el precio (lleva
  céntimos) y `step="1"` en el stock. El `min="0"` refleja en el navegador la
  misma regla que `@PositiveOrZero` valida en el servidor; la validación de
  verdad sigue siendo la del backend, la del navegador es solo comodidad.
- **El formulario no pide `activo`**: lo fija el servicio (todo producto nace
  activo). Es coherente con el criterio de que el DTO solo lleve lo que el
  usuario decide.

**Verificado end-to-end en el navegador:**

1. Enviando el formulario vacío, se vuelve a él mostrando los tres mensajes de
   Bean Validation ("El nombre es obligatorio", "El precio es obligatorio",
   "El stock es obligatorio").
2. Dando de alta "Aspiradora Dyson V15" (649,99 € / 7 uds.), se guarda,
   redirige al listado (Post-Redirect-Get) y aparece como id 6, con el precio
   formateado `649,99 €` y estado Activo.

---

*Próximos pasos previstos: formulario de alta de cliente y el de compra con
líneas dinámicas; después, la entidad `Factura`.*

## 12. Formulario de alta de cliente (20/07/2026)

`clientes/crearClientes.html`, con el patrón habitual (`.tarjeta` → `.campo` →
`.error` → `.acciones`). Seis campos: nombre, apellidos, NIF, email, teléfono y
dirección.

- **Campos obligatorios vs. opcionales**: los cuatro primeros llevan su
  `<span class="error">`; teléfono y dirección no tienen validación en el DTO
  (son datos de contacto opcionales), así que se etiquetan como "(opcional)" y
  no muestran mensaje de error.
- **Tipos de input semánticos**: `type="email"` y `type="tel"`, que además
  mejoran el teclado en móvil.
- **Lo que no se pide**: ni `activo` ni `fechaPrimeraCompra`. Los fija el
  servicio — el cliente nace activo y sin fecha de primera compra, que se
  rellenará sola con su primer pedido.

**Verificado end-to-end en el navegador:**

1. Formulario vacío → vuelve mostrando los cuatro errores obligatorios, y
   teléfono/dirección sin error (correcto, son opcionales).
2. Alta de "Elena Vargas Molina" → se guarda, redirige al listado y aparece
   como id 4, con badge **Activo** y 1ª compra **—**, confirmando que el
   servicio aplicó sus reglas.

### Nota de desarrollo: plantillas nuevas y recarga

Al crear una plantilla con la aplicación ya arrancada, Thymeleaf devolvió
`TemplateInputException` (plantilla no encontrada): la app sirve desde
`target/classes` y el archivo aún no se había copiado ahí. **Al añadir
plantillas nuevas hay que reiniciar** (o relanzar Maven); editar una existente
sí se recarga sola.

---

*Próximos pasos previstos: el formulario de compra con líneas dinámicas (el más
complejo) y, después, la entidad `Factura` para cerrar el ciclo.*

## 13. Sesión de empleado y asistente de compra (20/07/2026)

Dos bloques que van juntos: quién opera el sistema, y el proceso de venta.

### 13.1. Acceso: el empleado se elige al entrar

En vez de pedir el empleado dentro del formulario de compra, se elige **al
entrar a la aplicación**, como quien ficha en el mostrador, y firma todas las
operaciones de esa sesión. Es un sustituto sencillo de un login (sin
contraseñas ni Spring Security todavía). Piezas, todas en `shared`:

| Clase | Rol |
|---|---|
| `sesion/EmpleadoSesion` | Bean `@SessionScope` con el id y nombre del empleado. Una instancia **por navegador**. |
| `sesion/AccesoController` | Pantalla `/acceso`: elegir empleado, entrar y salir. |
| `sesion/EmpleadoSesionAdvice` | `@ControllerAdvice` que publica `${empleadoActual}` en **todas** las vistas, sin repetirlo en cada controlador. |
| `config/EmpleadoSesionInterceptor` | Corta cualquier petición sin empleado y redirige a `/acceso`. |
| `config/WebConfig` | Registra el interceptor, excluyendo `/acceso`, `/css/**`, `/h2-console/**` y `/error`. |

Detalle importante: `@SessionScope` inyecta un **proxy**, y por eso el bean de
sesión puede usarse dentro de componentes singleton como el interceptor o la
configuración. La exclusión de `/acceso` es obligatoria: sin ella habría
redirección infinita.

### 13.2. La compra, en tres pasos

Flujo elegido: **primero el electrodoméstico, después el cliente** (creándolo
en el momento si no existe), y por último confirmar.

- `pedidos/service/CarritoCompra` — bean `@SessionScope` con las líneas y el
  cliente elegido. Es estado **de interfaz**, no de negocio: nada se persiste
  hasta confirmar. Si se añade un producto ya presente, suma cantidades en vez
  de duplicar la línea.
- `pedidos/dto/LineaCarrito` — *record* de solo lectura (producto, cantidad,
  subtotal) para pintar la tabla; el carrito solo guarda ids.
- `PedidoWebController` — los tres pasos, con guardas: si el carrito está vacío
  devuelve al paso 1; si falta cliente, al paso 2.

**Lo más relevante del diseño:** el asistente es solo interfaz. En el último
paso arma un `PedidoRequest` y llama al `PedidoService` de siempre, que sigue
aplicando stock, precios congelados, total y transacción. **No hubo que tocar
la lógica de negocio para añadir el proceso por pasos.**

Otros detalles:

- **Validación de stock acumulativa**: al añadir se valida contra lo que ya hay
  en el carrito (3 dentro + 2 nuevos ⇒ se comprueban 5), usando
  `CarritoCompra.cantidadDe`.
- **Solo se ofrece lo vendible**: productos activos con stock
  (`listarDisponibles`) y clientes activos (`listarActivos`, método nuevo), para
  no ofrecer opciones que el servicio luego rechazaría.
- **Errores de negocio** (sin stock, cliente de baja) se muestran como aviso en
  la propia pantalla, vía *flash attributes*, en lugar de una página de error.

**Verificado end-to-end en el navegador:**

1. Ir a `/` sin sesión redirige a `/acceso`. Se elige "Sara Núñez Vidal" y la
   cabecera pasa a mostrar "Atendiendo: Sara Núñez Vidal".
2. Paso 1: el desplegable solo lista los 3 productos vendibles (quedan fuera el
   microondas sin stock y la secadora retirada). Se añaden 2 lavadoras
   (898,00 €) y 1 televisor ⇒ total 2.097,00 €.
3. Pedir 5 televisores habiendo 3 muestra "Stock insuficiente de Televisor LG
   OLED 55" (disponibles: 3, solicitados: 5)" y **deja el carrito intacto**.
4. Paso 2: solo aparecen clientes activos (Carlos, de baja, no está). Se da de
   alta "Rubén Iglesias Mora" sin salir de la compra.
5. Paso 3: resumen correcto y, al confirmar, se crea el **pedido 4**
   (2.097,00 €, estado CREADO) con Sara como responsable, tomada de la sesión.
6. Efectos colaterales correctos: el stock bajó (lavadora 12→10, televisor
   3→2) y el cliente nuevo quedó con **1ª compra 2026-07-22**, que rellenó
   `registrarPrimeraCompra`.

---

*Próximos pasos previstos: la entidad `Factura` para cerrar el ciclo de la
compra, y edición/baja en los listados existentes.*
