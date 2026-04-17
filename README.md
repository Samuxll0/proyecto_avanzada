# Sistema de Triage y Gestión de Solicitudes Académicas 🎓

Este sistema es una solución robusta para la centralización, clasificación y gestión de solicitudes académicas. Utiliza un motor de reglas de negocio para el triage y trazabilidad completa del ciclo de vida de cada trámite, apoyado por inteligencia artificial para la asistencia operativa.

## Tecnologías Principales

- **Backend:** Java 21 con **Spring Boot 3.5.13**
- **Seguridad:** Spring Security con **JWT** (JSON Web Tokens)
- **Persistencia:** Spring Data JPA + Hibernate
- **Base de Datos:** MySQL
- **Documentación:** OpenAPI 3.0 (Swagger)
- **Asistencia:** Mock AI Service (Arquitectura lista para integración con LLMs)

## Requisitos Previos

- **Java 21** instalado.
- **MySQL Server** en ejecución.
- Una base de datos llamada `triagre_db` (el sistema la creará automáticamente si no existe gracias a los parámetros en la URL de conexión).

## Configuración Inicial

El archivo `src/main/resources/application.properties` contiene la configuración de conexión. Por defecto está configurado para:

- **Puerto:** 8080
- **Usuario DB:** `root`
- **Contraseña DB:** `<contraseña_personal>`
- **Estrategia DDL:** `update` (mantiene los datos entre reinicios).

## Cómo Ejecutar el Proyecto

1.  Clona el repositorio o abre la carpeta en tu IDE preferido.
2.  Asegúrate de que MySQL esté activo.
3.  Ejecuta el comando en la terminal raíz:
    ```powershell
    .\mvnw spring-boot:run
    ```

## Cuentas de Prueba (Seeder)

Al iniciar, el sistema crea automáticamente un **Coordinador** maestro y los catálogos base:

- **Usuario:** `admin@coordinacion.triagre.com`
- **Password:** `admin123`
- **Rol:** `COORDINADOR`

_Nota: Estudiantes, Docentes y Administrativos pueden registrarse libremente usando el endpoint `/auth/register` siempre que usen sus dominios institucionales (@estudiante.triagre.com / @docente.triagre.com / @administrativo.triagre.com)._

## Flujo de Trabajo (Triage)

1.  **Registro (RF-01):** Un estudiante crea una solicitud (Estado: `REGISTRADA`).
2.  **Clasificación (RF-02/03):** El coordinador asigna tipo, prioridad y justifica la decisión (Estado: `CLASIFICADA`).
3.  **Asignación (RF-05):** El coordinador asigna un responsable activo (Estado: `EN_ATENCION`).
4.  **Atención (RF-08):** Se procesa la solicitud y se marcan comentarios (Estado: `ATENDIDA`).
5.  **Cierre (RF-08):** Cierre formal del caso. No se permiten más modificaciones (Estado: `CERRADA`).

## Funciones de IA Incluidas

- **Resumen de Historial:** Genera una explicación textual inteligente de todos los cambios que ha sufrido una solicitud.
- **Sugerencia de Triage:** Analiza el texto de la solicitud y sugiere automáticamente el tipo y prioridad más adecuados (Asistente).

## Colección de Postman

En la raíz del proyecto encontrarás el archivo `postman_collection.json`. Importalo en Postman para probar todos los endpoints con ejemplos de payloads listos.
