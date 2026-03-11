# Store Tracker 🏪

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![H2 Database](https://img.shields.io/badge/H2-Database-003545?style=for-the-badge)

Store Tracker es un microservicio diseñado para registrar, gestionar y monitorizar visitas y compras en tiendas. Construido con **Spring Boot**, provee una API REST eficiente, respaldada por **Spring Data JPA** y una base de datos en memoria **H2** para un despliegue rápido.

---

## 🚀 Tecnologías Principales

*   **Java 17**
*   **Spring Boot 3.2.3**
    *   `spring-boot-starter-web` (API REST)
    *   `spring-boot-starter-data-jpa` (Persistencia / Hibernate)
*   **H2 Database** (Base de datos relacional en memoria)
*   **Maven** (Gestor de dependencias y construcción)

## 📋 Requisitos Previos

Asegúrate de tener instalados los siguientes componentes localmente:

*   [Java Development Kit (JDK) 17+](https://adoptium.net/)
*   [Apache Maven](https://maven.apache.org/)

## 🛠️ Instalación y Ejecución

Sigue estos pasos para obtener una copia local ejecutándose:

1. **Clona el repositorio:**
   ```bash
   git clone https://github.com/SebastianOrtiz2194/store-tracker.git
   cd store-tracker
   ```

2. **Compila el proyecto y descarga las dependencias:**
   ```bash
   mvn clean install
   ```

3. **Ejecuta la aplicación:**
   ```bash
   mvn spring-boot:run
   ```

La aplicación se iniciará por defecto en `http://localhost:8080`.

## 🗄️ Base de Datos en Memoria (H2 Console)

Al utilizar H2, puedes acceder a la consola de administración de la base de datos desde tu navegador:

*   **URL:** `http://localhost:8080/h2-console` (Puede variar según tu `application.properties`)
*   **JDBC URL:** `jdbc:h2:mem:testdb` (Valor por defecto típico de Spring Boot)
*   **Username:** `sa`
*   **Password:** *(vacío por defecto)*

*(Asegúrate de que `spring.h2.console.enabled=true` esté configurado en `src/main/resources/application.properties` para poder acceder).*

## 🧪 Pruebas con Postman

En la raíz de este proyecto encontrarás el archivo `Store_Tracker_Postman_Collection.json`.
Puedes importarlo directamente en [Postman](https://www.postman.com/) para probar rápidamente los endpoints REST integrados.

---
*Desarrollado para la gestión eficiente de visitas.*
