# AcademiaAPP (Android, Jetpack Compose)

Aplicación Android cliente para el sistema de academias. Arquitectura MVVM ligera con Compose.

- Login contra API Worker (flavor `dev` usa http://10.0.2.2:5000)
- Chat contra backend mediador (flavor `dev` usa http://10.0.2.2:8080)
- Almacenamiento de sesión en DataStore
- Navegación simple (login → chat)

Desarrollo local (Android Studio):
- Selecciona Build Variant: `devDebug`
- Asegura backends en 10.0.2.2 (emulador) en puertos 5000 y 8080

Pendiente Sprint 1:
- Drawer por rol y popup de selección de academia on‑demand