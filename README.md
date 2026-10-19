# Gestión de Biblioteca

Proyecto del curso **Desarrollo de Aplicaciones Seguras** (IDAT — Ciclo VIII).

Aplicación web en **Spring Boot + MySQL** para la gestión de una biblioteca, tomando como
base la app de referencia del curso y cubriendo los requisitos de seguridad obligatorios:
**captcha**, **Argon2**, **Spring Security** y **sanitización** (SQLi/XSS).

## Stack

- Java 25
- Spring Boot 4.1.1 / Spring Security 7
- Spring Data JPA + MySQL 8
- Thymeleaf + Bootstrap + jQuery

## Puesta en marcha

1. Tener MySQL corriendo en `localhost:3306`.
2. Crear la base de datos (una vez):

   ```bash
   mysql -u root -p < db/bibliotecadb.sql
   ```

3. Ejecutar la app:

   ```bash
   ./mvnw spring-boot:run
   ```

4. Abrir `http://localhost:8056/auth/login`.

Los usuarios nuevos que se registran desde la web quedan con el rol **LECTOR**.

## Usuarios de prueba

Se crean automáticamente al arrancar la app (solo si no existen):

| Usuario | Password | Rol |
|---|---|---|
| admin | Admin12345 | ADMIN |
| bibliotecario | Biblio12345 | BIBLIOTECARIO |
| demo | Demo12345 | LECTOR |

## Autorización

- Rutas públicas: login, registro y recursos estáticos.
- `/backoffice/**`, `/categoria/**` y `/libro/**`: solo ADMIN y BIBLIOTECARIO.
- `/usuario/**` y `/backoffice/usuarios`: solo ADMIN (ruta + `@PreAuthorize` a nivel de método).
- Ocultar opciones en el menú es solo UX: la seguridad real se aplica en el servidor.

## Captcha (reCAPTCHA v2)

- El registro y el login validan el captcha **en el servidor** (POST a `siteverify` de Google).
- En desarrollo se usan las **claves de prueba** de Google (siempre aprueban y muestran una advertencia). Para producción, reemplazar por claves reales en `application.properties` (`recaptcha.site.key` / `recaptcha.secret.key`).
- Si el captcha no se resuelve, el login se rechaza antes de autenticar y el registro no crea al usuario.

## Mantenimientos (backoffice)

- Categorías y libros: ADMIN y BIBLIOTECARIO. Usuarios: solo ADMIN.
- Patrón JSON + modales: las tablas y formularios llaman por AJAX a `/categoria/*`, `/libro/*` y `/usuario/*`.
- Todas las respuestas usan el sobre `{respuesta, mensaje, data}` y los AJAX envían el token CSRF (meta tag en el layout).

## Sanitización (SQLi / XSS)

- **SQLi**: todo el acceso a datos pasa por Spring Data JPA (consultas parametrizadas); no se concatenan cadenas SQL.
- **XSS**: Thymeleaf escapa con `th:text`; las tablas AJAX se construyen con `.text()` de jQuery (nunca `innerHTML` con datos del servidor).
- Validación en el servidor: campos obligatorios, longitudes, stock numérico y existencia de categorías/roles antes de guardar.

## Estado del proyecto

- [x] Fase 1 — Esqueleto: login / registro / logout con Spring Security y Argon2
- [x] Fase 2 — Roles (ADMIN, BIBLIOTECARIO, LECTOR), autorización y home por rol
- [x] Fase 3 — reCAPTCHA v2 en registro y login (validación server-side)
- [x] Fase 4 — Dominio (Categoría, Libro, Préstamo) y mantenimientos (JSON + modales)
- [ ] Fase 5 — Flujo de préstamos y catálogo
