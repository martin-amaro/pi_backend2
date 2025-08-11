# Stocker Backend

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)

## **Instalación**

Para instalar y ejecutar el proyecto en tu máquina local, sigue estos pasos:

1. **Clona el repositorio**:

    ```bash
    git clone https://github.com/martin-amaro/pi_backend2.git
    ```

2. **Accede a la carpeta del proyecto**:
    ```bash
    cd pi_backend2
    ```

2. **Genera el archivo de variables de entorno `.env`**:

    ```bash
    cp .env.example .env
    ```

3. **Configura las credenciales de la base de datos en Postgres en el archivo `.env`**:

    ```bash
    DB_PASSWORD=
    DB_USERNAME=
    DB_URL=
    ```

    Puedes obtener credenciales gratuitas en [Supabase](https://supabase.com/).

4. **Inicia el proyecto**:

    Abre y ejecuta `Pib2Application.java`. Luego accede a [localhost:8080](http://localhost:8080/) desde cualquier navegador.




    ##############################################################################################################################


    # UNA VEZ TENGAS YA EL PROYECTO  EN TU EQUIPO TE DEBERIAN SALIR ESTOS DATOS  


    # Proyecto: Gestión de Productos

##  Fase 1 – Análisis de Negocio

### Recurso principal
**Producto**  
Atributos minimos:
- `id`
- `nombre`
- `descripcion`
- `precio`
- `stock`
- `categoriaId`
- `activo`

### Relaciones
- Una categoría tiene muchos productos.
- Cada producto pertenece a una categoría.

---

##  Fase 2 – Diseño MVC

### Capas y responsabilidades

| Capa        | Responsabilidad                  | Elemento diseñado |
|-------------|----------------------------------|-------------------|
| **Modelo**  | Persistencia + reglas de negocio | Entidad: `Producto`<br>Regla: "No permitir stock negativo" |
| **Vista**   | Representación JSON               | DTO: `ProductoRequest`, `ProductoResponse` |
| **Controlador** | Definir y manejar endpoints  | Clase: `ProductoController` |

---

### Endpoints

| Endpoint           | Método | Descripción                | Código Éxito     | Código Error |
|--------------------|--------|----------------------------|------------------|--------------|
| `/productos`       | GET    | Listar todos los productos | `200 OK`         | -            |
| `/productos`       | POST   | Crear nuevo producto       | `201 Created + Location` | `400 Bad Request` |
| `/productos/{id}`  | GET    | Obtener producto por ID    | `200 OK`         | `404 Not Found` |
| `/productos/{id}`  | PUT    | Actualizar producto        | `200 OK`         | `404 Not Found` |

---

### Reglas de negocio
1. Un producto no puede tener un stock negativo.
2. El nombre del producto debe ser único dentro de la categoría.
3. Si un producto está inactivo (`activo: false`), no puede aparecer en listados públicos.

---

##  Fase 3 – Diagrama de Secuencia

*(Pendiente de añadir)*

---

##  Mock de Respuesta JSON
```json
{
  "id": 1,
  "nombre": "Producto Ejemplo",
  "descripcion": "Descripción del producto",
  "precio": 100.0,
  "stock": 50,
  "categoriaId": 2,
  "activo": true
}
