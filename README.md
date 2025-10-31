# 📱 Ciudadano Digital (Android App)
Aplicación móvil del proyecto Ciudadano Digital.

## 🚀 Configuración del entorno
### 1️. Requisitos previos
- Android Studio Iguana o superior
- Java 11 o superior
- Android SDK 36 (mínimo soportado: 24)
- Gradle 8+
- Un emulador Android o dispositivo físico con Android 7 o superior.

### 2️. Clonar el repositorio
```bash
git clone https://github.com/erickguerra22/CiudadanoDigital_Android
cd CiudadanoDigital_App
```

### 3️. Configurar variables locales
En la raíz del proyecto, completa el archivo `local.properties` siguiendo la base de `local.properties.example` según tu entorno:

*  Ruta del SDK de Android (autogenerado por Android Studio)
```kts
sdk.dir=/ruta/a/tu/android/sdk
```
* Entorno de ejecución (DEV | PROD)
```kts
ENVIRONMENT=DEV
```
* URL base del API Ciudadano Digital
```kts
# Para emulador: http://10.0.2.2:3000/api/
# Para dispositivo físico: http://<IP_LOCAL>:3000/api/
API_URL=http://10.0.2.2:3000/api/
```

**💡NOTA:** 10.0.2.2 representa localhost desde el emulador de Android.

### 4. Compilar el proyecto
Desde Android Studio:
- Abre el proyecto
- Espera a que Gradle sincronice dependencias
- Selecciona tu dispositivo o emulador
- Ejecuta ▶️ Run ‘app’
  
Desde línea de comandos:
```bash
./gradlew assembleDebug
```
## 🧩 Tecnologías utilizadas
- Lenguaje principal: **Kotlin (Compose + plantillas XML)**
- Arquitectura: **MVVM + Repository Pattern**
- Inyección de dependencias: **Hilt (Dagger 2.57.1)**
- UI / UX: **Jetpack Compose, Material Design 3**
- Persistencia local: **Room**
- Red / API: **Retrofit + Gson + OkHttp Interceptor**
- Tiempo y fechas: **ThreeTenABP** (asegura compatibilidad desde Android 7)
- Navegación: **Navigation Component + Safe Args**
- Gestión de estados: **ViewModel + LiveData / StateFlow**

## ⚙️ Variables de entorno en tiempo de compilación

Estas variables se definen en el archivo build.gradle.kts y son accesibles desde el código Kotlin mediante BuildConfig:

- `BuildConfig.ENVIRONMENT`: Entorno actual de ejecución (Ejemplo: *DEV*)
- `BuildConfig.API_URL`: URL base del API backend (Ejemplo: *http://10.0.2.2:3000/api/*)

## 🌐 Comunicación con la API

La app se conecta al backend **_CiudadanoDigital_API_** mediante Retrofit.

## 🧠 Funcionalidades principales

✅ Registro y autenticación de usuarios  
✅ Chat educativo con IA (basado en API Ciudadano Digital)  
✅ Historial de sesiones y progreso  
✅ Recuperación de contraeña  
✅ Administración de documentos que alimentan al modelo
