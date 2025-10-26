from django.contrib.auth.models import (
    AbstractBaseUser,
    BaseUserManager,
    PermissionsMixin,
)
from django.db import models
from apps.core.models import Region, TimeStampedModel


class UsuarioManager(BaseUserManager):
    """
    Manager personalizado para crear usuarios.
    Necesario cuando personalizas el modelo de usuario de Django.
    """

    def create_user(
        self,
        dni,
        nombre,
        apellido_paterno,
        apellido_materno,
        region,
        password=None,
        **extra_fields,
    ):
        if not dni:
            raise ValueError("El DNI es obligatorio")

        user = self.model(
            dni=dni,
            nombre=nombre,
            apellido_paterno=apellido_paterno,
            apellido_materno=apellido_materno,
            region=region,
            **extra_fields,
        )
        user.set_password(password)  # Encripta la contraseña
        user.save(using=self._db)
        return user

    def create_superuser(
        self, dni, nombre, apellido_paterno, apellido_materno, region, password=None
    ):
        """Para crear superusuarios desde consola: python manage.py createsuperuser"""
        user = self.create_user(
            dni=dni,
            nombre=nombre,
            apellido_paterno=apellido_paterno,
            apellido_materno=apellido_materno,
            region=region,
            password=password,
        )
        user.is_staff = True
        user.is_superuser = True
        user.save(using=self._db)
        return user


class Usuario(AbstractBaseUser, PermissionsMixin, TimeStampedModel):
    """
    Modelo de usuario personalizado.
    Usamos DNI como identificador único en lugar de username.
    """

    ROL_CHOICES = [
        ("votante", "Votante"),
        ("invitado", "Invitado"),
    ]

    dni = models.CharField(max_length=8, unique=True, db_index=True, verbose_name="DNI")
    nombre = models.CharField(max_length=100, verbose_name="Nombre(s)")
    apellido_paterno = models.CharField(max_length=100, verbose_name="Apellido paterno")
    apellido_materno = models.CharField(max_length=100, verbose_name="Apellido materno")
    region = models.ForeignKey(
        Region,
        on_delete=models.PROTECT,  # PROTECT: no permite borrar región si tiene usuarios
        related_name="usuarios",
        verbose_name="Región",
    )
    rol = models.CharField(
        max_length=10, choices=ROL_CHOICES, default="votante", verbose_name="Rol"
    )

    # Campos requeridos por Django para auth
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)  # Para acceso al admin de Django

    # Indica que el DNI es el campo de login
    USERNAME_FIELD = "dni"
    REQUIRED_FIELDS = ["nombre", "apellido_paterno", "apellido_materno", "region"]

    objects = UsuarioManager()

    class Meta:
        db_table = "usuarios"
        verbose_name = "Usuario"
        verbose_name_plural = "Usuarios"
        indexes = [
            models.Index(fields=["dni"]),  # Índice para búsquedas rápidas por DNI
        ]

    def __str__(self):
        return f"{self.dni} - {self.get_full_name()}"

    def get_full_name(self):
        """Devuelve nombre completo del usuario"""
        return f"{self.nombre} {self.apellido_paterno} {self.apellido_materno}"

    def puede_votar(self):
        """Verifica si el usuario puede votar (solo votantes registrados)"""
        return self.rol == "votante"
