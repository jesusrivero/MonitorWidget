
💰 MonitorWidget

[![Kotlin](https://img.shields.io/badge/Kotlin-FF5722?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/Hilt-0D47A1?style=for-the-badge&logo=android&logoColor=white)](https://dagger.dev/hilt/)

📊 MonitorWidget es una aplicación Android desarrollada en Kotlin + Jetpack Compose que muestra el precio del dólar en Venezuela BCV y directamente desde un widget 2x1 en la pantalla de inicio.
Se actualiza automáticamente cada hora utilizando WorkManager y almacena los datos en DataStore para funcionar incluso sin conexión.

✨ Características principales

✅ Actualización automática cada hora con WorkManager.

✅ Widget 2x1 personalizable que muestra:

💵 Dólar BCV (oficial)

✅ Persistencia local mediante DataStore.

✅ Notificaciones inteligentes si la tasa cambia.

✅ Diseño moderno con Material 3 y colores dinámicos.

✅ Modo claro/oscuro seleccionable.

🏗️ Arquitectura

El proyecto sigue el patrón Clean Architecture


🧩 Tecnologías utilizadas

Componente	Descripción

🧱 Jetpack Compose	UI moderna declarativa

🧠 Hilt (Dagger)	Inyección de dependencias

⚙️ WorkManager	Ejecución periódica en segundo plano

☁️ Retrofit + Moshi	Consumo y parseo de la API

💾 DataStore	Almacenamiento local persistente

🔔 NotificationCompat	Notificaciones al usuario

🧩 AppWidgetProvider	Widget en pantalla de inicio

🖼️ Ejemplo visual (mockup)


🚀 Futuras mejoras

📈 Historial de precios con gráficos.

⚡ Sincronización con otras APIs (Euro, Binance, etc).

🧩 Personalización visual del widget.
