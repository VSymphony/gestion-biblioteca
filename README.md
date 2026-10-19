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

## Estado del proyecto

- [x] Fase 1 — Esqueleto: login / registro / logout con Spring Security y Argon2
- [ ] Fase 2 — Roles (ADMIN, BIBLIOTECARIO, LECTOR) y autorización
- [ ] Fase 3 — reCAPTCHA
- [ ] Fase 4 — Dominio (Categoría, Libro, Préstamo) y mantenimientos
- [ ] Fase 5 — Flujo de préstamos y catálogo
