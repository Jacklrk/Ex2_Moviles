# Ex2_Moviles
Notificaciones
# 📦 Android App con Notificaciones Push y Firebase

Este proyecto es una app Android con autenticación por roles y notificaciones push, usando Firebase y un backend en Node.js.

---

## 🚀 Funcionalidades

- 📲 **Registro e inicio de sesión** con Firebase Authentication.
- 👥 **Roles** de usuario: `Administrador` y `Usuario`.
- 🧑‍💼 **Panel de administrador**:
  - Ver lista de usuarios normales.
  - Editar y eliminar usuarios.
  - Enviar notificaciones individuales o generales.
- 🔔 **Notificaciones push** usando Firebase Cloud Messaging (FCM) HTTP v1.
- 📡 **Backend Node.js** para gestionar envíos mediante Firebase Admin SDK.

---

## 🔧 Tecnologías

| Tecnología | Uso |
|------------|-----|
| Kotlin / Android | Aplicación móvil |
| Firebase Auth | Registro e inicio de sesión |
| Firestore | Base de datos de usuarios |
| FCM | Notificaciones push |
| Firebase Admin SDK | Backend para envío de notificaciones |
| Node.js + Express | Servidor para peticiones FCM HTTP v1 |

---

## 🛠️ Configuración

### 🔥 Firebase

1. Crear un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Habilitar:
   - **Authentication** (email y contraseña)
   - **Firestore Database**
   - **Cloud Messaging**
3. Descargar `google-services.json` y colocarlo en `app/`
4. Crear una cuenta de servicio en `Configuración > Cuentas de servicio` y descargar el `.json` para el backend

---

### 📲 Android

1. Agrega los permisos en `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
