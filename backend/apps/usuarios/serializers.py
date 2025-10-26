from rest_framework import serializers
from apps.usuarios.models import Usuario
from apps.core.models import Region
from apps.usuarios.services import ReniecService


class RegionSerializer(serializers.ModelSerializer):
    """Serializer simple para mostrar regiones"""

    class Meta:
        model = Region
        fields = ["id", "nombre_region"]


class UsuarioSerializer(serializers.ModelSerializer):
    """
    Serializer para mostrar información del usuario.
    Solo lectura, no para crear usuarios.
    """

    region = RegionSerializer(read_only=True)
    nombre_completo = serializers.SerializerMethodField()

    class Meta:
        model = Usuario
        fields = [
            "id",
            "dni",
            "nombre",
            "apellido_paterno",
            "apellido_materno",
            "nombre_completo",
            "region",
            "rol",
            "created_at",
        ]
        read_only_fields = ["id", "created_at"]

    def get_nombre_completo(self, obj):
        return obj.get_full_name()


class RegistroSerializer(serializers.Serializer):
    """
    Serializer para registrar un nuevo usuario.
    Consulta RENIEC automáticamente con el DNI.
    """

    dni = serializers.CharField(max_length=8, min_length=8)
    region_id = serializers.IntegerField()
    password = serializers.CharField(write_only=True, min_length=6)

    def validate_dni(self, value):
        """Valida que el DNI tenga formato correcto y no esté registrado"""
        if not value.isdigit():
            raise serializers.ValidationError("El DNI debe contener solo números")

        # Verificar si ya existe un usuario con este DNI
        if Usuario.objects.filter(dni=value).exists():
            raise serializers.ValidationError("Este DNI ya está registrado")

        return value

    def validate_region_id(self, value):
        """Valida que la región exista"""
        try:
            Region.objects.get(id=value)
        except Region.DoesNotExist:
            raise serializers.ValidationError("La región seleccionada no existe")
        return value

    def create(self, validated_data):
        """
        Crea el usuario consultando primero RENIEC para obtener nombres.
        """
        dni = validated_data["dni"]
        region_id = validated_data["region_id"]
        password = validated_data["password"]

        # Consultar RENIEC para obtener nombres (puede lanzar ValidationError)
        datos_reniec = ReniecService.consultar_dni(dni)

        # Obtener la región
        region = Region.objects.get(id=region_id)

        # Crear el usuario con los datos de RENIEC
        usuario = Usuario.objects.create_user(
            dni=dni,
            nombre=datos_reniec["nombre"],
            apellido_paterno=datos_reniec["apellido_paterno"],
            apellido_materno=datos_reniec["apellido_materno"],
            region=region,
            password=password,
            rol="votante",  # Los usuarios registrados son votantes
        )

        return usuario


class ValidarDNISerializer(serializers.Serializer):
    """
    Serializer para validar DNI con RENIEC sin crear usuario.
    Útil para pre-validar antes del registro.
    """

    dni = serializers.CharField(max_length=8, min_length=8)

    def validate_dni(self, value):
        if not value.isdigit():
            raise serializers.ValidationError("El DNI debe contener solo números")
        return value
