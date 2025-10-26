from django.core.management.base import BaseCommand
from apps.core.models import Region, Cargo
from apps.candidatos.models import Partido, Candidato, Antecedente
from datetime import date


class Command(BaseCommand):
    help = "Carga datos iniciales de regiones, cargos, partidos y candidatos"

    def handle(self, *args, **options):
        self.stdout.write("Iniciando carga de datos...\n")

        # 1. Crear Regiones (Departamentos del Perú)
        self.stdout.write("Creando regiones...")
        regiones_data = [
            "Amazonas",
            "Áncash",
            "Apurímac",
            "Arequipa",
            "Ayacucho",
            "Cajamarca",
            "Callao",
            "Cusco",
            "Huancavelica",
            "Huánuco",
            "Ica",
            "Junín",
            "La Libertad",
            "Lambayeque",
            "Lima",
            "Loreto",
            "Madre de Dios",
            "Moquegua",
            "Pasco",
            "Piura",
            "Puno",
            "San Martín",
            "Tacna",
            "Tumbes",
            "Ucayali",
        ]

        regiones = {}
        for nombre in regiones_data:
            region, created = Region.objects.get_or_create(nombre_region=nombre)
            regiones[nombre] = region
            if created:
                self.stdout.write(f"  ✓ {nombre}")

        # 2. Crear Cargos
        self.stdout.write("\nCreando cargos...")
        cargos = {}
        for cargo_nombre in ["Presidente", "Senador", "Diputado"]:
            cargo, created = Cargo.objects.get_or_create(nombre_cargo=cargo_nombre)
            cargos[cargo_nombre] = cargo
            if created:
                self.stdout.write(f"  ✓ {cargo_nombre}")

        # 3. Crear Partidos
        self.stdout.write("\nCreando partidos...")
        partidos_data = [
            {
                "nombre_partido": "Fuerza Popular",
                "sigla": "FP",
                "logo_url": "https://ejemplo.com/logos/fp.png",
            },
            {
                "nombre_partido": "Partido Morado",
                "sigla": "PM",
                "logo_url": "https://ejemplo.com/logos/pm.png",
            },
            {
                "nombre_partido": "Alianza para el Progreso",
                "sigla": "APP",
                "logo_url": "https://ejemplo.com/logos/app.png",
            },
            {
                "nombre_partido": "Perú Libre",
                "sigla": "PL",
                "logo_url": "https://ejemplo.com/logos/pl.png",
            },
            {
                "nombre_partido": "Acción Popular",
                "sigla": "AP",
                "logo_url": "https://ejemplo.com/logos/ap.png",
            },
        ]

        partidos = {}
        for partido_data in partidos_data:
            partido, created = Partido.objects.get_or_create(
                sigla=partido_data["sigla"], defaults=partido_data
            )
            partidos[partido_data["sigla"]] = partido
            if created:
                self.stdout.write(
                    f"  ✓ {partido_data['sigla']} - {partido_data['nombre_partido']}"
                )

        # 4. Crear Candidatos de ejemplo
        self.stdout.write("\nCreando candidatos...")

        # PRESIDENTES (3 candidatos)
        candidatos_presidentes = [
            {
                "nombre": "Juan Carlos",
                "apellido_paterno": "Pérez",
                "apellido_materno": "García",
                "partido": partidos["FP"],
                "cargo": cargos["Presidente"],
                "foto_url": "https://ejemplo.com/fotos/presidente1.jpg",
            },
            {
                "nombre": "María Elena",
                "apellido_paterno": "Rodríguez",
                "apellido_materno": "López",
                "partido": partidos["PM"],
                "cargo": cargos["Presidente"],
                "foto_url": "https://ejemplo.com/fotos/presidente2.jpg",
            },
            {
                "nombre": "Carlos Alberto",
                "apellido_paterno": "Sánchez",
                "apellido_materno": "Torres",
                "partido": partidos["APP"],
                "cargo": cargos["Presidente"],
                "foto_url": "https://ejemplo.com/fotos/presidente3.jpg",
            },
        ]

        for data in candidatos_presidentes:
            candidato, created = Candidato.objects.get_or_create(
                nombre=data["nombre"],
                apellido_paterno=data["apellido_paterno"],
                apellido_materno=data["apellido_materno"],
                cargo=data["cargo"],
                defaults={"partido": data["partido"], "foto_url": data["foto_url"]},
            )
            if created:
                self.stdout.write(
                    f"  ✓ Presidente: {candidato.get_full_name()} ({data['partido'].sigla})"
                )

                # Agregar antecedentes de ejemplo
                Antecedente.objects.create(
                    candidato=candidato,
                    tipo="propuesta",
                    titulo="Reforma educativa integral",
                    descripcion="Propuesta para mejorar la calidad educativa en zonas rurales",
                    fecha=date(2024, 1, 15),
                    fuente_url="https://ejemplo.com/propuesta1",
                )

        # SENADORES (5 candidatos)
        candidatos_senadores = [
            {
                "nombre": "Pedro",
                "apellido_paterno": "Martínez",
                "apellido_materno": "Luna",
                "partido": partidos["FP"],
            },
            {
                "nombre": "Ana",
                "apellido_paterno": "Flores",
                "apellido_materno": "Quispe",
                "partido": partidos["PM"],
            },
            {
                "nombre": "Luis",
                "apellido_paterno": "Castro",
                "apellido_materno": "Ruiz",
                "partido": partidos["APP"],
            },
            {
                "nombre": "Rosa",
                "apellido_paterno": "Mendoza",
                "apellido_materno": "Vargas",
                "partido": partidos["PL"],
            },
            {
                "nombre": "Miguel",
                "apellido_paterno": "Gutiérrez",
                "apellido_materno": "Silva",
                "partido": partidos["AP"],
            },
        ]

        for data in candidatos_senadores:
            candidato, created = Candidato.objects.get_or_create(
                nombre=data["nombre"],
                apellido_paterno=data["apellido_paterno"],
                apellido_materno=data["apellido_materno"],
                cargo=cargos["Senador"],
                defaults={"partido": data["partido"]},
            )
            if created:
                self.stdout.write(
                    f"  ✓ Senador: {candidato.get_full_name()} ({data['partido'].sigla})"
                )

        # DIPUTADOS (2 por región de ejemplo: Lima y Cusco)
        candidatos_diputados = [
            # Lima
            {
                "nombre": "Jorge",
                "apellido_paterno": "Ramírez",
                "apellido_materno": "Paz",
                "partido": partidos["FP"],
                "region": regiones["Lima"],
            },
            {
                "nombre": "Carmen",
                "apellido_paterno": "Vega",
                "apellido_materno": "Morales",
                "partido": partidos["PM"],
                "region": regiones["Lima"],
            },
            # Cusco
            {
                "nombre": "Roberto",
                "apellido_paterno": "Chávez",
                "apellido_materno": "Huamán",
                "partido": partidos["APP"],
                "region": regiones["Cusco"],
            },
            {
                "nombre": "Lucia",
                "apellido_paterno": "Quispe",
                "apellido_materno": "Ccama",
                "partido": partidos["PL"],
                "region": regiones["Cusco"],
            },
            # Arequipa
            {
                "nombre": "Fernando",
                "apellido_paterno": "Gonzales",
                "apellido_materno": "Prado",
                "partido": partidos["AP"],
                "region": regiones["Arequipa"],
            },
            {
                "nombre": "Patricia",
                "apellido_paterno": "Ramos",
                "apellido_materno": "Díaz",
                "partido": partidos["FP"],
                "region": regiones["Arequipa"],
            },
        ]

        for data in candidatos_diputados:
            candidato, created = Candidato.objects.get_or_create(
                nombre=data["nombre"],
                apellido_paterno=data["apellido_paterno"],
                apellido_materno=data["apellido_materno"],
                cargo=cargos["Diputado"],
                region=data["region"],
                defaults={"partido": data["partido"]},
            )
            if created:
                self.stdout.write(
                    f"  ✓ Diputado ({data['region'].nombre_region}): {candidato.get_full_name()} ({data['partido'].sigla})"
                )

        self.stdout.write(self.style.SUCCESS("\nDatos cargados exitosamente!"))
        self.stdout.write("\nResumen:")
        self.stdout.write(f"  - Regiones: {Region.objects.count()}")
        self.stdout.write(f"  - Cargos: {Cargo.objects.count()}")
        self.stdout.write(f"  - Partidos: {Partido.objects.count()}")
        self.stdout.write(f"  - Candidatos: {Candidato.objects.count()}")
        self.stdout.write(f"  - Antecedentes: {Antecedente.objects.count()}\n")
