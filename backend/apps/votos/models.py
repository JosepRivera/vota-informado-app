from django.db import models
from django.core.exceptions import ValidationError
from apps.usuarios.models import Usuario
from apps.candidatos.models import Candidato
from apps.core.models import Cargo, TimeStampedModel


class Voto(TimeStampedModel):
    """
    Registro de votos emitidos por los usuarios.
    - Un usuario solo puede votar UNA VEZ por cargo.
    - Para Diputados: solo puede votar por candidatos de su región.
    """

    usuario = models.ForeignKey(
        Usuario,
        on_delete=models.PROTECT,  # No permitir borrar usuario si ya votó
        related_name="votos",
        verbose_name="Usuario",
    )
    candidato = models.ForeignKey(
        Candidato,
        on_delete=models.PROTECT,
        related_name="votos_recibidos",
        verbose_name="Candidato",
    )
    cargo = models.ForeignKey(
        Cargo, on_delete=models.PROTECT, verbose_name="Cargo votado"
    )

    class Meta:
        db_table = "votos"
        verbose_name = "Voto"
        verbose_name_plural = "Votos"
        indexes = [
            models.Index(fields=["usuario", "cargo"]),  # Para verificar votos previos
            models.Index(fields=["candidato"]),  # Para contar votos
            models.Index(fields=["created_at"]),  # Para estadísticas por fecha
        ]
        # Constraint: Un usuario solo vota UNA VEZ por cargo
        constraints = [
            models.UniqueConstraint(
                fields=["usuario", "cargo"], name="unique_voto_por_cargo"
            )
        ]

    def __str__(self):
        return f"{self.usuario.dni} votó por {self.candidato.get_full_name()} ({self.cargo.nombre_cargo})"

    def clean(self):
        """
        Validaciones antes de guardar el voto.
        - Usuario debe ser votante.
        - Para Diputados: validar que sea de la misma región.
        - El candidato debe estar activo.
        """
        # Validar que el usuario puede votar
        if not self.usuario.puede_votar():
            raise ValidationError(
                "Los invitados no pueden votar. Debes registrarte primero."
            )

        # Validar que el candidato está activo
        if not self.candidato.activo:
            raise ValidationError("El candidato seleccionado no está disponible.")

        # Validar que el cargo del voto coincide con el cargo del candidato
        if self.cargo != self.candidato.cargo:
            raise ValidationError(
                "El cargo del voto no coincide con el cargo del candidato."
            )

        # Validación especial para Diputados: deben ser de la misma región
        if self.cargo.nombre_cargo == "Diputado":
            if self.candidato.region != self.usuario.region:
                raise ValidationError(
                    f"Solo puedes votar por diputados de tu región ({self.usuario.region.nombre_region})"
                )

    def save(self, *args, **kwargs):
        """Ejecuta validaciones antes de guardar"""
        self.full_clean()  # Llama a clean()
        super().save(*args, **kwargs)
