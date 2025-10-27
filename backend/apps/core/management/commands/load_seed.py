import json
import os
from django.core.management.base import BaseCommand
from apps.core.models import Region, Cargo
from apps.candidatos.models import Partido, Candidato, Antecedente
from apps.votos.models import Voto
from apps.usuarios.models import Usuario
from datetime import datetime


class Command(BaseCommand):
    help = "Carga datos iniciales desde seed_data.json"

    def add_arguments(self, parser):
        parser.add_argument(
            "--with-votes",
            action="store_true",
            help="Carga seed_data_with_votes.json en lugar de seed_data.json",
        )

    def handle(self, *args, **options):
        with_votes = options.get("with_votes", False)

        # Seleccionar archivo JSON
        json_filename = "seed_data_with_votes.json" if with_votes else "seed_data.json"
        self.stdout.write(f"Iniciando carga de datos desde {json_filename}...\n")

        # Ruta al archivo JSON
        json_path = os.path.join(
            os.path.dirname(__file__), "..", "fixtures", json_filename
        )

        # Leer el archivo JSON
        try:
            with open(json_path, "r", encoding="utf-8") as f:
                data = json.load(f)
        except FileNotFoundError:
            self.stdout.write(
                self.style.ERROR(
                    f"No se encontró el archivo: {json_path}\n"
                    f"Asegúrate de crear: apps/core/management/fixtures/{json_filename}"
                )
            )
            return
        except json.JSONDecodeError as e:
            self.stdout.write(self.style.ERROR(f"Error al leer JSON: {e}"))
            return

        # 1. Crear Regiones (siempre las mismas 25)
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

        # 3. Crear Partidos desde JSON
        self.stdout.write("\nCreando partidos...")
        partidos = {}
        for partido_data in data.get("partidos", []):
            partido, created = Partido.objects.get_or_create(
                sigla=partido_data["sigla"],
                defaults={
                    "nombre_partido": partido_data["nombre_partido"],
                    "logo_url": partido_data.get("logo_url"),
                },
            )
            partidos[partido_data["sigla"]] = partido
            if created:
                self.stdout.write(f"  ✓ {partido.sigla} - {partido.nombre_partido}")

        # 4. Crear Candidatos Presidentes
        self.stdout.write("\nCreando candidatos presidenciales...")
        candidatos_dict = {}

        for cand_data in data.get("candidatos_presidente", []):
            candidato, created = Candidato.objects.get_or_create(
                nombre=cand_data["nombre"],
                apellido_paterno=cand_data["apellido_paterno"],
                apellido_materno=cand_data["apellido_materno"],
                cargo=cargos["Presidente"],
                defaults={
                    "partido": partidos[cand_data["partido_sigla"]],
                    "foto_url": cand_data.get("foto_url"),
                },
            )

            key = f"{cand_data['nombre']}|{cand_data['apellido_paterno']}|{cand_data['apellido_materno']}"
            candidatos_dict[key] = candidato

            if created:
                self.stdout.write(
                    f"  ✓ Presidente: {candidato.get_full_name()} "
                    f"({candidato.partido.sigla})"
                )

        # 5. Crear Candidatos Senadores
        self.stdout.write("\nCreando senadores...")
        for cand_data in data.get("candidatos_senador", []):
            candidato, created = Candidato.objects.get_or_create(
                nombre=cand_data["nombre"],
                apellido_paterno=cand_data["apellido_paterno"],
                apellido_materno=cand_data["apellido_materno"],
                cargo=cargos["Senador"],
                defaults={
                    "partido": partidos[cand_data["partido_sigla"]],
                    "foto_url": cand_data.get("foto_url"),
                },
            )

            key = f"{cand_data['nombre']}|{cand_data['apellido_paterno']}|{cand_data['apellido_materno']}"
            candidatos_dict[key] = candidato

            if created:
                self.stdout.write(
                    f"  ✓ Senador: {candidato.get_full_name()} "
                    f"({candidato.partido.sigla})"
                )

        # 6. Crear Candidatos Diputados
        self.stdout.write("\nCreando diputados...")
        for cand_data in data.get("candidatos_diputado", []):
            region = regiones[cand_data["region_nombre"]]

            candidato, created = Candidato.objects.get_or_create(
                nombre=cand_data["nombre"],
                apellido_paterno=cand_data["apellido_paterno"],
                apellido_materno=cand_data["apellido_materno"],
                cargo=cargos["Diputado"],
                region=region,
                defaults={
                    "partido": partidos[cand_data["partido_sigla"]],
                    "foto_url": cand_data.get("foto_url"),
                },
            )

            key = f"{cand_data['nombre']}|{cand_data['apellido_paterno']}|{cand_data['apellido_materno']}"
            candidatos_dict[key] = candidato

            if created:
                self.stdout.write(
                    f"  ✓ Diputado ({region.nombre_region}): "
                    f"{candidato.get_full_name()} ({candidato.partido.sigla})"
                )

        # 7. Crear Antecedentes
        self.stdout.write("\nCreando antecedentes...")
        for ant_data in data.get("antecedentes", []):
            key = f"{ant_data['candidato_nombre']}|{ant_data['candidato_apellido_paterno']}|{ant_data['candidato_apellido_materno']}"
            candidato = candidatos_dict.get(key)

            if not candidato:
                self.stdout.write(
                    self.style.WARNING(
                        f"Candidato no encontrado para antecedente: {key}"
                    )
                )
                continue

            fecha = datetime.strptime(ant_data["fecha"], "%Y-%m-%d").date()

            antecedente, created = Antecedente.objects.get_or_create(
                candidato=candidato,
                tipo=ant_data["tipo"],
                titulo=ant_data["titulo"],
                defaults={
                    "descripcion": ant_data["descripcion"],
                    "fecha": fecha,
                    "fuente_url": ant_data.get("fuente_url"),
                },
            )

            if created:
                self.stdout.write(
                    f"  ✓ {ant_data['tipo'].upper()}: {ant_data['titulo'][:50]}..."
                )

        # 8. Crear votos simulados (solo si se carga con --with-votes)
        if with_votes and data.get("votos_simulados"):
            self.stdout.write("\nCreando votos simulados...")

            votos_creados = 0
            usuario_counter = 1

            for voto_data in data.get("votos_simulados", []):
                key = f"{voto_data['candidato_nombre']}|{voto_data['candidato_apellido_paterno']}|{voto_data['candidato_apellido_materno']}"
                candidato = candidatos_dict.get(key)

                if not candidato:
                    self.stdout.write(
                        self.style.WARNING(f"Candidato no encontrado: {key}")
                    )
                    continue

                cantidad_votos = voto_data.get("cantidad", 0)

                # Crear usuarios ficticios y sus votos
                for i in range(cantidad_votos):
                    # DNI único ficticio (8 caracteres máximo)
                    dni = f"{usuario_counter:08d}"

                    # Crear usuario ficticio
                    usuario, _ = Usuario.objects.get_or_create(
                        dni=dni,
                        defaults={
                            "nombre": "Votante",
                            "apellido_paterno": "Ficticio",
                            "apellido_materno": str(usuario_counter),
                            "rol": "votante",
                            "region": candidato.region
                            if candidato.cargo.nombre_cargo == "Diputado"
                            else regiones["Lima"],
                        },
                    )

                    # Crear voto
                    try:
                        Voto.objects.create(
                            usuario=usuario, candidato=candidato, cargo=candidato.cargo
                        )
                        votos_creados += 1
                        usuario_counter += 1
                    except Exception as e:
                        self.stdout.write(
                            self.style.WARNING(f"Error creando voto: {e}")
                        )

                if cantidad_votos > 0:
                    self.stdout.write(
                        f"  ✓ {cantidad_votos} votos para {candidato.get_full_name()}"
                    )

            self.stdout.write(
                self.style.SUCCESS(f"\nTotal de votos creados: {votos_creados}")
            )

        # Resumen final
        self.stdout.write(self.style.SUCCESS("\n Datos cargados exitosamente!"))
        self.stdout.write("\nResumen:")
        self.stdout.write(f"  - Regiones: {Region.objects.count()}")
        self.stdout.write(f"  - Cargos: {Cargo.objects.count()}")
        self.stdout.write(f"  - Partidos: {Partido.objects.count()}")
        self.stdout.write(f"  - Candidatos: {Candidato.objects.count()}")
        self.stdout.write(
            f"    • Presidentes: {Candidato.objects.filter(cargo__nombre_cargo='Presidente').count()}"
        )
        self.stdout.write(
            f"    • Senadores: {Candidato.objects.filter(cargo__nombre_cargo='Senador').count()}"
        )
        self.stdout.write(
            f"    • Diputados: {Candidato.objects.filter(cargo__nombre_cargo='Diputado').count()}"
        )
        self.stdout.write(f"  - Antecedentes: {Antecedente.objects.count()}")

        if with_votes:
            self.stdout.write(f"  - Votos registrados: {Voto.objects.count()}")
            self.stdout.write(
                f"  - Usuarios votantes: {Usuario.objects.filter(rol='votante').count()}"
            )

        self.stdout.write("")
