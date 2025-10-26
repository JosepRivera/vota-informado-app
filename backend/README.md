# 🗳️ Vota Informado - Backend API

API REST para simulador electoral desarrollada con Django REST Framework y PostgreSQL. Permite a los usuarios registrarse, consultar información de candidatos y emitir votos de forma segura.

## 🚀 Tecnologías

- **Django 5.0** - Framework web
- **Django REST Framework 3.14** - API REST
- **PostgreSQL** - Base de datos
- **JWT** - Autenticación
- **API RENIEC** - Validación de DNI

## 📋 Características

- ✅ Autenticación con JWT (JSON Web Tokens)
- ✅ Registro de usuarios con validación RENIEC
- ✅ Gestión de candidatos, partidos y antecedentes
- ✅ Sistema de votación con validaciones por región
- ✅ Resultados en tiempo real
- ✅ API documentada y lista para consumir

## 🏗️ Estructura del Proyecto

```
vota_informado/
├── config/                 # Configuración principal
├── apps/
│   ├── core/              # Modelos compartidos (Region, Cargo)
│   ├── usuarios/          # Autenticación y usuarios
│   ├── candidatos/        # Candidatos, partidos y antecedentes
│   └── votos/             # Sistema de votación
├── requirements.txt
├── .env.example
└── manage.py
```

## ⚙️ Instalación

### 1. Clonar el repositorio
```bash
git clone https://github.com/tu-usuario/vota-informado-api.git
cd vota-informado-api
```

### 2. Crear entorno virtual
```bash
python -m venv venv
source venv/bin/activate  # Linux/Mac
venv\Scripts\activate     # Windows
```

### 3. Instalar dependencias
```bash
pip install -r requirements.txt
```

### 4. Configurar variables de entorno
```bash
cp .env.example .env
# Editar .env con tus credenciales
```

### 5. Crear base de datos PostgreSQL
```sql
CREATE DATABASE vota_informado_db;
```

### 6. Ejecutar migraciones
```bash
python manage.py migrate
```

### 7. Cargar datos iniciales
```bash
python manage.py seed_data
```

### 8. Ejecutar servidor
```bash
python manage.py runserver
```

La API estará disponible en `http://127.0.0.1:8000/`


## 📡 Endpoints Principales

### Autenticación
- `POST /api/usuarios/registro/` - Registrar usuario
- `POST /api/usuarios/login/` - Iniciar sesión
- `POST /api/usuarios/token/refresh/` - Renovar token
- `GET /api/usuarios/perfil/` 🔒 - Ver perfil

### Candidatos
- `GET /api/candidatos/` - Listar todos los candidatos
- `GET /api/candidatos/?cargo=Presidente` - Filtrar por cargo (Presidente, Senador, Diputado)
- `GET /api/candidatos/?cargo=Diputado&region=15` - Filtrar por cargo y región
- `GET /api/candidatos/?partido=1` - Filtrar por partido (ID del partido)
- `GET /api/candidatos/?search=Juan` - Buscar por nombre
- `GET /api/candidatos/?cargo=Presidente&partido=1` - Filtrar por cargo y partido
- `GET /api/candidatos/?cargo=Diputado&region=15&partido=1` - Combinar múltiples filtros
- `GET /api/candidatos/{id}/` - Detalle de candidato con antecedentes
  - **Incluye**: `denuncias[]`, `propuestas[]`, `proyectos[]` (ya filtrados por tipo)
- `GET /api/candidatos/partidos/` - Listar todos los partidos

### Votación
- `POST /api/votos/votar/` 🔒 - Emitir voto
- `GET /api/votos/mis-votos/` 🔒 - Ver mis votos
- `GET /api/votos/resultados/` - Ver resultados
- `GET /api/votos/estadisticas/` - Estadísticas generales

🔒 = Requiere autenticación JWT

## 🔐 Autenticación JWT

### Registro
```bash
POST /api/usuarios/registro/
Content-Type: application/json

{
  "dni": "12345678",
  "region_id": 15,
  "password": "mipassword123"
}
```

**Response:**
```json
{
  "user": { ... },
  "tokens": {
    "access": "eyJ0eXAiOiJKV1QiLCJhbGc...",
    "refresh": "eyJ0eXAiOiJKV1QiLCJhbGc..."
  }
}
```

### Uso del Token
```bash
GET /api/usuarios/perfil/
Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGc...
```

## 🗄️ Modelos de Datos

### Usuario
- DNI (único, 8 dígitos)
- Nombre completo (obtenido de RENIEC)
- Región
- Rol (votante/invitado)

### Candidato
- Nombre completo
- Partido político
- Cargo (Presidente, Senador, Diputado)
- Región (solo para Diputados)
- Antecedentes (denuncias, proyectos, propuestas)

### Voto
- Usuario
- Candidato
- Cargo
- Validaciones automáticas por región

## 📦 Comandos Útiles

```bash
# Crear superusuario para admin
python manage.py createsuperuser

# Crear nuevas migraciones
python manage.py makemigrations

# Aplicar migraciones
python manage.py migrate

# Cargar datos de ejemplo
python manage.py seed_data

# Shell de Django
python manage.py shell
```

## 📝 Validaciones Implementadas

- ✅ Un usuario solo puede votar una vez por cargo
- ✅ Los Diputados solo pueden ser votados por usuarios de su región
- ✅ Validación de DNI con RENIEC antes del registro
- ✅ Candidatos deben estar activos para recibir votos
- ✅ Tokens JWT con expiración (5 horas)