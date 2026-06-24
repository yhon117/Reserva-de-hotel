# Recerva Hotel

Sistema de gestión hotelera con autenticación JWT, administración de habitaciones, reservas, pagos y facturación. Desarrollado con Spring Boot 4 y frontend basado en SB Admin 2.

---

## ✨ Características

- **Autenticación y autorización** con JWT y 3 roles: ADMIN, RECEPCIONISTA, HUESPED
- **CRUD completo** de usuarios, habitaciones, reservas, pagos y facturas
- **Generación de facturas en PDF** con cálculo de IVA (19%)
- **Dashboard** con estadísticas de ocupación e ingresos mensuales (gráficos)
- **Cancelación de reservas** con liberación automática de habitaciones
- **Programación automática** para finalizar reservas expiradas (cada hora)
- **API RESTful** con validación y manejo global de errores
- **Interfaz web** responsiva basada en SB Admin 2 (Bootstrap 5)

---

## 🛠 Stack Tecnológico

| Tecnología | Versión |
|---|---|
| Java | 26 |
| Spring Boot | 4.0.6 |
| Spring Security | 6.x |
| Spring Data JPA / Hibernate | 3.x |
| MySQL | 8+ |
| H2 (test) | — |
| JWT (jjwt) | 0.12.6 |
| MapStruct | 1.6.3 |
| Lombok | 1.18.46 |
| OpenPDF | 2.0.3 |
| Maven | 3.9+ |
| SB Admin 2 | Bootstrap 5 |





## 🖥 Vistas Web

| Página | Ruta | Descripción |
|---|---|---|
| `login.html` | `/login.html` | Inicio de sesión |
| `dashboard.html` | `/dashboard.html` | Panel principal con estadísticas |
| `usuarios.html` | `/usuarios.html` | Gestión de usuarios |
| `habitaciones.html` | `/habitaciones.html` | Gestión de habitaciones |
| `reservas.html` | `/reservas.html` | Gestión de reservas |
| `mis-reservas.html` | `/mis-reservas.html` | Reservas del huésped |
| `pagos.html` | `/pagos.html` | Registro de pagos |
| `facturas.html` | `/facturas.html` | Facturación |
| `estadisticas.html` | `/estadisticas.html` | Estadísticas y gráficos |

---

## 📁 Estructura del Proyecto

```
reserva-hotel/
├── pom.xml
├── mvnw / mvnw.cmd
├── bitacora.txt
├── src/
│   └── main/
│       ├── java/com/protec/recervhotel/
│       │   ├── RecervHotelApplication.java
│       │   ├── controller/    → 5 controladores REST
│       │   ├── service/       → 5 servicios
│       │   ├── repository/    → 5 repositorios JPA
│       │   ├── persistencia/  → DAOs (6 clases)
│       │   ├── entities/      → 6 entidades
│       │   ├── dto/           → 15 DTOs
│       │   ├── mappers/       → 5 mappers MapStruct
│       │   ├── enums/         → 6 enums
│       │   ├── security/      → 4 clases (JWT, filter, config, UserDetails)
│       │   ├── exception/     → 3 clases (handler, excepciones)
│       │   └── scheduler/     → 1 clase (tarea programada)
│       └── resources/
│           ├── application.properties
│           └── static/        → SB Admin 2 (HTML, JS, CSS, vendor)
└── target/                    → compilado
```

---

## 🧪 Notas Técnicas

- **Spring Boot 4.0.6** con Java 26 — requiere JDK 26+
- **Spring Security 6.x** con `@EnableMethodSecurity` y `@PreAuthorize`
- **JWT** con llave HMAC-SHA256 (256 bits), expiración configurable (por defecto 24h)
- **MapStruct + Lombok** trabajan juntos mediante `lombok-mapstruct-binding`
- **OpenPDF 2.0.3** genera facturas en PDF con datos de la reserva
- **Scheduler** (`@EnableScheduling`) ejecuta cada hora `0 0 * * * *` para finalizar reservas cuya fecha de salida ya pasó
- **CORS** habilitado para desarrollo local

---

## 🏗 Arquitectura

```
com.protec.recervhotel
├── controller/       → Controladores REST (endpoints)
├── service/          → Lógica de negocio
├── repository/       → Repositorios Spring Data JPA
├── persistencia/     → Implementaciones DAO
├── entities/         → Entidades JPA (Usuario, Habitacion, Reserva, Pago, Factura)
├── dto/              → Objetos de transferencia (request/response)
├── mappers/          → MapStruct (conversión Entity ↔ DTO)
├── enums/            → Enumeraciones (Rol, Estado, TipoHab, EstadoHab, MetodoPago, EstadoPago)
├── security/         → JWT, filtro de autenticación, config
├── exception/        → Manejo global de excepciones
├── scheduler/        → Tareas programadas (reservas expiradas)
└── RecervHotelApplication.java
```

---

## 📊 Modelo de Datos

### Entidades

| Entidad | Descripción | Relaciones |
|---|---|---|
| `Usuario` | Usuarios del sistema | 1 → N con Reserva |
| `Habitacion` | Habitaciones del hotel | 1 → N con Reserva |
| `Reserva` | Reservas de habitaciones | N → 1 con Usuario, N → 1 con Habitacion, 1 → N con Pago, 1 → 1 con Factura |
| `Pago` | Pagos registrados | N → 1 con Reserva |
| `Factura` | Factura de una reserva | 1 → 1 con Reserva, 1 → N con FacturaItem |
| `FacturaItem` | Items de la factura | N → 1 con Factura |

### Enumeraciones

| Enum | Valores |
|---|---|
| `Rol` | `ADMIN`, `RECEPCIONISTA`, `HUESPED` |
| `Estado` (Reserva) | `PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `COMPLETADA` |
| `EstadoHab` | `DISPONIBLE`, `OCUPADA`, `MANTENIMIENTO` |
| `TipoHab` | `SIMPLE`, `DOBLE`, `SUITE`, `FAMILIAR` |
| `MetodoPago` | `EFECTIVO`, `TARJETA_CREDITO`, `TARJETA_DEBITO`, `TRANSFERENCIA` |
| `EstadoPago` | `PENDIENTE`, `PAGADO`, `PARCIAL`, `REEMBOLSADO` |

---

## 👥 Roles y Permisos

| Rol | Acceso |
|---|---|
| **ADMIN** | Acceso total: CRUD usuarios, habitaciones, reservas, pagos, facturas, estadísticas |
| **RECEPCIONISTA** | Gestionar reservas, pagos y facturas. No puede crear/eliminar usuarios ni habitaciones |
| **HUESPED** | Ver y cancelar sus propias reservas, ver sus facturas, actualizar su perfil |

---

## 🚀 Guía Rápida

### Requisitos

- Java 26+
- MySQL 8+
- Maven 3.9+

### Instalación

```bash
# 1. Clonar
git clone https://github.com/tu-usuario/recerva-hotel.git
cd reserva-hotel

# 2. Crear base de datos
mysql -u root -e "CREATE DATABASE reserva_hotel;"

# 3. Compilar y ejecutar
./mvnw spring-boot:run
```

La aplicación arranca en `http://localhost:8080`.

### Configuración

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/reserva_hotel
spring.datasource.username=root
spring.datasource.password=tu_password

jwt.secret=RecervHotelSecretKey2026MustBeAtLeast256BitsLongForHS256Algorithm!!
jwt.expiration-ms=86400000

factura.iva=19
factura.hotel.nombre=Recerva Hotel
factura.hotel.direccion=Av. Principal 123
factura.hotel.telefono=+51 999 999 999
factura.hotel.email=info@recervhotel.com
```

---

## 🔐 Credenciales por Defecto

| Email | Password | Rol |
|---|---|---|
| `admin@recervhotel.com` | `admin123` | ADMIN |
| `recepcion@recervhotel.com` | `recepcion123` | RECEPCIONISTA |
| `huesped@recervhotel.com` | `huesped123` | HUESPED |

---

## 📡 API REST

Base URL: `http://localhost:8080/api`

Todas las rutas protegidas requieren el header:
```
Authorization: Bearer <token>
```

### 🔐 Autenticación

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| POST | `/api/usuarios/login` | ❌ | Iniciar sesión |
| POST | `/api/usuarios/registro` | ❌ | Registro como HUESPED |

**POST /api/usuarios/login**

```json
{
  "email": "admin@recervhotel.com",
  "password": "admin123"
}
```

**Response 200 OK**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "id": 1,
  "nombre": "Admin",
  "email": "admin@recervhotel.com",
  "telefono": "999000000",
  "rol": "ADMIN"
}
```

**POST /api/usuarios/registro**

```json
{
  "nombre": "Juan Pérez",
  "email": "juan@email.com",
  "password": "123456",
  "telefono": "999111222"
}
```

**Response 201 Created**
```json
{
  "id": 2,
  "nombre": "Juan Pérez",
  "email": "juan@email.com",
  "telefono": "999111222",
  "rol": "HUESPED"
}
```

### 👤 Usuarios

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| GET | `/api/usuarios` | ADMIN / RECEPCIONISTA | Listar todos |
| GET | `/api/usuarios/{id}` | ADMIN / RECEPCIONISTA | Obtener por ID |
| POST | `/api/usuarios` | ADMIN | Crear usuario (con rol) |
| PUT | `/api/usuarios/{id}` | ADMIN | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | ADMIN | Eliminar usuario |
| PUT | `/api/usuarios/mi-perfil` | Authenticated | Actualizar mi perfil |
| PUT | `/api/usuarios/{id}/rol` | ADMIN | Cambiar rol |

**GET /api/usuarios**

**Response 200**
```json
[
  {
    "id": 1,
    "nombre": "Admin",
    "email": "admin@recervhotel.com",
    "telefono": "999000000",
    "rol": "ADMIN"
  }
]
```

**POST /api/usuarios** (ADMIN)

```json
{
  "nombre": "Nuevo Recepcionista",
  "email": "recepcion2@recervhotel.com",
  "password": "123456",
  "telefono": "999444555",
  "rol": "RECEPCIONISTA"
}
```

**Response 201**
```json
{
  "id": 5,
  "nombre": "Nuevo Recepcionista",
  "email": "recepcion2@recervhotel.com",
  "telefono": "999444555",
  "rol": "RECEPCIONISTA"
}
```

**PUT /api/usuarios/mi-perfil**

```json
{
  "nombre": "Juan Pérez Actualizado",
  "email": "juan@email.com",
  "password": "nueva123",
  "telefono": "999111333"
}
```

**Response 200**
```json
{
  "id": 2,
  "nombre": "Juan Pérez Actualizado",
  "email": "juan@email.com",
  "telefono": "999111333",
  "rol": "HUESPED"
}
```

### 🛏️ Habitaciones

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| GET | `/api/habitaciones` | Público | Listar todas |
| GET | `/api/habitaciones/{id}` | Público | Obtener por ID |
| POST | `/api/habitaciones` | ADMIN | Crear habitación |
| PUT | `/api/habitaciones/{id}` | ADMIN | Actualizar habitación |
| DELETE | `/api/habitaciones/{id}` | ADMIN | Eliminar habitación |
| GET | `/api/habitaciones/disponibles?tipo=DOBLE` | Público | Disponibles por tipo |
| GET | `/api/habitaciones/disponibles/precio?max=150` | Público | Disponibles por precio máximo |
| GET | `/api/habitaciones/disponibles/capacidad?personas=3` | Público | Disponibles por capacidad |

**POST /api/habitaciones** (ADMIN)

```json
{
  "numero": "101",
  "piso": 1,
  "precioNoche": 120.00,
  "capacidad": 2,
  "tipo": "DOBLE",
  "estado": "DISPONIBLE"
}
```

**Response 201**
```json
{
  "id": 1,
  "numero": "101",
  "piso": 1,
  "precioNoche": 120.00,
  "capacidad": 2,
  "tipo": "DOBLE",
  "estado": "DISPONIBLE"
}
```

**GET /api/habitaciones/disponibles/precio?max=150**

**Response 200**
```json
[
  {
    "id": 1,
    "numero": "101",
    "piso": 1,
    "precioNoche": 120.00,
    "capacidad": 2,
    "tipo": "DOBLE",
    "estado": "DISPONIBLE"
  }
]
```

### 📅 Reservas

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| POST | `/api/reservas` | Público | Crear reserva |
| PUT | `/api/reservas/{id}/cancelar` | ADMIN / RECEPCIONISTA | Cancelar reserva |
| GET | `/api/reservas/mias` | Authenticated | Mis reservas (HUESPED) |
| GET | `/api/reservas` | ADMIN / RECEPCIONISTA | Listar todas (opcional `?habitacionId=`) |
| GET | `/api/reservas/{id}` | ADMIN / RECEPCIONISTA | Obtener por ID |

**POST /api/reservas**

```json
{
  "fechaEntrada": "2026-07-01",
  "fechaSalida": "2026-07-05",
  "usuarioId": 2,
  "habitacionId": 1
}
```

**Response 201**
```json
{
  "id": 1,
  "fechaEntrada": "2026-07-01",
  "fechaSalida": "2026-07-05",
  "total": 480.00,
  "estado": "CONFIRMADA",
  "fechaCreacion": "2026-06-24T10:30:00",
  "usuarioId": 2,
  "habitacionId": 1,
  "usuarioNombre": "Juan Pérez",
  "habitacionNumero": "101"
}
```

**PUT /api/reservas/{id}/cancelar** (ADMIN / RECEPCIONISTA)

**Response 200**
```json
{
  "id": 1,
  "estado": "CANCELADA",
  "fechaEntrada": "2026-07-01",
  "fechaSalida": "2026-07-05",
  "total": 480.00,
  "usuarioNombre": "Juan Pérez",
  "habitacionNumero": "101"
}
```

**GET /api/reservas/mias**

**Response 200**
```json
[
  {
    "id": 1,
    "fechaEntrada": "2026-07-01",
    "fechaSalida": "2026-07-05",
    "total": 480.00,
    "estado": "CONFIRMADA",
    "fechaCreacion": "2026-06-24T10:30:00",
    "usuarioId": 2,
    "habitacionId": 1,
    "usuarioNombre": "Juan Pérez",
    "habitacionNumero": "101"
  }
]
```

### 💰 Pagos

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| POST | `/api/pagos` | ADMIN / RECEPCIONISTA | Registrar pago |
| GET | `/api/pagos` | ADMIN / RECEPCIONISTA | Listar todos (opcional `?reservaId=`) |
| GET | `/api/pagos/{id}` | ADMIN / RECEPCIONISTA | Obtener por ID |

**POST /api/pagos** (ADMIN / RECEPCIONISTA)

```json
{
  "reservaId": 1,
  "monto": 480.00,
  "metodoPago": "TARJETA_CREDITO",
  "observaciones": "Pago completo"
}
```

**Response 201**
```json
{
  "id": 1,
  "reservaId": 1,
  "huespedNombre": "Juan Pérez",
  "habitacionNumero": "101",
  "monto": 480.00,
  "metodoPago": "TARJETA_CREDITO",
  "estadoPago": "PAGADO",
  "codigoTransaccion": "TXN-abc123",
  "fechaPago": "2026-06-24T11:00:00",
  "observaciones": "Pago completo"
}
```

### 📄 Facturas

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| GET | `/api/facturas/mias` | Authenticated | Mis facturas (HUESPED) |
| GET | `/api/facturas` | ADMIN / RECEPCIONISTA | Listar todas |
| GET | `/api/facturas/{id}` | ADMIN / RECEPCIONISTA | Obtener detalle |
| GET | `/api/facturas/por-reserva/{reservaId}` | ADMIN / RECEPCIONISTA | Factura por reserva |
| GET | `/api/facturas/{id}/pdf` | Público | Descargar PDF |

**GET /api/facturas/{id}**

**Response 200**
```json
{
  "id": 1,
  "numeroFactura": "FAC-2024-0001",
  "reservaId": 1,
  "huespedNombre": "Juan Pérez",
  "huespedEmail": "juan@email.com",
  "habitacionNumero": "101",
  "habitacionTipo": "DOBLE",
  "fechaEntrada": "2026-07-01",
  "fechaSalida": "2026-07-05",
  "noches": 4,
  "subtotal": 403.36,
  "iva": 76.64,
  "total": 480.00,
  "pagada": false,
  "fechaEmision": "2026-06-24T11:05:00",
  "items": [
    {
      "id": 1,
      "descripcion": "Alojamiento - Habitación 101 (4 noches)",
      "cantidad": 4,
      "precioUnitario": 100.84,
      "total": 403.36
    }
  ]
}
```

**GET /api/facturas/{id}/pdf** → Descarga el archivo `factura-{id}.pdf`

### 📊 Estadísticas (ADMIN / RECEPCIONISTA)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/reservas/estadisticas/ocupadas?fecha=2026-07-01` | Habitaciones ocupadas en una fecha |
| GET | `/api/reservas/estadisticas/ingresos-por-mes` | Ingresos agrupados por mes |
| GET | `/api/reservas/estadisticas/ocupacion-por-mes` | Ocupación agrupada por mes |
| GET | `/api/reservas/estadisticas/tendencia?desde=2026-01-01` | Tendencia de reservas |
| GET | `/api/reservas/estadisticas/habitaciones-mas-reservadas` | Ranking de habitaciones más reservadas |

---

