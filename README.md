# Pedidos360 Backend

Backend del sistema de gestión de pedidos para Pedidos360. La aplicación expone una API REST para crear, consultar, actualizar y eliminar pedidos, con autenticación JWT basada en Microsoft Entra ID (Azure AD) y soporte para CORS desde el frontend local.

## Descripción general

Este proyecto es una API REST desarrollada con Spring Boot que sirve como backend para una aplicación de pedidos. Actualmente utiliza almacenamiento en memoria para simular la persistencia, ideal para desarrollo, pruebas y validación de flujo de negocio antes de integrar una base de datos real.

Incluye:

- CRUD completo de pedidos
- Seguridad con JWT y OAuth2 Resource Server
- Validación de tokens y audiencias
- Endpoints protegidos bajo el scope `SCOPE_access_as_user`
- Health check con Spring Actuator
- CORS configurado para `http://localhost:4200`

## Stack tecnológico

- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Web MVC
- Spring Security
- OAuth2 Resource Server
- Bean Validation
- Spring Actuator
- JUnit 5

## Estructura del proyecto

```text
pedidos360-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── cl/
│   │   │       └── duoc/
│   │   │           └── pedidos360/
│   │   │               ├── config/
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── controller/
│   │   │               │   └── PedidoController.java
│   │   │               ├── model/
│   │   │               │   └── Pedido.java
│   │   │               ├── service/
│   │   │               │   └── PedidoService.java
│   │   │               └── Pedidos360BackendApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── cl/
│               └── duoc/
│                   └── pedidos360/
│                       └── Pedidos360BackendApplicationTests.java
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Funcionalidades principales

### Modelo de negocio

La entidad `Pedido` contiene la siguiente información:

```json
{
  "id": 1,
  "cliente": "Ana Torres",
  "producto": "Notebook",
  "cantidad": 1,
  "estado": "PENDIENTE",
  "direccionEntrega": "Av. Providencia 123"
}
```

### Servicios disponibles

La clase `PedidoService` mantiene una lista en memoria con tres pedidos iniciales y ofrece las operaciones CRUD básicas:

- `listarTodos()`
- `buscarPorId(Long id)`
- `crear(Pedido pedido)`
- `actualizar(Long id, Pedido pedido)`
- `eliminar(Long id)`

## Configuración de seguridad

La seguridad está configurada en `SecurityConfig` y usa JWT OAuth2 con Microsoft Entra ID.

### Propiedades principales

Archivo: `src/main/resources/application.properties`

```properties
spring.application.name=pedidos360-backend
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://login.microsoftonline.com/3441157d-ea5c-483f-a66d-e45c3ed7f9da/v2.0
pedidos360.security.jwt.audience=5582b6c4-7ecd-4bed-9337-ba3f1f8e58e5
```

### Reglas de acceso

- `OPTIONS /api/**` permitidos
- `GET /actuator/health` permitido
- Todas las rutas bajo `/api/**` requieren el scope `SCOPE_access_as_user`
- El resto de rutas requieren autenticación

### CORS

La aplicación acepta peticiones desde:

```text
http://localhost:4200
```

## Endpoints de la API

La API está montada bajo el prefijo `/api/pedidos`.

### 1. Listar todos los pedidos

```http
GET /api/pedidos
Authorization: Bearer <token>
```

### 2. Buscar pedido por ID

```http
GET /api/pedidos/{id}
Authorization: Bearer <token>
```

### 3. Crear pedido

```http
POST /api/pedidos
Authorization: Bearer <token>
Content-Type: application/json
```

Ejemplo de body:

```json
{
  "cliente": "Luis Pérez",
  "producto": "Mouse Gamer",
  "cantidad": 2,
  "estado": "PENDIENTE",
  "direccionEntrega": "Calle Falsa 123"
}
```

### 4. Actualizar pedido

```http
PUT /api/pedidos/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

### 5. Eliminar pedido

```http
DELETE /api/pedidos/{id}
Authorization: Bearer <token>
```

### 6. Health check

```http
GET /actuator/health
```

## Requisitos previos

- Java 21
- Maven 3.9+
- Acceso a un token JWT válido emitido por el issuer configurado en Azure AD

## Instalación y ejecución

### Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd Pedidos-Service-Pedidos360
```

### Ejecutar con Maven Wrapper

```bash
./mvnw clean install
./mvnw spring-boot:run
```

En Windows:

```powershell
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

La aplicación quedará disponible en:

```text
http://localhost:8080
```

## Ejecutar pruebas

```bash
./mvnw test
```

## Observaciones del proyecto

- El backend es una versión inicial y funcional de demostración.
- La persistencia actual es en memoria, no usa base de datos ni JPA.
- Los datos se reinician al reiniciar la aplicación.
- El sistema está preparado para autenticación real con Azure AD, pero requiere un tenant y un audience válidos para funcionar en entorno real.


