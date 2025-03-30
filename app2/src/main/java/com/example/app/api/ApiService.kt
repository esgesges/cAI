import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("read-date") // Use GET and pass date as a query parameter
    fun getEventsByDate(@Query("date") date: String): Call<ResponseBody>?

    @POST("manage")
    fun manage(@Body requestBody: RequestBody): Call<ResponseBody>? // Add @Body here
}
