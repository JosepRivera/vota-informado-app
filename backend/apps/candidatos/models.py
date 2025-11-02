from django.db import models
from apps.core.models import Region, Cargo, TimeStampedModel


class Partido(TimeStampedModel):
    """
    Partidos políticos que participan en las elecciones.
    """

    nombre_partido = models.CharField(
        max_length=200, unique=True, verbose_name="Nombre del partido"
    )
    sigla = models.CharField(max_length=20, verbose_name="Sigla")
    logo_url = models.URLField(blank=True, null=True, verbose_name="URL del logo")
    activo = models.BooleanField(default=True, verbose_name="Activo")  # Soft delete

    class Meta:
        db_table = "partidos"
        verbose_name = "Partido"
        verbose_name_plural = "Partidos"
        ordering = ["nombre_partido"]

    def __str__(self):
        return f"{self.sigla} - {self.nombre_partido}"


class Candidato(TimeStampedModel):
    """
    Candidatos que postulan a los diferentes cargos.
    - Si es Diputado: region = región específica (obligatoria)
    - Presidente y Senador pueden opcionalmente tener región (por ejemplo "Perú")
    """

    nombre = models.CharField(max_length=100, verbose_name="Nombre(s)")
    apellido_paterno = models.CharField(max_length=100, verbose_name="Apellido paterno")
    apellido_materno = models.CharField(max_length=100, verbose_name="Apellido materno")
    partido = models.ForeignKey(
        Partido,
        on_delete=models.CASCADE,  # Si se borra partido, se borran sus candidatos
        related_name="candidatos",
        verbose_name="Partido",
    )
    cargo = models.ForeignKey(
        Cargo,
        on_delete=models.PROTECT,
        related_name="candidatos",
        verbose_name="Cargo al que postula",
    )
    region = models.ForeignKey(
        Region,
        on_delete=models.PROTECT,
        related_name="candidatos",
        null=True,  # NULL si es Presidente o Senador
        blank=True,
        verbose_name="Región (solo para Diputados)",
    )
    foto_url = models.URLField(max_length=700,
        blank=True, null=True, verbose_name="URL de foto del candidato"
    )
    activo = models.BooleanField(default=True, verbose_name="Activo")  # Soft delete

    class Meta:
        db_table = "candidatos"
        verbose_name = "Candidato"
        verbose_name_plural = "Candidatos"
        indexes = [
            models.Index(
                fields=["cargo", "region"]
            ),  # Para búsquedas rápidas por cargo y región
            models.Index(fields=["partido"]),
        ]
        # Constraint: Un candidato solo puede postular una vez al mismo cargo en la misma región
        constraints = [
            models.UniqueConstraint(
                fields=[
                    "nombre",
                    "apellido_paterno",
                    "apellido_materno",
                    "cargo",
                    "region",
                ],
                name="unique_candidato_cargo_region",
            )
        ]

    def __str__(self):
        region_str = f" - {self.region.nombre_region}" if self.region else ""
        return f"{self.get_full_name()} ({self.cargo.nombre_cargo}{region_str})"

    def get_full_name(self):
        return f"{self.nombre} {self.apellido_paterno} {self.apellido_materno}"

    def save(self, *args, **kwargs):
        """
        Validación:
        - Diputado debe tener región obligatoriamente.
        - Presidente y Senador: región opcional (se respeta si viene informada).
        """
        if self.cargo.nombre_cargo == "Diputado" and not self.region:
            raise ValueError("Los Diputados deben tener una región asignada")
        super().save(*args, **kwargs)


class Antecedente(TimeStampedModel):
    """
    Información pública del candidato: denuncias, proyectos, propuestas.
    Un candidato puede tener múltiples antecedentes.
    """

    TIPO_CHOICES = [
        ("denuncia", "Denuncia"),
        ("proyecto", "Proyecto"),
        ("propuesta", "Propuesta"),
    ]

    candidato = models.ForeignKey(
        Candidato,
        on_delete=models.CASCADE,  # Si se borra candidato, se borran sus antecedentes
        related_name="antecedentes",
        verbose_name="Candidato",
    )
    tipo = models.CharField(
        max_length=20, choices=TIPO_CHOICES, verbose_name="Tipo de antecedente"
    )
    titulo = models.CharField(max_length=300, verbose_name="Título")
    descripcion = models.TextField(verbose_name="Descripción detallada")
    fecha = models.DateField(verbose_name="Fecha del antecedente")
    fuente_url = models.URLField(blank=True, null=True, verbose_name="Fuente oficial")

    class Meta:
        db_table = "antecedentes"
        verbose_name = "Antecedente"
        verbose_name_plural = "Antecedentes"
        ordering = ["-fecha"]  # Más recientes primero
        indexes = [
            models.Index(fields=["candidato", "tipo"]),  # Para filtrar por tipo
        ]

    def __str__(self):
        return f"{self.tipo.upper()}: {self.titulo} ({self.candidato.get_full_name()})"
