from django.db import models


class TimeStampedModel(models.Model):
    """
    Modelo abstracto que agrega campos de timestamp a todos los modelos.
    No crea tabla en la BD, solo hereda estos campos.
    """

    created_at = models.DateTimeField(
        auto_now_add=True, verbose_name="Fecha de creación"
    )
    updated_at = models.DateTimeField(
        auto_now=True, verbose_name="Fecha de actualización"
    )

    class Meta:
        abstract = True  # No crea tabla, solo para herencia


class Region(TimeStampedModel):
    """
    Regiones del Perú (departamentos).
    Ejemplo: Lima, Cusco, Arequipa, etc.
    """

    nombre_region = models.CharField(
        max_length=100, unique=True, verbose_name="Nombre de la región"
    )

    class Meta:
        db_table = "regiones"
        verbose_name = "Región"
        verbose_name_plural = "Regiones"
        ordering = ["nombre_region"]

    def __str__(self):
        return self.nombre_region


class Cargo(TimeStampedModel):
    """
    Tipos de cargos electorales.
    Solo 3 opciones: Presidente, Senador, Diputado
    """

    CARGO_CHOICES = [
        ("Presidente", "Presidente"),
        ("Senador", "Senador"),
        ("Diputado", "Diputado"),
    ]

    nombre_cargo = models.CharField(
        max_length=20,
        choices=CARGO_CHOICES,
        unique=True,
        verbose_name="Nombre del cargo",
    )

    class Meta:
        db_table = "cargos"
        verbose_name = "Cargo"
        verbose_name_plural = "Cargos"

    def __str__(self):
        return self.nombre_cargo
