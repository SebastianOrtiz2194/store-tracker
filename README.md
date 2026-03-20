# Store Tracker 🏪 (Enterprise Refactored)

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Security](https://img.shields.io/badge/Security-Basic_Auth-red?style=for-the-badge)

Store Tracker es un microservicio alineado con estándares empresariales diseñado para gestionar visitas y compras. Refactorizado para ser robusto, escalable y seguro.

---

## 🚀 Tecnologías y Buenas Prácticas
Este proyecto implementa:
*   **Arquitectura en Capas**: Controller -> Service -> Repository.
*   **Validación de Datos**: Beans Validation (JSR-303).
*   **Manejo Global de Errores**: Centralizado con `@ControllerAdvice`.
*   **Observabilidad**: Spring Boot Actuator y SLF4J.
*   **Documentación**: Swagger UI (OpenAPI 3).
*   **Seguridad**: Basic Auth (Stateless).
*   **Testing**: JUnit 5, Mockito, MockMvc y DataJpaTest (Cobertura completa).

## 📋 Requisitos Previos
*   **Java JDK 17** o superior.
*   **Maven 3.8+**.
*   **Git**.

## 🛠️ Instalación y Ejecución

1. **Clona el repositorio:**
   ```bash
   git clone https://github.com/SebastianOrtiz2194/store-tracker.git
   cd store-tracker
   ```

2. **Compila y corre las pruebas:**
   ```bash
   mvn clean test
   ```

3. **Ejecuta la aplicación:**
   #### Perfil de Desarrollo (H2)
   Ideal para pruebas rápidas.
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

   #### Perfil de Producción
   Requiere configuración de variables de entorno (DB externa).
   ```bash
   mvn spring-boot:run "-Dspring-boot.run.profiles=prod"
   ```

## 🔐 Seguridad e Ingreso

El servicio está protegido. Usa estas credenciales para probar los endpoints:
*   **Usuario:** `admin`
*   **Contraseña:** `admin123`

## 📖 Documentación de la API (Swagger)

Una vez iniciada la aplicación, accede a la documentación interactiva:
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## 📊 Monitoreo (Actuator)

Monitorea la salud del servicio en:
*   Health Check: `http://localhost:8080/actuator/health`

## 🌍 Variables de Entorno (Producción)
Si usas el perfil `prod`, configura:
*   `DB_URL`: URL de tu base de datos (PostgreSQL/MySQL).
*   `DB_USERNAME`: Usuario BD.
*   `DB_PASSWORD`: Password BD.
*   `SERVER_PORT`: Puerto (default 8080).

## 🗄️ Base de Datos en Memoria (H2 Console)

Al utilizar el perfil `dev`, puedes acceder a la consola de administración de la base de datos:
*   **URL:** `http://localhost:8080/h2-console`
*   **JDBC URL:** `jdbc:h2:mem:testdb`
*   **Username:** `sa`
*   **Password:** *(vacío)*

## 🧪 Pruebas con Postman

En la raíz del proyecto encontrarás el archivo `Store_Tracker_Postman_Collection.json`.
1. Impórtalo en [Postman](https://www.postman.com/).
2. Configura la **Basic Auth** en la colección con las credenciales `admin`/`admin123`.
3. Ejecuta todos los requests predefinidos.

---
*Desarrollado bajo estándares de ingeniería de software de alta calidad.*
*Desarrollado para la gestión eficiente de visitas en un shop.*
