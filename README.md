# Pedidos360 — Pedidos Service

Microservicio backend para la gestión de pedidos del sistema **Pedidos360**, desarrollado con Java 21 y Spring Boot 4.1.1.

Permite administrar pedidos mediante una API REST protegida con Microsoft Entra ID, almacenar información en una base de datos MySQL/MariaDB e integrar comunicación asíncrona mediante RabbitMQ.

## 1. Descripción general

Pedidos Service forma parte de la arquitectura de microservicios de Pedidos360.

Sus principales responsabilidades son:

- Gestionar pedidos mediante operaciones CRUD.
- Persistir información utilizando Spring Data JPA y MySQL/MariaDB.
- Proteger los endpoints REST mediante JWT y OAuth2 Resource Server.
- Validar tokens emitidos por Microsoft Entra ID.
- Publicar eventos relacionados con la creación de pedidos.
- Consumir eventos de RabbitMQ.
- Procesar mensajes utilizando ACK y NACK.
- Gestionar errores, reintentos y mensajes fallidos mediante Dead Letter Queue (DLQ).
- Exponer información de monitoreo mediante Spring Boot Actuator.

## 2. Tecnologías utilizadas

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje de programación |
| Spring Boot 4.1.1 | Framework backend |
| Spring Web MVC | API REST |
| Spring Data JPA | Persistencia |
| MySQL/MariaDB | Base de datos |
| Spring Security | Seguridad |
| OAuth2 Resource Server | Validación de JWT |
| Microsoft Entra ID | Autenticación |
| RabbitMQ | Mensajería asíncrona |
| Spring AMQP | Integración con RabbitMQ |
| Spring Boot Actuator | Monitoreo |
| Maven | Gestión de dependencias |
| JUnit 5 y Mockito | Pruebas unitarias |
| Docker | Ejecución local de RabbitMQ |

## 3. Arquitectura

Pedidos Service se comunica con otros componentes del sistema mediante peticiones REST y eventos RabbitMQ.

```text
Frontend Angular
       |
       | HTTP + JWT
       v
Pedidos Service
       |
       +---- MySQL/MariaDB
       |
       +---- RabbitMQ
                |
                +---- Cola de Pedidos
                |
                +---- Cola de Usuarios
                |
                +---- Dead Letter Queues
```

RabbitMQ permite desacoplar el procesamiento de eventos de la operación HTTP que los origina.

## 4. Gestión de pedidos

El microservicio permite:

- Crear pedidos.
- Consultar todos los pedidos.
- Buscar pedidos por identificador.
- Actualizar pedidos.
- Eliminar pedidos.

### Ejemplo de pedido

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

La información se administra mediante las capas de controlador, servicio, repositorio y modelo.

## 5. Endpoints REST

La API utiliza el prefijo:

`/api/pedidos`

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/pedidos` | Listar pedidos |
| GET | `/api/pedidos/{id}` | Consultar pedido |
| POST | `/api/pedidos` | Crear pedido |
| PUT | `/api/pedidos/{id}` | Actualizar pedido |
| DELETE | `/api/pedidos/{id}` | Eliminar pedido |

Los endpoints protegidos requieren un token JWT válido.

### Ejemplo de creación

```http
POST /api/pedidos
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "cliente": "Luis Pérez",
  "producto": "Mouse Gamer",
  "cantidad": 2,
  "estado": "PENDIENTE",
  "direccionEntrega": "Calle Falsa 123"
}
```

## 6. Seguridad con Microsoft Entra ID

El proyecto utiliza Spring Security y OAuth2 Resource Server para validar tokens JWT emitidos por Microsoft Entra ID.

La configuración contempla:

- Validación del emisor del token.
- Validación de audiencia.
- Protección de endpoints REST.
- Autorización mediante scopes.
- Configuración CORS para el frontend Angular.

Las peticiones protegidas deben incluir:

```http
Authorization: Bearer <token>
```

El frontend local utiliza:

`http://localhost:4200`

## 7. Integración con RabbitMQ

RabbitMQ se utiliza para implementar comunicación asíncrona entre microservicios.

Cuando se genera un evento de creación de pedido, el productor publica un mensaje en el exchange correspondiente.

Los consumidores reciben los eventos desde sus colas y ejecutan la lógica asociada.

### Componentes principales

- `RabbitTopologyProperties`: centraliza propiedades de la topología.
- `RabbitTopologyConfig`: configura colas, exchanges y bindings.
- `PedidoCreadoEvent`: define la estructura del evento.
- `PedidoEventPublisher`: publica eventos.
- `PedidoNotificationListener`: consume y procesa mensajes.

### Topología de RabbitMQ

| Componente | Nombre |
|---|---|
| Exchange principal | `pedidos360.events` |
| Tipo de exchange | Topic |
| Routing key | `pedido.creado` |
| Cola principal de Pedidos | `pedidos.pedido-creado.q` |
| Cola DLQ de Pedidos | `pedidos.pedido-creado.dlq` |
| Dead Letter Exchange | `pedidos360.dlx` |
| Routing key DLQ | `pedido.creado.dlq` |

Los nombres se gestionan mediante la configuración de la aplicación.

### Evento PedidoCreadoEvent

El evento contiene:

- `eventoId`
- `pedidoId`
- `cliente`
- `producto`
- `cantidad`
- `estado`
- `fechaCreacion`

Ejemplo:

```json
{
  "eventoId": "5ba7e1a7-c218-45fc-bfc8-45907466fbbc",
  "pedidoId": 100,
  "cliente": "Cliente prueba",
  "producto": "Producto RabbitMQ",
  "cantidad": 2,
  "estado": "NUEVO",
  "fechaCreacion": "2026-10-08T17:00:00Z"
}
```

## 8. ACK, NACK, reintentos y DLQ

El consumidor implementa confirmación manual de mensajes.

### Procesamiento correcto

1. RabbitMQ entrega el mensaje.
2. El consumidor recibe el evento.
3. Se valida y procesa su información.
4. Se registra el resultado en los logs.
5. Se confirma mediante ACK.

### Procesamiento con errores

1. RabbitMQ entrega el mensaje.
2. El consumidor detecta un error.
3. Se ejecuta la lógica de reintentos configurada.
4. Si el procesamiento no se recupera, el mensaje se rechaza.
5. El mensaje fallido se envía a la DLQ.

Esto permite aislar mensajes problemáticos y evitar que bloqueen permanentemente la cola principal.

### Registro de eventos

El sistema utiliza logs para identificar:

- Recepción de mensajes.
- Procesamiento correcto.
- Confirmación de pedidos.
- Errores de procesamiento.
- Reintentos.
- Envío de mensajes a la DLQ.

## 9. Base de datos

Pedidos Service utiliza Spring Data JPA con MySQL/MariaDB.

En el entorno local de desarrollo se ha utilizado:

| Parámetro | Valor |
|---|---|
| Host | `127.0.0.1` |
| Puerto | `3306` |
| Base de datos | `pedidos360` |
| Motor local | MariaDB mediante XAMPP |

La conexión puede configurarse mediante propiedades y variables de entorno.

## 10. Requisitos previos

Para ejecutar el proyecto se necesita:

- Java 21.
- Maven 3.9 o superior.
- MySQL/MariaDB en ejecución.
- Base de datos `pedidos360`.
- RabbitMQ disponible.
- Docker, si RabbitMQ se ejecuta mediante contenedor.
- Configuración válida de Microsoft Entra ID.

### RabbitMQ con Docker

Para verificar el contenedor:

```powershell
docker ps
```

Para iniciar el contenedor existente:

```powershell
docker start pedidos360-rabbitmq
```

RabbitMQ utiliza los siguientes puertos locales:

| Puerto | Función |
|---|---|
| 5672 | Comunicación AMQP |
| 15672 | Panel de administración |

Panel web:

`http://localhost:15672`

En el entorno local de pruebas se utiliza `guest/guest`. Estas credenciales deben reemplazarse en entornos productivos.

## 11. Instalación y ejecución

### Clonar el repositorio

```bash
git clone https://github.com/philipp717/Pedidos-Service-Pedidos360.git
cd Pedidos-Service-Pedidos360
```

### Compilar

```powershell
mvn clean compile
```

### Ejecutar

```powershell
mvn spring-boot:run
```

Si la instalación local de MariaDB utiliza el usuario `root` sin contraseña, se puede indicar explícitamente:

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--spring.datasource.password="
```

La aplicación utiliza el puerto:

`http://localhost:8080`

Antes de iniciarla deben estar disponibles la base de datos y RabbitMQ.

## 12. Monitoreo con Spring Boot Actuator

Se encuentra habilitado el endpoint:

```http
GET /actuator/health
```

Ejemplo de respuesta cuando los componentes monitoreados están disponibles:

```json
{
  "status": "UP"
}
```

El monitoreo permite comprobar el estado general del microservicio.

## 13. Pruebas unitarias

Se utilizan JUnit 5 y Mockito para verificar el comportamiento de los componentes.

Entre las pruebas implementadas se encuentra:

`PedidoNotificationListenerTest`

Esta prueba permite verificar el comportamiento del consumidor ante diferentes escenarios.

### Ejecutar pruebas

```powershell
mvn test
```

### Ejecutar pruebas específicas

```powershell
mvn "-Dtest=PedidoNotificationListenerTest" test
```

Las pruebas unitarias del consumidor permiten validar la lógica sin depender de publicar mensajes reales en RabbitMQ.

## 14. Pruebas de integración realizadas

Durante el desarrollo se verificaron los siguientes escenarios:

**Escenario 1: Mensaje válido**

- Publicación de un evento de pedido.
- Recepción desde RabbitMQ.
- Procesamiento del consumidor.
- Registro en logs.
- Confirmación ACK.

**Escenario 2: Mensaje inválido**

- Publicación de un evento con información inválida.
- Detección del error.
- Ejecución de reintentos.
- Envío del mensaje fallido a la DLQ.

**Escenario 3: Administración de mensajes fallidos**

- Consulta de colas y DLQ.
- Reprocesamiento de mensajes desde el administrador RabbitMQ.
- Limpieza de mensajes de prueba.

Las pruebas se realizaron utilizando RabbitMQ en Docker y los microservicios ejecutados localmente.

## 15. Integración con otros microservicios

Pedidos Service forma parte del ecosistema Pedidos360.

Se integra con:

- **Frontend Pedidos360:** interfaz Angular.
- **Usuarios Service:** microservicio que consume eventos relacionados con pedidos.
- **RabbitMQ Admin Service:** administración de colas, exchanges, bindings y mensajes fallidos.
- **RabbitMQ:** intermediario de mensajería.
- **MySQL/MariaDB:** persistencia de información.

La comunicación asíncrona permite que distintos consumidores procesen eventos de manera independiente.

## 16. Consideraciones

- La configuración presentada corresponde al entorno local de desarrollo.
- Los servicios deben disponer de conexión a RabbitMQ y a las dependencias que utilizan.
- Los tokens JWT deben provenir del emisor autorizado.
- Las credenciales y parámetros sensibles no deben almacenarse directamente en el repositorio.
- Las pruebas unitarias complementan, pero no reemplazan, las pruebas de integración.
- El procesamiento de eventos utiliza logs para facilitar la trazabilidad y el diagnóstico de errores.

## 17. Objetivo académico

Este proyecto forma parte de la evaluación de **Desarrollo Cloud Native I**.

Permite demostrar:

- Arquitectura basada en microservicios.
- Integración de RabbitMQ.
- Configuración de colas, exchanges y bindings.
- Publicación y consumo de eventos.
- ACK, NACK, reintentos y DLQ.
- Separación de responsabilidades.
- Administración de recursos RabbitMQ.
- Monitoreo y pruebas automatizadas.
