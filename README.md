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
- `/backoffice/**`: solo ADMIN y BIBLIOTECARIO (a nivel de ruta).
- `/backoffice/usuarios`: solo ADMIN (a nivel de método con `@PreAuthorize`).
- Ocultar opciones en el menú es solo UX: la seguridad real se aplica en el servidor.

## Estado del proyecto

- [x] Fase 1 — Esqueleto: login / registro / logout con Spring Security y Argon2
- [x] Fase 2 — Roles (ADMIN, BIBLIOTECARIO, LECTOR), autorización y home por rol
- [ ] Fase 3 — reCAPTCHA
- [ ] Fase 4 — Dominio (Categoría, Libro, Préstamo) y mantenimientos
- [ ] Fase 5 — Flujo de préstamos y catálogo
