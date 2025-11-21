@echo off
echo ========================================
echo CREAR EMULADOR ANDROID MAS RAPIDO
echo ========================================
echo.
echo Este script te ayudara a crear un emulador mas ligero
echo.
echo OPCIONES RECOMENDADAS:
echo.
echo 1. Pixel 5 API 34 (Recomendado - Balance perfecto)
echo 2. Pixel 5 API 33 (Mas rapido, muy estable)
echo 3. Medium Phone API 34 (El mas rapido)
echo.
echo PASOS A SEGUIR EN ANDROID STUDIO:
echo.
echo 1. Abre Android Studio
echo 2. Tools ^> Device Manager (o AVD Manager)
echo 3. Click en "Create Device"
echo 4. Selecciona: Pixel 5 (no Pixel 8 Pro)
echo 5. Click Next
echo 6. Selecciona: API 34 (no API 35) - Descargalo si no lo tienes
echo 7. Click Next
echo 8. IMPORTANTE - Configuracion avanzada:
echo    - RAM: 2048 MB (2GB)
echo    - VM Heap: 512 MB
echo    - Graphics: Hardware - GLES 2.0
echo    - Boot option: Quick Boot
echo 9. Click Finish
echo.
echo ========================================
echo ALTERNATIVA MAS RAPIDA: USA TU CELULAR
echo ========================================
echo.
echo 1. En tu celular Android: Ajustes ^> Acerca del telefono
echo 2. Toca 7 veces en "Numero de compilacion"
echo 3. Vuelve atras ^> Opciones de desarrollo
echo 4. Activa "Depuracion USB"
echo 5. Conecta el celular por USB a la PC
echo 6. Acepta la depuracion en el celular
echo 7. En Android Studio aparecera en la lista de dispositivos
echo.
echo El celular real es 10x mas rapido que el emulador!
echo.
pause

