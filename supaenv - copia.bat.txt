@echo off
setlocal enabledelayedexpansion

:: --- Pide la URL ---
set /p FULL_URL=Introduce la URL de conexion (ejemplo: postgresql://usuario:[PASSWORD]@host:5432/postgres): 

:: --- Pide la clave ---
set /p DB_PASS=Introduce la contraseña para la base de datos: 

:: --- Parsear la URL ---
:: 1. Quitamos el "postgresql://"
set TMP=%FULL_URL:postgresql://=%

:: 2. Separamos en "usuario:clave@host:puerto/base"
for /f "tokens=1* delims=@" %%A in ("%TMP%") do (
    set PART1=%%A
    set PART2=%%B
)

:: 3. Sacar usuario (antes de :)
for /f "tokens=1 delims=:" %%A in ("%PART1%") do (
    set DB_USER=%%A
)

:: 4. Sacar host, puerto y dbname
for /f "tokens=1,2 delims=/" %%A in ("%PART2%") do (
    set HOST_PORT=%%A
    set DB_NAME=%%B
)

:: 5. Host y puerto separados
for /f "tokens=1,2 delims=:" %%A in ("%HOST_PORT%") do (
    set DB_HOST=%%A
    set DB_PORT=%%B
)

:: --- Crear el archivo .env ---
(
echo SPRING_PROFILES_ACTIVE=dev
echo DB_USERNAME=%DB_USER%
echo DB_PASSWORD=%DB_PASS%
echo.
echo # conexion a la base de datos
echo DB_URL=jdbc:postgresql://%DB_HOST%:%DB_PORT%/%DB_NAME%
echo.
echo # puerto de tu aplicacion Spring
echo PORT=8080
) > .env

echo Archivo .env generado correctamente.
pause
