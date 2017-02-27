package com.almende.askfast.register;


import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Freeware Sys on 8/20/2016.
 */
public interface ASKFastAPI {
    static final String BASE_URL = "https://sandbox.ask-fast.com/";

    @GET("index")
    Call<Response> index(
            @Query("name") String name,
            @Query("age") String age);

    @GET("user_exists")
    Call<Result> UserCheck(
            @Query("username") String username);

    @GET("register")
    Call<Result> Register(
            @Query("email") String email,
            @Query("language") String language,
            @Query("name") String name,
            @Query("password") String password,
            @Query("phone") String phone,
            @Query("username") String username,
            @Query("verification") String verification
    );

    @GET("register_verify")
    Call<Result> RegisterVerify(
            @Query("code") String code);

    @GET("login")
    Call<Result> Login(
            @Query("password") String password,
            @Query("username") String username);

    @GET("key")
    Call<Result> Key();

    @POST("keyserver/token")
    Call<Result> SendToken(
            @Query("client_id") String clientid,
            @Query("grant_type") String granttype,
            @Query("refresh_token") String refreshtoken,
            @Query("client_secret") String clientsecret
    );
}
