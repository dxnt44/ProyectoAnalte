package com.example.proyecto

import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Field

interface ApiService {

    @FormUrlEncoded
    @POST("http://192.168.100.164/analte/login.php")
    fun login(
        @Field("accion") accion: String,
        @Field("correo") correo: String,
        @Field("contrasena") contrasena: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("http://192.168.100.164/analte/registro.php")
    fun registrarUsuario(
        @Field("accion") accion: String,
        @Field("nombre") nombre: String,
        @Field("correo") correo: String,
        @Field("contrasena") contrasena: String
    ): Call<RegistroResponse>

    @FormUrlEncoded
    @POST("agregar_libro.php") // Ruta del archivo PHP
    fun agregarLibro(
        @Field("id_usuario") idUsuario: Int,
        @Field("titulo") titulo: String,
        @Field("autor") autor: String,
        @Field("genero") genero: String,
        @Field("paginas_totales") paginasTotales: Int,
        @Field("pagina_actual") paginaActual: Int,
        @Field("calificacion") calificacion: Int,
        @Field("portada") portada: String
    ): Call<Respuesta>

    @FormUrlEncoded
    @POST("obtener_estadisticas.php")
    fun obtenerEstadisticas(
        @Field("id_usuario") idUsuario: Int
    ): Call<EstadisticasResponse>


}
