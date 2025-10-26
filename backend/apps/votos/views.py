from rest_framework import status, generics
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated, AllowAny
from django.db.models import Count, Q
from django.core.exceptions import ValidationError
from apps.votos.models import Voto
from apps.votos.serializers import VotarSerializer, MiVotoSerializer
from apps.candidatos.models import Candidato
from apps.core.models import Cargo, Region


@api_view(["POST"])
@permission_classes([IsAuthenticated])  # Requiere JWT token
def votar(request):
    """
    POST /api/votos/votar/
    Emite un voto para un candidato.

    Body: {"candidato_id": 5}
    Headers: Authorization: Bearer <access_token>

    Validaciones automáticas:
    - Usuario debe ser votante (no invitado)
    - No puede votar dos veces por el mismo cargo
    - Para Diputados: solo puede votar por candidatos de su región
    """
    serializer = VotarSerializer(data=request.data, context={"request": request})

    try:
        serializer.is_valid(raise_exception=True)
        voto = serializer.save()

        return Response(
            {
                "message": "Voto registrado exitosamente",
                "voto": MiVotoSerializer(voto).data,
            },
            status=status.HTTP_201_CREATED,
        )

    except ValidationError as e:
        # Errores de validación del modelo (región, doble voto, etc.)
        return Response(
            {"error": str(e.message) if hasattr(e, "message") else str(e)},
            status=status.HTTP_400_BAD_REQUEST,
        )


@api_view(["GET"])
@permission_classes([IsAuthenticated])
def mis_votos(request):
    """
    GET /api/votos/mis-votos/
    Lista todos los votos que ha emitido el usuario autenticado.
    """
    votos = Voto.objects.filter(usuario=request.user).select_related(
        "candidato", "cargo", "candidato__partido"
    )
    serializer = MiVotoSerializer(votos, many=True)
    return Response(serializer.data)


@api_view(["GET"])
@permission_classes([IsAuthenticated])
def puede_votar_cargo(request, cargo_nombre):
    """
    GET /api/votos/puede-votar/{cargo_nombre}/
    Verifica si el usuario ya votó por un cargo específico.

    Params: cargo_nombre = 'Presidente' | 'Senador' | 'Diputado'

    Response: {
        "puede_votar": true/false,
        "ya_voto": true/false
    }
    """
    try:
        cargo = Cargo.objects.get(nombre_cargo=cargo_nombre)
    except Cargo.DoesNotExist:
        return Response(
            {"error": "Cargo no válido"}, status=status.HTTP_400_BAD_REQUEST
        )

    ya_voto = Voto.objects.filter(usuario=request.user, cargo=cargo).exists()

    return Response(
        {"puede_votar": not ya_voto and request.user.puede_votar(), "ya_voto": ya_voto}
    )


@api_view(["GET"])
@permission_classes([AllowAny])  # Cualquiera puede ver resultados
def resultados_generales(request):
    """
    GET /api/votos/resultados/
    Resultados generales de todas las elecciones.

    Query params:
    - cargo: Filtrar por cargo específico
    - region: Filtrar por región (solo para Diputados)

    Response: Lista de candidatos con su conteo de votos ordenados de mayor a menor.
    """
    queryset = (
        Candidato.objects.filter(activo=True)
        .select_related("partido", "cargo", "region")
        .annotate(total_votos=Count("votos_recibidos"))
    )

    # Filtros opcionales
    cargo = request.query_params.get("cargo", None)
    if cargo:
        queryset = queryset.filter(cargo__nombre_cargo=cargo)

    region = request.query_params.get("region", None)
    if region:
        queryset = queryset.filter(region_id=region)

    # Ordenar por número de votos (descendente)
    queryset = queryset.order_by("-total_votos", "apellido_paterno")

    # Serializar resultados
    resultados = []
    for candidato in queryset:
        resultados.append(
            {
                "id": candidato.id,
                "nombre_completo": candidato.get_full_name(),
                "partido": {
                    "sigla": candidato.partido.sigla,
                    "nombre": candidato.partido.nombre_partido,
                    "logo_url": candidato.partido.logo_url,
                },
                "cargo": candidato.cargo.nombre_cargo,
                "region": candidato.region.nombre_region if candidato.region else None,
                "total_votos": candidato.total_votos,
                "foto_url": candidato.foto_url,
            }
        )

    return Response(resultados)


@api_view(["GET"])
@permission_classes([AllowAny])
def resultados_por_partido(request):
    """
    GET /api/votos/resultados/por-partido/
    Resultados agrupados por partido político.
    Suma todos los votos de candidatos del mismo partido.

    Query param:
    - cargo: Filtrar por cargo específico
    """
    from apps.candidatos.models import Partido

    cargo = request.query_params.get("cargo", None)

    partidos = (
        Partido.objects.filter(activo=True)
        .annotate(
            total_votos=Count(
                "candidatos__votos_recibidos",
                filter=Q(candidatos__activo=True)
                & (Q(candidatos__cargo__nombre_cargo=cargo) if cargo else Q()),
            )
        )
        .order_by("-total_votos")
    )

    resultados = []
    for partido in partidos:
        if partido.total_votos > 0:  # Solo partidos con votos
            resultados.append(
                {
                    "id": partido.id,
                    "nombre_partido": partido.nombre_partido,
                    "sigla": partido.sigla,
                    "logo_url": partido.logo_url,
                    "total_votos": partido.total_votos,
                }
            )

    return Response(resultados)


@api_view(["GET"])
@permission_classes([AllowAny])
def estadisticas(request):
    """
    GET /api/votos/estadisticas/
    Estadísticas generales del sistema.

    Response: {
        "total_votos": 1234,
        "votos_por_cargo": {...},
        "total_votantes": 500,
        "total_candidatos": 100
    }
    """
    from apps.usuarios.models import Usuario

    total_votos = Voto.objects.count()
    total_votantes = Usuario.objects.filter(rol="votante").count()
    total_candidatos = Candidato.objects.filter(activo=True).count()

    # Votos por cargo
    votos_por_cargo = {}
    for cargo in Cargo.objects.all():
        votos_por_cargo[cargo.nombre_cargo] = Voto.objects.filter(cargo=cargo).count()

    return Response(
        {
            "total_votos": total_votos,
            "total_votantes": total_votantes,
            "total_candidatos": total_candidatos,
            "votos_por_cargo": votos_por_cargo,
        }
    )
