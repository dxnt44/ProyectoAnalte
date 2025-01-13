package com.example.proyecto

import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Field

interface ApiService {

    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/login.php")
    @POST("http://192.168.100.164/analte/login.php")
    fun login(
        @Field("accion") accion: String,
        @Field("correo") correo: String,
        @Field("contrasena") contrasena: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/registro.php")
    @POST("http://192.168.100.164/analte/registro.php")
    fun registrarUsuario(
        @Field("accion") accion: String,
        @Field("nombre") nombre: String,
        @Field("correo") correo: String,
        @Field("contrasena") contrasena: String
    ): Call<RegistroResponse>

    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/agregar_libro.php")
    @POST("http://192.168.100.164/analte/agregar_libro.php")
    fun agregarLibro(
        @Field("id_usuario") idUsuario: Int,
        @Field("titulo") titulo: String,
        @Field("autor") autor: String,
        @Field("genero") genero: String,
        @Field("paginas_totales") paginasTotales: Int,
        @Field("pagina_actual") paginaActual: Int,
        @Field("calificacion") calificacion: Float,
        @Field("portada") portada: String
    ): Call<Respuesta>

    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/obtener_estadisticas.php")
    @POST("http://192.168.100.164/analte/obtener_estadisticas.php")
    fun obtenerEstadisticas(
        @Field("id_usuario") idUsuario: Int
    ): Call<EstadisticasResponse>

    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/obtener_libros.php")
    @POST("http://192.168.100.164/analte/obtener_libros.php")
    fun obtenerLibros(
        @Field("id_usuario") idUsuario: Int
    ): Call<ObtenerLibrosResponse>


    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/actualizar_libro.php")
    @POST("http://192.168.100.164/analte/actualizar_libro.php")
    fun actualizarLibro(
        @Field("id") id: Int,
        @Field("titulo") titulo: String,
        @Field("autor") autor: String,
        @Field("genero") genero: String,
        @Field("paginas_totales") paginasTotales: Int,
        @Field("pagina_actual") paginaActual: Int,
        @Field("calificacion") calificacion: Float,
        @Field("portada") portada: String
    ): Call<RespuestaBase>

    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/libro_selec.php")
    @POST("http://192.168.100.164/analte/libro_selec.php")
    fun obtenerLibro(
        @Field("id_usuario") idUsuario: Int,
        @Field("id_libro") idLibro: Int
    ): Call<DetalleLibroResponse>

    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/crear_reseña.php")
    @POST("http://192.168.100.164/analte/crear_resena.php")
    fun crearResena(
        @Field("id_libro") idLibro: Int,
        @Field("id_usuario") idUsuario: Int,
        @Field("resena") resena: String
    ): Call<Respuesta>

    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/editar_reseña.php")
    @POST("http://192.168.100.164/analte/editar_resena.php")
    fun editarResena(
        @Field("id_resena") idResena: Int,
        @Field("resena") resena: String
    ): Call<Respuesta>

    @FormUrlEncoded
    //@POST("http://192.168.249.239/analte/obtener_resena.php")
    @POST("http://192.168.100.164/analte/obtener_resena.php")
    fun obtenerResena(
        @Field("id_libro") idLibro: Int,
        @Field("id_usuario") idUsuario: Int
    ): Call<ObtenerResenaResponse>

}
