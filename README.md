# Sistema para la Gestión y Emisión de Certificados para una Escuela de Oficios

### Seminario de Práctica – Licenciatura en Informática

---

## 📚 Descripción

Aplicación de escritorio que automatiza la carga, gestión y emisión de certificados oficiales para el CFP 402.
## 🛠️ Stack tecnológico

* **Java** – Interfaz gráfica con **Swing**
* **MySQL** – Persistencia de datos
* **JDBC** – Capa de acceso a datos

## 📂 Estructura del repositorio

```
├── app/          # Código fuente Java (UI, lógica, DAO)
└── db/           # Scripts SQL para crear y poblar la base
```

## 🚀 Puesta en marcha rápida

1. **Clonar el repo**

   ```bash
   git clone https://github.com/agustinmme/seminario-s21.git
   cd certificados-cfp402
   ```
2. **Base de datos**

   * Ejecutar tablas.sql
   * Ejecutar poblar.sql
3. **Configurar conexión**

Completar los String conection en DatabaseConfig

4. **Compilar y correr** 

   Descargar proyecto, agregar a espacio de trabajo en netbeans y "RUN PROJECT"

## 🧩 Módulos principales

| Módulo   | Descripción                                                                   |
| -------- | ----------------------------------------------------------------------------- |
| UI       | Pantallas Swing para gestión de alumnos, cursos y emisión PDF de certificados |
| DAO      | Objetos de acceso a datos vía JDBC                                            |
| Reportes | Generación de certificados en formato PDF                                     |


