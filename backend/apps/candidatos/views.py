from rest_framework import generics, filters
from rest_framework.permissions import AllowAny
from django.db.models import Count, Q
from apps.candidatos.models import Candidato, Partido
from apps.candidatos.serializers import (
    CandidatoListSerializer,
    CandidatoDetailSerializer,
    PartidoSerializer,
)


class CandidatoListView(generics.ListAPIView):
    """
    GET /api/candidatos/
    Lista todos los candidatos con filtros.

    Query params:
    - cargo: Filtrar por cargo (Presidente, Senador, Diputado)
    - region: Filtrar por región (solo para Diputados)
    - partido: Filtrar por ID de partido
    - search: Buscar por nombre del candidato

    Ejemplos:
    - /api/candidatos/?cargo=Presidente
    - /api/candidatos/?cargo=Diputado&region=15  (Diputados de Lima)
    - /api/candidatos/?search=Juan
    """

    serializer_class = CandidatoListSerializer
    permission_classes = [AllowAny]  # Cualquiera puede ver candidatos
    filter_backends = [filters.SearchFilter]
    search_fields = ["nombre", "apellido_paterno", "apellido_materno"]

    def get_queryset(self):
        """
        Queryset con filtros y conteo de votos.
        """
        queryset = (
            Candidato.objects.filter(activo=True)
            .select_related("partido", "cargo", "region")
            .annotate(
                total_votos=Count(
                    "votos_recibidos"
                )  # Cuenta los votos de cada candidato
            )
        )

        # Filtro por cargo (Presidente, Senador, Diputado)
        cargo = self.request.query_params.get("cargo", None)
        if cargo:
            queryset = queryset.filter(cargo__nombre_cargo=cargo)

        # Filtro por región (solo para Diputados)
        region = self.request.query_params.get("region", None)
        if region:
            queryset = queryset.filter(region_id=region)

        # Filtro por partido
        partido = self.request.query_params.get("partido", None)
        if partido:
            queryset = queryset.filter(partido_id=partido)

        return queryset.order_by("partido__sigla", "apellido_paterno")


class CandidatoDetailView(generics.RetrieveAPIView):
    """
    GET /api/candidatos/{id}/
    Obtiene el detalle completo de un candidato con sus antecedentes.
    """

    queryset = (
        Candidato.objects.filter(activo=True)
        .select_related("partido", "cargo", "region")
        .prefetch_related("antecedentes")
        .annotate(total_votos=Count("votos_recibidos"))
    )
    serializer_class = CandidatoDetailSerializer
    permission_classes = [AllowAny]


class PartidoListView(generics.ListAPIView):
    """
    GET /api/partidos/
    Lista todos los partidos políticos.
    """

    queryset = Partido.objects.filter(activo=True).order_by("sigla")
    serializer_class = PartidoSerializer
    permission_classes = [AllowAny]
