<h1 align="center">App Vota Informado</h1>

<div align="center">
  
  <!-- Logo de la aplicación -->
  <img src="./assets/logo-vota-informado-2.png" alt="VotaInformado Logo" width="400"/>
  
  <p><strong>Información pública y transparente sobre candidatos políticos del Perú</strong></p>

</div>

---

## 📋 Descripción del Proyecto

**VotaInformado** es una aplicación móvil Android que centraliza información pública verificable sobre candidatos al Congreso y la Presidencia del Perú. Permite a los ciudadanos consultar datos oficiales y participar en simulaciones de votación para estimar tendencias electorales.

### Características Principales

- 🔍 Búsqueda de candidatos con datos de fuentes oficiales
- 📊 Historial político, denuncias y proyectos de ley
- 🗳️ Simulador de votación con validación de identidad
- 📈 Estadísticas en tiempo real de preferencias electorales
- 🔗 Enlaces directos a plataformas gubernamentales

### Valor Social

Facilita el acceso ciudadano a información electoral verificable para la toma de decisiones informadas, fortaleciendo la transparencia democrática y reduciendo la desinformación.

---

## 📱 Demo de la Aplicación

### Video Demostrativo

<div align="center">
  
  [![Video Demo](./assets/video_thumbnail.png)](https://youtu.be/tu-video-aqui)
  
  <p><em>Click en la imagen para ver el video completo</em></p>

</div>

### GIF de Funcionalidades

<div align="center">
  
  <img src="./assets/demo.gif" alt="Demo de la aplicación" width="300"/>

</div>

---

## 🎨 Diseño y Prototipo

El diseño de la aplicación fue desarrollado utilizando **Figma**, siguiendo los principios de Material Design 3 para garantizar una experiencia moderna y profesional.

### 🔗 Enlace al Prototipo
[Ver prototipo en Figma](https://figma.com/file/tu-prototipo-aqui)

### Capturas de Pantalla

<div align="center">
  
  <img src="./screenshots/home_screen.png" alt="Pantalla de Inicio" width="250"/>
  <img src="./screenshots/candidate_detail.png" alt="Detalle del Candidato" width="250"/>
  <img src="./screenshots/voting_screen.png" alt="Simulador de Votación" width="250"/>
  
  <p><em>Pantalla de Inicio | Detalle del Candidato | Simulador de Votación</em></p>

</div>

---

## ✨ Funcionalidades Implementadas

### Planificación y Diseño
- ✅ Investigación de fuentes oficiales de información pública
- ✅ Definición del flujo de navegación y arquitectura de información
- ✅ Creación de wireframes y prototipo visual en Figma
- ✅ Diseño de identidad visual con enfoque en transparencia y confianza

### Estructura y Navegación
- ✅ Proyecto configurado con Kotlin y Jetpack Compose
- ✅ Arquitectura MVVM organizada por módulos funcionales
- ✅ Sistema de navegación fluido entre pantallas principales
- ✅ Configuración de control de versiones con Git/GitHub

### Interfaz de Usuario
- ✅ Pantalla de inicio con barra de búsqueda inteligente
- ✅ Lista de candidatos con datos esenciales (foto, partido, cargo)
- ✅ Perfil detallado con información completa del candidato
- ✅ Pantalla de simulador de votación tipo "boca de urna"
- ✅ Diseño Material 3 con paleta coherente y profesional
- ✅ Interfaz responsiva adaptable a diferentes dispositivos

### Lógica y Datos
- ✅ Modelos de datos: Candidato, Proyecto, Denuncia, Voto
- ✅ Repositorio para gestión de datos locales y remotos
- ✅ Integración con API de RENIEC para validación de identidad
- ✅ Sistema de almacenamiento de votos simulados
- ✅ Visualización dinámica de datos en tiempo real

### Funcionalidades Avanzadas
- ✅ Búsqueda y filtrado por nombre, partido, región o cargo
- ✅ Comparador de candidatos lado a lado
- ✅ Sistema de votación simulada con validación de DNI
- ✅ Estadísticas en tiempo real de votaciones "boca de urna"
- ✅ Enlaces directos a fuentes oficiales verificadas
- ✅ Manejo de estados de carga y errores
- ✅ Visualización de historial político completo

### Documentación y Presentación
- ✅ README completo con toda la información del proyecto
- ✅ Documentación técnica de implementación
- ✅ Capturas de pantalla de funcionalidades principales
- ✅ Release v1.0 etiquetado en GitHub

---

## 🛠️ Tecnologías Utilizadas

### Frontend Móvil
- **Kotlin** - Lenguaje de programación principal
- **Jetpack Compose** - Framework UI moderno y declarativo
- **Material Design 3** - Sistema de diseño
- **Navigation Compose** - Navegación entre pantallas
- **ViewModel** - Gestión de estado UI
- **Retrofit** - Cliente HTTP para consumo de APIs
- **Coil** - Carga y caché de imágenes

### Backend y Base de Datos
- **Django REST Framework** - API REST para servicios backend
- **PostgreSQL** - Base de datos relacional
- **API RENIEC** - Validación de identidad ciudadana

### Herramientas de Desarrollo
- **Android Studio** - IDE principal
- **Visual Studio Code** - Editor ligero de código
- **Figma** - Diseño y prototipado
- **Git/GitHub** - Control de versiones

---

## 🚀 Instalación y Ejecución

### Requisitos Previos

- Android Studio Hedgehog o superior
- JDK 17 o superior
- Android SDK con API 26+
- Git instalado

### Pasos de Instalación

1. **Clonar el repositorio:**
```bash
https://github.com/JosepRivera/vota-informado-app.git
cd candidatoinfo
```

2. **Abrir el proyecto en Android Studio:**
   - File → Open → Seleccionar la carpeta del proyecto

3. **Sincronizar dependencias:**
   - Esperar a que Gradle sincronice automáticamente

4. **Ejecutar la aplicación:**
   - Conectar un dispositivo físico o iniciar un emulador
   - Run → Run 'app'

---

## 📊 Fuentes de Información Investigadas

### JNE - Jurado Nacional de Elecciones
- **URL Principal:** https://www.jne.gob.pe
- **Plataforma Electoral:** https://plataformaelectoral.jne.gob.pe
- **Voto Informado:** https://votoinformado.jne.gob.pe
- **Datos disponibles:** Hojas de vida, planes de gobierno, sentencias electorales, declaraciones juradas

### Infogob - Observatorio para la Gobernabilidad
- **URL:** https://infogob.jne.gob.pe
- **Datos disponibles:** Historial político desde 1931, cargos públicos anteriores, trayectoria partidaria, resultados electorales históricos

### ONPE - Oficina Nacional de Procesos Electorales
- **URL Principal:** https://www.onpe.gob.pe
- **Sistema Claridad:** https://claridad.onpe.gob.pe
- **Datos disponibles:** Resultados electorales en tiempo real, financiamiento de campañas, aportes y gastos electorales

### Poder Judicial del Perú
- **URL Principal:** https://www.pj.gob.pe
- **Consulta de Expedientes:** https://cej.pj.gob.pe/cej/forms/busquedaform.html
- **REDAM:** https://casillas.pj.gob.pe/redam/
- **Datos disponibles:** Expedientes judiciales, sentencias, registro de deudores alimentarios

### Contraloría General de la República
- **URL Principal:** https://www.contraloria.gob.pe
- **Sistema de DDJJ:** https://apps1.contraloria.gob.pe/ddjj/
- **Datos disponibles:** Declaración jurada de ingresos y bienes, información patrimonial de funcionarios

### Congreso de la República
- **URL Principal:** https://www.congreso.gob.pe
- **Proyectos de Ley:** https://www.congreso.gob.pe/proyectosdeley
- **Asistencias y Votaciones:** https://www.congreso.gob.pe/AsistVotPlenoPermanente/
- **Datos disponibles:** Proyectos de ley por congresista, votaciones nominales, asistencia a sesiones

---

<div align="center">
  
  ⭐ Si este proyecto te parece útil, considera darle una estrella en GitHub
  
</div>
