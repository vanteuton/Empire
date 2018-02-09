package android.bowz.fr.empire

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


//todo https://code.tutsplus.com/tutorials/getting-started-with-retrofit-2--cms-27792


object RetrofitClient {

    private var retrofit: Retrofit? = null

    val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit!!
    }
}

interface EmpiresService {

    @get:GET("login?")
    val answers: Call<ReturnMessage>

    @GET("login?")
    fun getLogin(@Query("password") password: String,
                 @Query("email") email: String): Call<ReturnMessage>

    @GET("world")
    fun getAnswers(@Header("Authorization") Authorization: String): Call<ReturnMessage>
}


object ApiUtils {

    val BASE_URL = "http://lfbn-1-9328-179.w86-237.abo.wanadoo.fr/api/"

    val empiresService: EmpiresService
        get() = RetrofitClient.getClient(BASE_URL).create(EmpiresService::class.java)
}
