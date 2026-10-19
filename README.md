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

   Las credenciales de BD se pueden sobreescribir con las variables de entorno `DB_USER` y
   `DB_PASSWORD` (por defecto `root` / `redhat247`).

4. Abrir `http://localhost:8056/auth/login` (o `https://localhost:8443/auth/login` con el perfil HTTPS).

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
- `/backoffice/**`, `/categoria/**`, `/libro/**` y `/prestamo/**`: solo ADMIN y BIBLIOTECARIO.
- `/usuario/**` y `/backoffice/usuarios`: solo ADMIN (ruta + `@PreAuthorize` a nivel de método).
- `/catalogo`: cualquier usuario autenticado (los lectores solicitan préstamos desde ahí).
- `/misprestamos`: solo LECTOR.
- Ocultar opciones en el menú es solo UX: la seguridad real se aplica en el servidor.

## Captcha (reCAPTCHA v2)

- El registro y el login validan el captcha **en el servidor** (POST a `siteverify` de Google).
- En desarrollo se usan las **claves de prueba** de Google (siempre aprueban y muestran una advertencia). Para producción, reemplazar por claves reales en `application.properties` (`recaptcha.site.key` / `recaptcha.secret.key`).
- Si el captcha no se resuelve, el login se rechaza antes de autenticar y el registro no crea al usuario.

## HTTPS local

El perfil `https` sirve la aplicación por TLS en el puerto 8443 con un keystore autofirmado local.
El keystore **no se versiona** (`keystore/` está en `.gitignore`): cada entorno lo genera una vez.

1. Generar el keystore (requiere `keytool`, incluido en el JDK):

   ```bash
   keytool -genkeypair -alias biblioteca -keyalg RSA -keysize 2048 \
     -storetype PKCS12 -keystore keystore/biblioteca.p12 \
     -storepass biblioteca -keypass biblioteca \
     -dname "CN=localhost, OU=IDAT, O=Gestion Biblioteca, L=Lima, C=PE" -validity 3650
   ```

   Si `keytool` no está en el `PATH`, usar la ruta completa del JDK, por ejemplo
   `& "C:\Program Files\Java\jdk-25.0.4\bin\keytool.exe" -genkeypair ...`.

2. Ejecutar con el perfil:

   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=https
   ```

3. Abrir `https://localhost:8443/auth/login` y aceptar la advertencia del certificado autofirmado.

Con HTTPS activo, Spring Security agrega el header **HSTS** y la cookie de sesión se marca
`Secure`, además de `HttpOnly` y `SameSite=Lax`. En producción se reemplaza el certificado
autofirmado por uno real (p. ej. Let's Encrypt) y se mantiene HTTP solo como redirección.

## Mantenimientos (backoffice)

- Categorías y libros: ADMIN y BIBLIOTECARIO. Usuarios: solo ADMIN.
- Patrón JSON + modales: las tablas y formularios llaman por AJAX a `/categoria/*`, `/libro/*`, `/usuario/*` y `/prestamo/*`.
- Todas las respuestas usan el sobre `{respuesta, mensaje, data}` y los AJAX envían el token CSRF (meta tag en el layout).

## Validación de entradas y sanitización (SQLi / XSS)

- **Bean Validation (Jakarta)** en el servidor, con DTOs anotados y `@Valid` + `BindingResult`:
  - Registro: usuario de 4–15 caracteres (letras, números, `.`, `_`, `-`), email válido,
    password de 8–30 con al menos una letra y un número, nombres/apellidos de 2–15.
  - Categorías: nombre de 2–80, descripción de hasta 255.
  - Libros: título obligatorio de hasta 200, autor de hasta 150, ISBN de hasta 20,
    año 0–2100, stock 0–9999 y categoría existente.
- **SQLi**: todo el acceso a datos pasa por Spring Data JPA (consultas parametrizadas); no se concatenan cadenas SQL.
- **XSS**: Thymeleaf escapa con `th:text`/`th:value`; las tablas AJAX se construyen con `.text()` de jQuery (nunca `innerHTML` con datos del servidor).
- **Reglas de negocio**: duplicados de categoría, dependencias antes de eliminar y control de stock/préstamos.

## Checklist de demostración del logro

1. **Validación de entradas**: registrar un usuario con email inválido, password corta o usuario con
   espacios → la pantalla muestra los errores y el usuario no se crea. Probar `' OR 1=1 --` en un
   campo (sin efecto) y `<script>alert(1)</script>` como nombre de categoría (se muestra como texto).
2. **Control de acceso**: `SELECT LEFT(password, 10) FROM usuario;` → `$argon2id$`; entrar como
   `demo` y abrir `/prestamo/listar` → 403; revisar que la cookie `JSESSIONID` sea `HttpOnly`.
3. **Aseguramiento de datos**: con el perfil HTTPS abrir `https://localhost:8443`, verificar el
   candado, el header `Strict-Transport-Security` y la cookie de sesión `Secure`.
4. **Sesión**: expira a los 15 minutos de inactividad; el logout invalida la sesión y borra la cookie.

## Estado del proyecto

- [x] Fase 1 — Esqueleto: login / registro / logout con Spring Security y Argon2
- [x] Fase 2 — Roles (ADMIN, BIBLIOTECARIO, LECTOR), autorización y home por rol
- [x] Fase 3 — reCAPTCHA v2 en registro y login (validación server-side)
- [x] Fase 4 — Dominio (Categoría, Libro, Préstamo) y mantenimientos (JSON + modales)
- [x] Fase 5 — Préstamos (autoservicio del lector y en mostrador) con control de stock, catálogo y mis préstamos
