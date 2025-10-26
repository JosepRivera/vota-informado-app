from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path("admin/", admin.site.urls),
    # API endpoints
    path("api/usuarios/", include("apps.usuarios.urls")),
    path("api/candidatos/", include("apps.candidatos.urls")),
    path("api/votos/", include("apps.votos.urls")),
]
