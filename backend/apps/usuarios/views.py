from rest_framework import status
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework_simplejwt.tokens import RefreshToken # type: ignore
from apps.usuarios.models import Usuario
from apps.usuarios.serializers import ( # type: ignore
    UsuarioSerializer,
    RegistroSerializer,
    ValidarDNISerializer,
)
from apps.usuarios.services import ReniecService
from apps.core.models import Region
from apps.core.serializers import RegionSerializer  # type: ignore


@api_view(["POST"])
@permission_classes([AllowAny])  # No requiere autenticación
def validar_dni(request):
    """
    POST /api/usuarios/validar-dni/
    Valida un DNI con RENIEC sin crear usuario.

    Body: {"dni": "46027896"}
    Response: {
        "nombre": "ROXANA KARINA",
        "apellido_paterno": "DELGADO",
        "apellido_materno": "CUELLAR",
        "dni": "46027896"
    }
    """
    serializer = ValidarDNISerializer(data=request.data)
    serializer.is_valid(raise_exception=True)

    dni = serializer.validated_data["dni"]

    # Consultar RENIEC (puede lanzar ValidationError o APIException)
    datos = ReniecService.consultar_dni(dni)

    return Response(datos, status=status.HTTP_200_OK)


@api_view(["POST"])
@permission_classes([AllowAny])
def registro(request):
    """
    POST /api/usuarios/registro/
    Registra un nuevo usuario consultando RENIEC.

    Body: {
        "dni": "46027896",
        "region_id": 15,
        "password": "mipassword123"
    }

    Response: {
        "user": {...},
        "tokens": {
            "access": "...",
            "refresh": "..."
        }
    }
    """
    serializer = RegistroSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)

    # Crear usuario (consulta RENIEC internamente)
    usuario = serializer.save()

    # Generar tokens JWT para login automático
    refresh = RefreshToken.for_user(usuario)

    return Response(
        {
            "user": UsuarioSerializer(usuario).data,
            "tokens": {"access": str(refresh.access_token), "refresh": str(refresh)},
            "message": "Usuario registrado exitosamente",
        },
        status=status.HTTP_201_CREATED,
    )


@api_view(["GET"])
@permission_classes([IsAuthenticated])  # Requiere JWT token
def perfil(request):
    """
    GET /api/usuarios/perfil/
    Obtiene información del usuario autenticado.

    Headers: Authorization: Bearer <access_token>
    """
    serializer = UsuarioSerializer(request.user)
    return Response(serializer.data)


@api_view(["GET"])
@permission_classes([AllowAny])
def listar_regiones(request):
    """
    GET /api/usuarios/regiones/
    Lista todas las regiones disponibles para el registro.
    """
    regiones = Region.objects.all()
    serializer = RegionSerializer(regiones, many=True)
    return Response(serializer.data)


# Vista opcional: Login manual (si el usuario ya está registrado)
@api_view(["POST"])
@permission_classes([AllowAny])
def login(request):
    """
    POST /api/usuarios/login/
    Login manual con DNI y contraseña.

    Body: {
        "dni": "46027896",
        "password": "mipassword123"
    }
    """
    dni = request.data.get("dni")
    password = request.data.get("password")

    if not dni or not password:
        return Response(
            {"error": "DNI y contraseña son requeridos"},
            status=status.HTTP_400_BAD_REQUEST,
        )

    try:
        usuario = Usuario.objects.get(dni=dni)
    except Usuario.DoesNotExist:
        return Response(
            {"error": "Usuario no encontrado"}, status=status.HTTP_404_NOT_FOUND
        )

    # Verificar contraseña
    if not usuario.check_password(password):
        return Response(
            {"error": "Contraseña incorrecta"}, status=status.HTTP_401_UNAUTHORIZED
        )

    # Generar tokens JWT
    refresh = RefreshToken.for_user(usuario)

    return Response(
        {
            "user": UsuarioSerializer(usuario).data,
            "tokens": {"access": str(refresh.access_token), "refresh": str(refresh)},
        }
    )
