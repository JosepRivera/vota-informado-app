from django.urls import path
from apps.votos import views

urlpatterns = [
    # Votar
    path("votar/", views.votar, name="votar"),
    path("mis-votos/", views.mis_votos, name="mis_votos"),
    path(
        "puede-votar/<str:cargo_nombre>/",
        views.puede_votar_cargo,
        name="puede_votar_cargo",
    ),
    # Resultados
    path("resultados/", views.resultados_generales, name="resultados_generales"),
    path(
        "resultados/por-partido/",
        views.resultados_por_partido,
        name="resultados_por_partido",
    ),
    path("estadisticas/", views.estadisticas, name="estadisticas"),
]
