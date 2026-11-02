# MarketAR — backend TP

Implementación de los 42 endpoints vigentes (END-03/US13 retirado), 18 entidades JPA y una ruta auxiliar para imágenes privadas. Java 21, Spring Boot 4.0.8 y PostgreSQL. Versiones del proyecto original conservadas.

## Estado de verificación

La refactorización adopta `Controller → Service (interfaz) → ServiceImpl → JpaRepository → PostgreSQL`. Se compiló con JDK 21 y Maven y se ejecutaron pruebas contra PostgreSQL aislado. Las verificaciones y sus límites se registran en [docs/VALIDATION.md](docs/VALIDATION.md); las decisiones de arquitectura están en [docs/REFACTORING.md](docs/REFACTORING.md).

El inventario estático y OpenAPI se comprueban por separado de las pruebas HTTP de permisos, propiedad, transacciones, stock, concurrencia e idempotencia. La integración con IA se verifica con un proveedor local de prueba; el proveedor real sigue pendiente.

## Arranque local (PowerShell)

Requisitos: JDK 21, PostgreSQL disponible y acceso a Maven Central. Usar una base académica desechable: create-drop recrea las tablas al iniciar y las elimina al cerrar.

1. Crear una base vacía en pgAdmin, por ejemplo `db_marketar_tp`.
2. Abrir PowerShell en esta carpeta:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/db_marketar_tp"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "TU_CONTRASENA_LOCAL"
$bytes = New-Object byte[] 48
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
# Opcional, solo para generar imágenes reales:
# $env:CLOUDFLARE_ACCOUNT_ID = "TU_ACCOUNT_ID"
# $env:CLOUDFLARE_API_TOKEN = "TU_TOKEN_WORKERS_AI"
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

En Linux/macOS exportar las mismas variables y ejecutar `bash mvnw clean package` y `bash mvnw spring-boot:run`.

Swagger: http://localhost:8080/swagger-ui.html
OpenAPI: http://localhost:8080/v3/api-docs

Inicialización, conforme a clase:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
spring.jpa.defer-datasource-initialization=true
spring.sql.init.data-locations=classpath:constraints.sql,classpath:data.sql
```

Hibernate crea las tablas; `constraints.sql` agrega los CHECK, cinco índices parciales y tres claves compuestas; `data.sql` carga las 18 tablas. No ejecutar el SQL CREATE TABLE original como data.sql. No cambiar a validate sin preparar una estrategia de migración.

## Alternativa aislada con Docker

Definir JWT_SECRET (mínimo 32 bytes) en el entorno y ejecutar `docker compose up --build`. PostgreSQL de demostración se publica en 5433 y la app en 8080. La base `marketar_tp_test` del contenedor está separada de tu PostgreSQL habitual. Docker necesita acceso a imágenes públicas y Maven.

Las credenciales de PostgreSQL del compose son exclusivamente de demostración. No publicar ese puerto o esas credenciales en un despliegue compartido. Para AWS, configurar credenciales y almacenamiento por variables y restringir el acceso a PostgreSQL.

## Cuentas de demostración

Contraseña común: **Demo12345!**. data.sql almacena hashes BCrypt, no la contraseña legible.

| Correo | Rol |
|---|---|
| comprador1@marketar.test | COMPRADOR |
| comprador2@marketar.test | COMPRADOR |
| vendedor1@marketar.test | VENDEDOR |
| vendedor2@marketar.test | VENDEDOR |

Login: POST `/api/v1/auth/login` con `{"correo":"comprador1@marketar.test","password":"Demo12345!"}`.
Copiar `jwt` en Swagger → Authorize. Login también devuelve roles, vencimiento, usuario y cabecera Authorization.
No existe logout ni refresh token. El JWT vence a los 60 minutos. Cambiar JWT_SECRET invalida los tokens anteriores.

## Organización del código

- `entities/`: 18 entidades JPA que generan el esquema; nombres existentes conservados.
- `controllers/`: controladores de negocio que reciben y devuelven DTO e inyectan interfaces.
- `dtos/`: un archivo por DTO de entrada o salida; records inmutables con las validaciones originales.
- `services/`: siete interfaces para autenticación, catálogo, prototipos, cotizaciones, comercio, reportes y generación de imágenes.
- `serviceimpl/`: implementaciones transaccionales, propiedad, reglas de negocio y mapeo explícito.
- `repositories/`: 18 repositorios `JpaRepository` y un repositorio de proyecciones agregadas para reportes. Consultas derivadas y JPQL; `SqlStore` y `JdbcTemplate` retirados.
- `security/config`, `controllers`, `dtos`, `filters`, `services`, `util`: AuthenticationManager, CustomUserDetailsService, BCrypt, identidad autenticada y JWT.
- `exceptions/GlobalExceptionHandler`: respuestas controladas, sin detalles internos de SQL ni del proveedor IA.
- `config/`: CORS, OpenAPI, recursos públicos y ejecutor de prototipos.
- `infrastructure/`: almacenamiento de imágenes e integración con IA.
- `reference/previous-code/`: copia de las clases previas, fuera de `src` y sin participación en la compilación, para comparar la implementación. No copiarlas encima del código nuevo.

La implementación nueva sustituye los controladores `/api/producto`, `/api/comprador`, etc. por el contrato `/api/v1` y evita listados generales de datos personales. Consultar `docs/ENDPOINTS.md`.

## Contratos y reglas

- Paginación desde 0; size 1–100; DTO Page con content/page/size/totalElements/totalPages.
- Catálogo público incluye ACTIVO y AGOTADO; INACTIVO devuelve 404 en consulta pública. Carrito exige ACTIVO y stock suficiente.
- Propietario del producto, prototipo, solicitud, carrito o compra se resuelve desde el JWT; recursos ajenos devuelven 404. Rol incorrecto: 403; sin autenticación: 401.
- Crear/editar producto usa multipart: parte `data` con application/json y una o varias partes `images`. Ejemplo en docs/product.json. Máximo 10 imágenes y 5 MB por imagen, firmas JPEG/PNG/WebP comprobadas. `retainedImageIds` ausente conserva las existentes; lista vacía las elimina si se envían reemplazos.
- DTO de productos devuelve `price` para precio unitario; DTO de prototipo mantiene `specifications` como JSON serializado en texto por compatibilidad con el VARCHAR del modelo.
- Crear oferta usa monto TOTAL de toda la cantidad, no unitario, en PEN. Una oferta por vendedor/solicitud; condiciones inmutables.
- Aceptar oferta y cancelar solicitud bloquean la misma fila Solicitud. Solo una operación puede ganar; reintentar la aceptación ganadora retorna 200 con el mismo encargo.
- Encargos: CONFIRMADO → EN_FABRICACION → LISTO → ENVIADO → ENTREGADO. Repetir estado es idempotente; saltar/retroceder: 409.
- Checkout es SIMULADO. `PENDIENTE` en BoletaVenta representa un registro interno completado sin pago real. No se usa PAGADA.
- POST checkout requiere Idempotency-Key. Puede enviarse X-Cart-Id para identificar explícitamente el carrito y reintentar una operación antigua aunque exista otro carrito con ítems. La clave es única por comprador. Reutilizarla para un carrito distinto: 409.
- GET carrito refresca los precios de referencia. Si cambia el precio entre esa consulta y checkout, este devuelve 409 sin compra parcial; consultar y confirmar otra vez.
- El checkout bloquea al comprador y los productos en orden estable; conserva detalles del carrito comprado y precios históricos en BoletaDetalle.
- Reportes se filtran por fechas inclusivas en America/Lima y por vendedor autenticado. Encargos confirmados no se presentan como dinero cobrado.

## IA e imágenes

Spring Boot llama directamente a la API REST de Cloudflare Workers AI, sin Worker JavaScript, SDK, fallback ni llamadas a OpenAI.

1. En el [dashboard de Workers AI](https://dash.cloudflare.com/), seleccionar **Use REST API**, copiar **Account ID** y crear un **Workers AI API Token** usando la plantilla. Para un token personalizado se requieren permisos Workers AI Read y Edit para la cuenta. Seguir la [guía oficial REST](https://developers.cloudflare.com/workers-ai/get-started/rest-api/).
2. Configurar las variables siguientes; nunca versionar ni publicar el token:

| Variable | Valor |
|---|---|
| `CLOUDFLARE_ACCOUNT_ID` | ID de tu cuenta (obligatorio para generar) |
| `CLOUDFLARE_API_TOKEN` | Token Workers AI de esa cuenta (obligatorio para generar) |
| `CLOUDFLARE_IMAGE_MODEL` | `@cf/black-forest-labs/flux-1-schnell` por defecto |
| `CLOUDFLARE_IMAGE_STEPS` | `4` por defecto; se admiten pasos positivos hasta el máximo documentado de `8` |

El adaptador solo admite FLUX schnell; otro modelo necesita un adaptador a su esquema. La configuración inválida devuelve 503 al generar y no impide el arranque. Sin Account ID/token, devuelve 503 antes de persistir un prototipo. Conexión: 15 segundos; respuesta: 3 minutos; sin reintentos automáticos.

3. Arrancar con Maven o Docker sobre una base desechable. Obtener JWT con el login existente, autorizarse en Swagger como comprador y enviar `docs/prototype.json` a `POST /api/v1/prototypes`. Esta prueba real es **manual explícita y consume cuota**. Consultar `GET /api/v1/prototypes/{prototypeId}` hasta COMPLETADO o ERROR. Descargar la URL de `images` con el JWT; nunca se devuelve Base64 al comprador.

Solicitud ejemplo: `docs/prototype.json`. Para MOBILIARIO, specifications requiere material, color y dimensions {width,height,depth,unit}; dimensiones positivas y unit mm/cm/m. Otras categorías requieren material y color y admiten atributos adicionales. El prompt incluye texto del comprador, instrucciones y especificaciones completas: si supera 2048 caracteres devuelve 400 antes de llamar a Cloudflare, sin truncar. [Contrato del modelo](https://developers.cloudflare.com/workers-ai/models/flux-1-schnell/): se envían únicamente prompt y steps; se lee el envelope success/errors/result.image y se validan Base64 y píxeles JPEG/PNG.

Tras validar la entrada, la API devuelve 202; dos trabajadores y una cola limitada conservan PENDIENTE/GENERANDO/COMPLETADO/ERROR. La llamada HTTP ocurre fuera de la transacción. COMPLETADO se registra junto con la imagen almacenada; un rollback elimina el archivo. Es un ejecutor local para el TP, no una cola durable. El campo modeloia registra el identificador real de FLUX.

El proveedor inválido, permisos insuficientes, cuota o rate limit se traducen a 503; timeout a 504; JSON/Base64/imagen inválidos a 502. Como la llamada ocurre después del 202, estos fallos se reflejan en ERROR y un mensaje seguro al consultar el prototipo, sin cambiar retrospectivamente el HTTP 202 ni confundirlos con el JWT del comprador.

Según los [precios oficiales](https://developers.cloudflare.com/workers-ai/platform/pricing/), la asignación gratuita es 10 000 Neurons/día y se reinicia a las 00:00 UTC. Neurons mide cómputo: **no equivale a 10 000 imágenes** ni garantiza un número fijo. Consultar uso en el dashboard Workers AI. En Free, al superar la cuota fallan las operaciones; Paid permite consumo adicional facturado según las tarifas vigentes. El backend no reintenta ni cambia de proveedor ante agotamiento.

**Moderación:** se retiró la llamada externa de OpenAI. FLUX no proporciona un endpoint equivalente de moderación en este contrato. Quedan validación de DTO, longitud final, material/color, dimensiones/unidades, autenticación y propiedad; no había un filtro local semántico. Estos controles no detectan contenido dañino y **no representan aprobación de moderación**. Se pierde el bloqueo semántico previo de OpenAI. No se incorpora moderación externa adicional ni se afirma que FLUX la sustituya; cualquier incorporación futura necesita un modelo documentado, configuración y evaluación de coste propias.

Los archivos se guardan en MEDIA_DIR (por defecto ./media). Productos se sirven públicamente; prototipos requieren Authorization. Para mostrar una imagen privada en Angular, descargarla con HttpClient y JWT como blob y crear una URL de objeto. No colocar JWT en la URL.

Las imágenes de `media/` y los prototipos `DATOS_DE_PRUEBA` son fixtures mínimos, no imágenes generadas por IA. En un despliegue AWS, conservar el directorio de medios en almacenamiento persistente o sustituir MediaStorage por almacenamiento de objetos con acceso autorizado.

## Verificación

### Logs de solicitudes

SLF4J y Logback de Spring Boot escriben en consola, también en Docker. El filtro anterior a Spring Security registra método, ruta sin query string, requestId, código HTTP final y duración en milisegundos. El interceptor identifica automáticamente la operación `Controlador.metodo` en los controladores de negocio y autenticación; no es necesario repetir logs en cada endpoint. En rechazos anteriores al controlador, la operación indica rechazo de autenticación/autorización. Segmentos de ruta desconocidos se redactan para evitar datos personales o inyección de líneas.

Cada solicitud recibe un UUID nuevo en `X-Request-ID`, disponible en CORS y en MDC. Se ignoran los IDs recibidos del cliente. Buscar ese UUID permite relacionar recepción, resultado y generación IA asíncrona. El ejecutor captura el ID antes de encolar y restaura/limpia MDC al terminar; el filtro lo limpia en `finally`.

- **INFO:** recepción, resultado normal y trabajo IA completado.
- **DEBUG:** operación e IDs técnicos de ruta, ID de prototipo y modelo IA; sin datos privados.
- **WARN:** respuestas 4xx y errores controlados de generación IA, sin traza.
- **ERROR:** fallos internos. La traza se registra una sola vez, omitiendo mensajes, causas y excepciones suprimidas que pueden contener SQL, credenciales o entrada privada. El resultado HTTP sigue registrándose sin repetir la traza.

No se registran DTO, cuerpos, query strings, JWT, cabeceras privadas, contraseñas, correos, direcciones, prompts, especificaciones, multipart ni Base64. Se desactiva `org.hibernate.orm.jdbc.error` porque sus detalles de restricciones pueden incluir correos u otros valores; se conserva el resultado seguro del rechazo. No se habilita DEBUG global de Spring Security ni parámetros SQL. Los contratos de respuesta existentes permanecen iguales.

Para activar detalles técnicos de MarketAR antes de arrancar:

```powershell
$env:MARKETAR_LOG_LEVEL = "DEBUG"
.\mvnw.cmd spring-boot:run
# Restaurar el nivel habitual:
$env:MARKETAR_LOG_LEVEL = "INFO"
```

Consultar la consola de Maven o `docker compose logs -f app`. Con Docker, definir la variable antes de crear/recrear el contenedor. No se configura un archivo de logs adicional ni captura de cuerpos. Las pruebas aisladas guardan su salida en `target/verification/run-*/app.log` y `ai-app.log`.

Las pruebas de logging simulan éxito, validación 400, rechazo JWT 401, autorización 403 y fallo interno 500. Comprueban cabecera UUID, códigos finales, una sola traza saneada, ausencia de marcadores privados, limpieza entre solicitudes y propagación/restauración en el ejecutor. La suite HTTP aislada comprueba la cabecera en todas sus respuestas y correlaciona el POST IA con el trabajo completado mediante un proveedor local; no llama a Cloudflare real.

```powershell
python scripts/check_sources.py
# Después de iniciar una base de demostración recién recreada:
python scripts/smoke_test.py --base http://localhost:8080
```

Para repetir toda la verificación en Windows sin instalar un servicio PostgreSQL ni utilizar tu base habitual:

```powershell
# Python 3.12+ y JDK 21; la descarga de binarios solo se utiliza para pruebas.
python scripts/prepare_test_postgres.py
python scripts/run_isolated_tests.py --java-home "C:\ruta\al\jdk-21"
```

El ejecutor crea un clúster nuevo bajo `target/verification/run-*`, escucha solo en `127.0.0.1:55439`, usa HTTP en `18089`, fuerza un token IA vacío para el smoke test y emplea un proveedor HTTP local para probar los estados asíncronos. No utiliza `DB_URL`, credenciales ni medios existentes del entorno. Detiene sus procesos al terminar y conserva los logs. Aborta si los puertos ya están ocupados. En Linux/macOS proporcionar `--postgres-bin /ruta/a/bin` con `initdb` y `pg_ctl`.

`mvnw test` ejecuta pruebas JWT y del cliente Cloudflare con servidor HTTP local (éxito, errores, timeout, JPEG/PNG privados y rollback), sin llamadas reales; las pruebas de restricciones PostgreSQL se habilitan únicamente con `MARKETAR_TEST_DB_URL=jdbc:postgresql://127.0.0.1:55439/postgres`, después de la inicialización SQL del clúster aislado. El ejecutor realiza ambos pasos en el orden adecuado.

El smoke test modifica únicamente la API indicada. Cubre registro, login, rutas públicas, permisos y propiedad, multipart, favoritos, carrito, checkout/reintento, consultas de prototipos, solicitudes, ofertas, aceptación concurrente, estados, compras y reportes. La generación IA se prueba solo por validación de entrada (400); no realiza llamadas de pago. Requiere un arranque limpio de data.sql. No confundir el resultado estático con haber superado esta prueba.

Para probar generación real, configurar Account ID y token de Cloudflare y enviar docs/prototype.json desde Swagger; comprobar la progresión hasta COMPLETADO o un error seguro.

## Pendientes externos a este ZIP

- Proporcionar credencial y probar el proveedor de IA real.
- Desplegar en AWS y capturar evidencias de Swagger, Git y funcionamiento para el TP.
- Integrar frontend y comprobar criterios visuales/accesibilidad/idiomas de US17/US18.
