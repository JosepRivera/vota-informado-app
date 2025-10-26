from rest_framework import serializers
from apps.votos.models import Voto
from apps.candidatos.serializers import CandidatoListSerializer


class VotarSerializer(serializers.Serializer):
    """
    Serializer para emitir un voto.
    Solo recibe el ID del candidato, el usuario viene del JWT.
    """

    candidato_id = serializers.IntegerField()

    def validate_candidato_id(self, value):
        """Valida que el candidato exista y esté activo"""
        from apps.candidatos.models import Candidato

        try:
            candidato = Candidato.objects.get(id=value, activo=True)
        except Candidato.DoesNotExist:
            raise serializers.ValidationError(
                "El candidato seleccionado no existe o no está disponible"
            )

        return value

    def create(self, validated_data):
        """
        Crea el voto con las validaciones del modelo.
        El modelo Voto.clean() se encarga de validar región, cargo, etc.
        """
        from apps.candidatos.models import Candidato

        usuario = self.context["request"].user
        candidato = Candidato.objects.get(id=validated_data["candidato_id"])

        # Crear voto (el modelo se encarga de validaciones)
        voto = Voto(usuario=usuario, candidato=candidato, cargo=candidato.cargo)
        voto.save()  # Llama a clean() internamente

        return voto


class MiVotoSerializer(serializers.ModelSerializer):
    """
    Serializer para mostrar los votos del usuario autenticado.
    """

    candidato = CandidatoListSerializer(read_only=True)
    cargo = serializers.StringRelatedField()

    class Meta:
        model = Voto
        fields = ["id", "candidato", "cargo", "created_at"]
        read_only_fields = ["id", "created_at"]
