@echo off
REM Script para diagnosticar y reparar AVD "Pixel_8_Pro_API_35" en Windows (cmd.exe)
REM Uso: doble clic o ejecutar desde cmd.exe. Muestra salidas y pausa antes/despues.

echo =============================
echo Diagnóstico y reparación AVD
echo AVD: Pixel_8_Pro_API_35
echo =============================

setlocal enabledelayedexpansion

REM Detectar SDK - priorizar ANDROID_HOME que es donde está tu SDK real
set SDK=%ANDROID_HOME%
if "%SDK%"=="" set SDK=%ANDROID_SDK_ROOT%
if "%SDK%"=="" set SDK=%LOCALAPPDATA%\Android\Sdk
if "%SDK%"=="" set SDK=C:\AndroidSDK

echo SDK detectado: "%SDK%"

set ADB="%SDK%\platform-tools\adb.exe"
set EMULATOR="%SDK%\emulator\emulator.exe"

echo.
echo 1) Procesos relacionados con emulador:
tasklist | findstr /I emulator || echo [no emulator.exe process encontrado]
tasklist | findstr /I qemu || echo [no qemu process encontrado]
echo.
echo 2) Dispositivos adb:
if exist %ADB% (
  %ADB% devices
) else (
  echo adb no encontrado en %ADB%
)
echo.
echo 3) Listado de AVDs:
if exist %EMULATOR% (
  %EMULATOR% -list-avds
) else (
  echo emulator.exe no encontrado en %EMULATOR%
)
echo.
set AVD_DIR=%USERPROFILE%\.android\avd\Pixel_8_Pro_API_35.avd
echo 4) Contenido de %AVD_DIR%:
if exist "%AVD_DIR%" (
  dir /a "%AVD_DIR%" | findstr /I "lock"
  echo.
  echo [Mostrando solo archivos lock - para ver todo ejecuta: dir /a "%AVD_DIR%"]
) else (
  echo No existe la carpeta AVD: "%AVD_DIR%"
)
echo.
echo =============================
echo ATENCION: El script cerrara procesos del emulador y borrara archivos .lock
echo Si tienes datos sin guardar en el emulador, cierralos manualmente antes.
echo =============================
echo Pulsa una tecla para continuar o Ctrl+C para cancelar...
pause >nul

echo.
echo =============================
echo Paso 1: Cerrando procesos
echo =============================

echo Intentando cerrar instancias del emulador si existen...
tasklist | findstr /I emulator.exe >nul
if %ERRORLEVEL% EQU 0 (
  echo Matando emulator.exe...
  taskkill /IM emulator.exe /F
) else (
  echo No hay emulator.exe corriendo
)

echo Intentando cerrar procesos qemu...
for /f "tokens=2" %%a in ('tasklist ^| findstr /I qemu 2^>nul') do (
  echo Matando PID %%a (qemu)
  taskkill /PID %%a /F
)

echo Esperando 2 segundos para que los procesos terminen...
timeout /t 2 /nobreak >nul

echo.
echo =============================
echo Paso 2: Forzando apagado via adb
echo =============================
if exist %ADB% (
  for /f "skip=1 tokens=1" %%d in ('%ADB% devices 2^>nul') do (
    echo Dispositivo detectado: %%d
    if not "%%d"=="List" (
      if not "%%d"=="" (
        echo Enviando comando kill a %%d
        %ADB% -s %%d emu kill 2>nul
      )
    )
  )
  echo Esperando 2 segundos...
  timeout /t 2 /nobreak >nul
) else (
  echo adb no disponible: no se puede enviar emu kill
)

echo.
echo =============================
echo Paso 3: Borrando archivos .lock
echo =============================
if exist "%AVD_DIR%" (
  echo Borrando archivos .lock en %AVD_DIR%
  del /F /Q "%AVD_DIR%\*.lock" 2>nul
  if %ERRORLEVEL% EQU 0 (
    echo Archivos .lock borrados correctamente
  ) else (
    echo No se pudieron borrar algunos archivos .lock - puede que aun haya procesos corriendo
  )

  echo Borrando carpeta hardware-qemu.ini.lock si existe...
  if exist "%AVD_DIR%\hardware-qemu.ini.lock" (
    rmdir /S /Q "%AVD_DIR%\hardware-qemu.ini.lock" 2>nul
    if %ERRORLEVEL% EQU 0 (
      echo Carpeta lock borrada correctamente
    ) else (
      echo No se pudo borrar la carpeta lock - cierra Android Studio e intenta de nuevo
    )
  )
) else (
  echo Saltando borrado de .lock porque no existe la carpeta AVD
)

echo.
echo =============================
echo Paso 4: Verificando que no hay procesos
echo =============================
tasklist | findstr /I emulator || echo [OK - no hay emulator.exe]
tasklist | findstr /I qemu || echo [OK - no hay qemu]

echo.
echo =============================
echo Paso 5: Arrancando AVD
echo =============================
echo Arrancando AVD con cold boot (SIN borrar datos)...
echo.
if exist %EMULATOR% (
  echo Ejecutando: %EMULATOR% -avd Pixel_8_Pro_API_35 -no-snapshot-load -verbose
  echo.
  echo NOTA: Si el emulador se queda en "starting up" mas de 3 minutos:
  echo   1. Cierra este script con Ctrl+C
  echo   2. Ve a Android Studio ^> AVD Manager
  echo   3. Click en la flecha del AVD ^> "Cold Boot Now"
  echo   4. Si sigue fallando, ejecuta el script con la opcion -wipe-data
  echo.
  %EMULATOR% -avd Pixel_8_Pro_API_35 -no-snapshot-load -verbose
) else (
  echo ERROR: No puedo arrancar el emulador
  echo emulator.exe no encontrado en %EMULATOR%
  echo.
  echo Verifica que el SDK este instalado correctamente en: %SDK%
  echo O edita este script y cambia la linea "set SDK=" con la ruta correcta
)
echo.
echo El script ha terminado.
pause
endlocal
