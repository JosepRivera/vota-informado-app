from django.urls import path
from rest_framework_simplejwt.views import TokenRefreshView
from apps.usuarios import views

urlpatterns = [
    # Endpoints de autenticación
    path("registro/", views.registro, name="registro"),
    path("login/", views.login, name="login"),
    path(
        "token/refresh/", TokenRefreshView.as_view(), name="token_refresh"
    ),  # Renovar token
    # Validación y consultas
    path("validar-dni/", views.validar_dni, name="validar_dni"),
    path("perfil/", views.perfil, name="perfil"),
    path("regiones/", views.listar_regiones, name="listar_regiones"),
]
