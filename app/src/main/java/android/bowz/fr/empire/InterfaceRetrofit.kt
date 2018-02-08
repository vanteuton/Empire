import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//todo https://code.tutsplus.com/tutorials/getting-started-with-retrofit-2--cms-27792


//Inside the remote package, create an interface and call it SOService.
// This interface contains methods we are going to use to execute HTTP requests such as GET, POST, PUT, PATCH, and DELETE.
// For this tutorial, we are going to execute a GET request.

interface SOService {

    @get:GET("/answers?order=desc&sort=activity&site=stackoverflow")
    val answers: Call<SOAnswersResponse>

    @GET("/answers?order=desc&sort=activity&site=stackoverflow")
    fun getAnswers(@Query("tagged") tags: String): Call<SOAnswersResponse>
}

//To issue network requests to a REST API with Retrofit, we need to create an instance using the Retrofit.
// Builder class and configure it with a base URL.
//Create a new sub-package package inside the data package and name it remote. Now inside remote, create a Java class and name it RetrofitClient.
// This class will create a singleton of Retrofit. Retrofit needs a base URL to build its instance, so we will pass a URL when calling
// RetrofitClient.getClient(String baseUrl). This URL will then be used to build the instance in line 13. We are also specifying the JSON converter we need (Gson) in line 14.

object RetrofitClient {

    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
        return retrofit
    }
}



//Now are going to create a utility class. We'll name it ApiUtils. This class will have the base URL as a static variable and also provide the
// SOService interface to our application through the getSOService() static method.

object ApiUtils {

    val BASE_URL = "https://api.stackexchange.com/2.2/"

    val soService: SOService
        get() = RetrofitClient.getClient(BASE_URL).create(SOService::class.java)
}