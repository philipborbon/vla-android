package com.vla.sksu.app.manager

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.vla.sksu.app.BuildConfig
import com.vla.sksu.app.data.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ServerService {
    companion object {
        private lateinit var instance: ServerService

        const val HTTP_LOCKED = 423

        fun getInstance(context: Context): ServerService {
            if ( !(::instance.isInitialized) ) {
                val authorizationStore = AuthorizationStore.getInstance(context)

                val gsonBuilder = GsonBuilder()
                    .setLenient()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")


                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor {
                    val request = it.request()
                        .newBuilder()
                        .addHeader("Accept", "application/json")

                    authorizationStore.token?.let {
                        val authorization = authorizationStore.getAuthorization()
                        request.addHeader("Authorization", authorization.getAccessToken())
                    }

                    it.proceed(request.build())
                }

                if (BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.level = HttpLoggingInterceptor.Level.BODY
                    httpClient.addInterceptor(logging)
                }

                val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gsonBuilder.create()))
                    .baseUrl(BuildConfig.SERVER_URL)
                    .client(httpClient.build())
                    .build()

                instance = retrofit.create(ServerService::class.java)
            }

            return instance
        }
    }

    @FormUrlEncoded
    @POST("token")
    fun login(@Field("library_id") username: String?, @Field("password") password: String?): Call<String>

    @DELETE("revoke-token")
    fun logout(): Call<Void>

    @GET("whoami")
    fun whoAmI(): Call<User>

    @FormUrlEncoded
    @POST("user/push-token")
    fun updatePushToken(@Field("push_token") token: String?): Call<Void>

    @DELETE("user/push-token")
    fun clearPushToken(): Call<Void>

    // --

    @GET("categories")
    fun getCategories(): Call<ArrayList<Category>>

    @GET("categories/{id}")
    fun getSubCategories(@Path("id") categoryId: Int): Call<ArrayList<Category>>

    @GET("categories/{categoryId}/books")
    fun getBooks(@Path("categoryId") categoryId: Int): Call<ArrayList<Book>>

    @GET("books")
    fun search(@Query("keyword") keyword: String): Call<ArrayList<Book>>

    @GET("books/{id}")
    fun getBook(@Path("id") id: Int): Call<ResponseData<Book, BookMeta>>

    @POST("books/{id}/request")
    fun borrow(@Path("id") id: Int): Call<Void>

    @POST("books/{id}/notify")
    fun notify(@Path("id") id: Int): Call<Void>

    @GET("histories")
    fun getHistories(@Query("show") show: String?): Call<ArrayList<History>>

    @GET("histories/{id}")
    fun getHistory(@Path("id") id: Int): Call<History>

    @GET("histories/overview")
    fun getHistoryOverview(): Call<HistoryOverview>
}