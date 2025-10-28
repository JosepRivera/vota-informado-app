package com.rivera.votainformado.util

// sealed class -> Limita qué clases pueden heredar de ella.
// <T> -> Parámetro genérico, que puede ser de cualquier tipo.
sealed class Resource<T>(
    val data: T? = null, // data -> Representa los datos devueltos por la operación.
    val message: String? = null // message -> Guarda un mensaje de error o información adicional.
){
    class Success<T>(data: T) : Resource<T>(data) // Recibe solo los datos y llama al contructor padre pasandole esos datos.
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message) // Recibe los datos y el mensaje opcionalmente y llama al contructor padre pasandole esos datos.
    class Loading<T> : Resource<T>() // No tiene datos ni mensajes, solo indica que la operación está en proceso
}