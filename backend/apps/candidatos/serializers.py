from rest_framework import serializers
from apps.candidatos.models import Partido, Candidato, Antecedente
from apps.core.models import Cargo


class PartidoSerializer(serializers.ModelSerializer):
    """Serializer para partidos políticos"""

    class Meta:
        model = Partido
        fields = ["id", "nombre_partido", "sigla", "logo_url"]


class CargoSerializer(serializers.ModelSerializer):
    """Serializer para cargos"""

    class Meta:
        model = Cargo
        fields = ["id", "nombre_cargo"]


class AntecedenteSerializer(serializers.ModelSerializer):
    """Serializer para antecedentes de candidatos"""

    class Meta:
        model = Antecedente
        fields = [
            "id",
            "tipo",
            "titulo",
            "descripcion",
            "fecha",
            "fuente_url",
            "created_at",
        ]


class CandidatoListSerializer(serializers.ModelSerializer):
    """
    Serializer ligero para listar candidatos (sin antecedentes).
    Útil para listados generales.
    """

    partido = PartidoSerializer(read_only=True)
    cargo = CargoSerializer(read_only=True)
    region = serializers.SerializerMethodField()
    nombre_completo = serializers.SerializerMethodField()
    total_votos = serializers.IntegerField(read_only=True)  # Campo agregado en query

    class Meta:
        model = Candidato
        fields = [
            "id",
            "nombre",
            "apellido_paterno",
            "apellido_materno",
            "nombre_completo",
            "partido",
            "cargo",
            "region",
            "foto_url",
            "total_votos",
        ]

    def get_nombre_completo(self, obj):
        return obj.get_full_name()

    def get_region(self, obj):
        # Si tiene región explícita, devolverla
        if obj.region:
            return {"id": obj.region.id, "nombre_region": obj.region.nombre_region}
        # Para Presidente y Senador devolver región nacional por consistencia con el cliente
        if obj.cargo and obj.cargo.nombre_cargo in ["Presidente", "Senador"]:
            return {"id": 0, "nombre_region": "Perú"}
        return None


class CandidatoDetailSerializer(serializers.ModelSerializer):
    """
    Serializer completo para ver detalle de un candidato.
    Incluye todos los antecedentes agrupados por tipo.
    """

    partido = PartidoSerializer(read_only=True)
    cargo = CargoSerializer(read_only=True)
    region = serializers.SerializerMethodField()
    nombre_completo = serializers.SerializerMethodField()

    # Antecedentes agrupados por tipo
    denuncias = serializers.SerializerMethodField()
    proyectos = serializers.SerializerMethodField()
    propuestas = serializers.SerializerMethodField()

    total_votos = serializers.IntegerField(read_only=True)

    class Meta:
        model = Candidato
        fields = [
            "id",
            "nombre",
            "apellido_paterno",
            "apellido_materno",
            "nombre_completo",
            "partido",
            "cargo",
            "region",
            "foto_url",
            "denuncias",
            "proyectos",
            "propuestas",
            "total_votos",
            "created_at",
        ]

    def get_nombre_completo(self, obj):
        return obj.get_full_name()

    def get_region(self, obj):
        if obj.region:
            return {"id": obj.region.id, "nombre_region": obj.region.nombre_region}
        if obj.cargo and obj.cargo.nombre_cargo in ["Presidente", "Senador"]:
            return {"id": 0, "nombre_region": "Perú"}
        return None

    def get_denuncias(self, obj):
        """Devuelve solo las denuncias del candidato"""
        denuncias = obj.antecedentes.filter(tipo="denuncia")
        return AntecedenteSerializer(denuncias, many=True).data

    def get_proyectos(self, obj):
        """Devuelve solo los proyectos del candidato"""
        proyectos = obj.antecedentes.filter(tipo="proyecto")
        return AntecedenteSerializer(proyectos, many=True).data

    def get_propuestas(self, obj):
        """Devuelve solo las propuestas del candidato"""
        propuestas = obj.antecedentes.filter(tipo="propuesta")
        return AntecedenteSerializer(propuestas, many=True).data
