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