import requests  # type: ignore
from django.conf import settings
from rest_framework.exceptions import ValidationError, APIException


class ReniecService:
    """
    Servicio para consultar la API de RENIEC (terceros).
    Maneja errores y devuelve datos normalizados.
    """

    @staticmethod
    def consultar_dni(dni):
        """
        Consulta el DNI en la API de RENIEC.

        Args:
            dni (str): Número de DNI a consultar (8 dígitos)

        Returns:
            dict: Datos del ciudadano normalizados
            {
                'nombre': 'ROXANA KARINA',
                'apellido_paterno': 'DELGADO',
                'apellido_materno': 'CUELLAR',
                'dni': '46027896'
            }

        Raises:
            ValidationError: Si el DNI no existe o es inválido
            APIException: Si hay error en el servicio de RENIEC
        """
        # Validar formato de DNI
        if not dni or len(dni) != 8 or not dni.isdigit():
            raise ValidationError({"dni": "El DNI debe tener 8 dígitos numéricos"})

        try:
            # URL de la API de RENIEC (ejemplo: https://api.decolecta.com/v1/reniec/dni?numero=46027896)
            url = f"{settings.RENIEC_API_URL}?numero={dni}"

            # Headers si la API requiere token de autenticación
            headers = {}
            if settings.RENIEC_API_TOKEN:
                headers["Authorization"] = f"Bearer {settings.RENIEC_API_TOKEN}"

            # Hacer petición GET a la API (timeout de 10 segundos)
            response = requests.get(url, headers=headers, timeout=10)

            # Manejar respuestas según código HTTP
            if response.status_code == 200:
                data = response.json()

                # Normalizar datos de la API al formato de nuestro modelo
                return {
                    "nombre": data.get("first_name", "").strip(),
                    "apellido_paterno": data.get("first_last_name", "").strip(),
                    "apellido_materno": data.get("second_last_name", "").strip(),
                    "dni": data.get("document_number", dni).strip(),
                }

            elif response.status_code == 400:
                # DNI no existe en RENIEC
                raise ValidationError(
                    {"dni": "El DNI ingresado no se encuentra registrado en RENIEC"}
                )

            elif response.status_code == 404:
                raise ValidationError({"dni": "DNI no encontrado"})

            else:
                # Otro error del servidor de RENIEC
                raise APIException(
                    f"Error al consultar RENIEC (código {response.status_code})",
                    code=503,
                )

        except requests.exceptions.Timeout:
            # La API de RENIEC tardó más de 10 segundos
            raise APIException(
                "El servicio de RENIEC no responde. Intenta nuevamente en unos minutos.",
                code=503,
            )

        except requests.exceptions.ConnectionError:
            # No hay conexión a internet o el servicio está caído
            raise APIException(
                "No se pudo conectar con el servicio de RENIEC. Verifica tu conexión.",
                code=503,
            )

        except requests.exceptions.RequestException as e:
            # Cualquier otro error de requests
            raise APIException(f"Error al consultar RENIEC: {str(e)}", code=503)
