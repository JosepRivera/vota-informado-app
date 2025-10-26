from django.urls import path
from apps.candidatos import views

urlpatterns = [
    path("", views.CandidatoListView.as_view(), name="candidatos_list"),
    path("<int:pk>/", views.CandidatoDetailView.as_view(), name="candidato_detail"),
    path("partidos/", views.PartidoListView.as_view(), name="partidos_list"),
]
