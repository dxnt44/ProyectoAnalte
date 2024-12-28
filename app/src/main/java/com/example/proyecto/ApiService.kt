package com.example.proyecto

import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Field

interface ApiService {

    @FormUrlEncoded
    @POST("http://192.168.192.239/analte/login.php")
    fun login(
        @Field("accion") accion: String,
        @Field("correo") correo: String,
        @Field("contrasena") contrasena: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("http://192.168.192.239/analte/registro.php")
    fun registrarUsuario(
        @Field("accion") accion: String,
        @Field("nombre") nombre: String,
        @Field("correo") correo: String,
        @Field("contrasena") contrasena: String
    ): Call<RegistroResponse>
}
