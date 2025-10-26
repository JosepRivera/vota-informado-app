from rest_framework import serializers
from apps.core.models import Region, Cargo


class RegionSerializer(serializers.ModelSerializer):
    """Serializer para regiones"""

    class Meta:
        model = Region
        fields = ["id", "nombre_region"]


class CargoSerializer(serializers.ModelSerializer):
    """Serializer para cargos"""

    class Meta:
        model = Cargo
        fields = ["id", "nombre_cargo"]
